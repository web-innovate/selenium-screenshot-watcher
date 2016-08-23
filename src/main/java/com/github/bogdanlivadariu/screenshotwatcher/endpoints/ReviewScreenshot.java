package com.github.bogdanlivadariu.screenshotwatcher.endpoints;

import static com.github.bogdanlivadariu.screenshotwatcher.db.DBConnectors.BASE_IMAGES;
import static com.github.bogdanlivadariu.screenshotwatcher.db.DBConnectors.TMP_IMAGES;

import java.io.IOException;
import java.io.StringWriter;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.bson.types.ObjectId;
import org.glassfish.grizzly.http.server.Request;

import com.github.bogdanlivadariu.screenshotwatcher.Main;
import com.github.bogdanlivadariu.screenshotwatcher.models.BaseScreenshotModel;
import com.github.bogdanlivadariu.screenshotwatcher.util.EndpointUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * This endpoint is being used in order to review the new screenshot when a base one is not found.
 */
@Path("review-single/{newScreenshotId}")
public class ReviewScreenshot {

    private final String IMAGE_ENDPOINT = String.format("%simage/photo/", Main.getBaseUri());

    private Logger logger = LogManager.getLogger(ReviewScreenshot.class);

    private final String IMG_PLACEHOLDER = "http://riflingmachinemethods.com/wp-content/uploads/placeholder.gif";

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String review(@PathParam("newScreenshotId") String newScreenshotId, @Context Request request)
        throws IOException {
        EndpointUtil.printClientInfo(request);

        // pull information about the tmp screenshot
        BasicDBObject tmpQuery = new BasicDBObject();
        tmpQuery.put("imageId", new ObjectId(newScreenshotId));

        DBObject tmpRecord = TMP_IMAGES.find(tmpQuery).next();
        Object reviewedObject = tmpRecord.get("screenshotsHaveBeenReviewed");
        boolean tmpRecordReviewed = false;
        if (reviewedObject != null) {
            tmpRecordReviewed = Boolean.parseBoolean(tmpRecord.get("screenshotsHaveBeenReviewed").toString());
        }

        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
        Template template = ve.getTemplate("html/review/review.vm");

        VelocityContext context = new VelocityContext();
        context.put("urlLinkBase", IMG_PLACEHOLDER);
        context.put("urlLinkTmp", IMAGE_ENDPOINT + newScreenshotId);
        context.put("urlLinkDiff", IMG_PLACEHOLDER);
        context.put("screenshotsHaveBeenReviewed", tmpRecordReviewed);
        if (tmpRecordReviewed) {
            context.put("reviewMessage", "These images have already been reviewed !");
        } else {
            context.put("reviewMessage", "As Expected ?");
        }

        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        return writer.toString();
    }

    @Path("/validate")
    @POST
    public void validate(@PathParam("newScreenshotId") String newScreenshotId) {
        logger.info(
            String.format("Validating newScreenshotId '%s' to be added into base images collection", newScreenshotId));
        // pull information from the tmp collection and add them to the base collection
        BasicDBObject tmpQuery = new BasicDBObject();
        tmpQuery.put("imageId", new ObjectId(newScreenshotId));

        DBObject tmpRecord = TMP_IMAGES.find(tmpQuery).next();
        String testName = tmpRecord.get("testName").toString();
        String testBrowser = tmpRecord.get("testBrowser").toString();
        String description = tmpRecord.get("description").toString();
        ObjectId imageId = new ObjectId(newScreenshotId);

        BaseScreenshotModel baseScreenshot = new BaseScreenshotModel(testName, testBrowser, description, imageId);
        BASE_IMAGES.save(baseScreenshot);

        tmpRecord.put("screenshotsHaveBeenReviewed", true);
        TMP_IMAGES.save(tmpRecord);
    }
}
