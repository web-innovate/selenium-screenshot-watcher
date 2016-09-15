package com.github.bogdanlivadariu.screenshotwatcher.endpoints;

import static com.github.bogdanlivadariu.screenshotwatcher.db.DBConnectors.BASE_IMAGES;
import static com.github.bogdanlivadariu.screenshotwatcher.db.DBConnectors.GFS_DIFF_PHOTOS;

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
import com.github.bogdanlivadariu.screenshotwatcher.ScreenshotProcessing;
import com.github.bogdanlivadariu.screenshotwatcher.models.ProcessedScreenshots;
import com.github.bogdanlivadariu.screenshotwatcher.util.EndpointUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

/**
 * This endpoint is being used to review two screenshots(base/new).
 */
@Path("review/{baseScreenshotId}/{newScreenshotId}")
public class ReviewScreenshots {

    private final String IMAGE_ENDPOINT = String.format("%simage/photo/", Main.getBaseUri());

    private final String IMAGE_DIFF_ENDPOINT = String.format("%simage/diff_photos/", Main.getBaseUri());

    private final String IMAGE_PERFECT_MATCH =
        "http://cdn2.hubspot.net/hub/137629/file-2411625209-jpg/JobSeeker_Blog/Perfect_Match_Puzzle_Piece.jpg";

    private Logger logger = LogManager.getLogger(ReviewScreenshots.class);

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String review(@PathParam("baseScreenshotId") String baseScreenShotId,
        @PathParam("newScreenshotId") String newScreenshotId, @Context Request request) throws IOException {
        EndpointUtil.printClientInfo(request);

        ProcessedScreenshots processedScreenshots =
            ScreenshotProcessing.processScreenshots(baseScreenShotId, newScreenshotId);

        String imgDiffLocation = "";
        boolean perfectMatch = false;

        // check if images were a perfect match, this means the id of the tmp image should be 200
        if (processedScreenshots.getIdOfTheDiffImage().equals("200")) {
            imgDiffLocation = IMAGE_PERFECT_MATCH;
            perfectMatch = true;
        } else {
            imgDiffLocation = IMAGE_DIFF_ENDPOINT + processedScreenshots.getIdOfTheDiffImage();
        }

        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
        Template template = ve.getTemplate("html/review/review.vm");

        VelocityContext context = new VelocityContext();
        context.put("urlLinkBase", IMAGE_ENDPOINT + baseScreenShotId);
        context.put("urlLinkTmp", IMAGE_ENDPOINT + newScreenshotId);
        context.put("urlLinkDiff", imgDiffLocation);
        context.put("screenshotsHaveBeenReviewed", processedScreenshots.isScreenshotsHaveBeenReviewed());
        if (perfectMatch) {
            context.put("reviewMessage", "These images are a perfect match");
        } else {
            if (processedScreenshots.isScreenshotsHaveBeenReviewed()) {
                context.put("reviewMessage", "These images have already been reviewed !");
            } else {
                context.put("reviewMessage", "As Expected ?");
            }
        }

        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        return writer.toString();
    }

    @Path("/validate")
    @POST
    public void validate(@PathParam("baseScreenshotId") String baseScreenshotId,
        @PathParam("newScreenshotId") String newScreenshotId) {
        logger.info(String.format("Validating: baseScreenshotId: '%s', newScreenshotId: ''%s",
            baseScreenshotId, newScreenshotId));

        String diffImageFileName = String.format("%s|%s|%s", baseScreenshotId, newScreenshotId, "-differences");
        ObjectId newBaseScreenshotId = new ObjectId(newScreenshotId);

        // pull the diff image from the DB, mark the field for processed screenshots to true
        BasicDBObject diffImageQuery = new BasicDBObject();
        diffImageQuery.put("filename", diffImageFileName);

        GridFSDBFile diffFile = GFS_DIFF_PHOTOS.find(diffImageQuery).iterator().next();
        diffFile.put("screenshotsHaveBeenReviewed", true);
        diffFile.save();

        // update the image id of the base screenshot with the id of the new screenshot
        BasicDBObject baseQuery = new BasicDBObject();
        baseQuery.put("imageId", new ObjectId(baseScreenshotId));

        DBObject baseObject = BASE_IMAGES.find(baseQuery).next();
        baseObject.put("imageId", newBaseScreenshotId);
        BASE_IMAGES.save(baseObject);
    }
}
