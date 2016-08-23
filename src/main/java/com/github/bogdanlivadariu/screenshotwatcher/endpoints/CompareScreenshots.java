package com.github.bogdanlivadariu.screenshotwatcher.endpoints;

import static com.github.bogdanlivadariu.screenshotwatcher.db.DBConnectors.BASE_IMAGES;
import static com.github.bogdanlivadariu.screenshotwatcher.db.DBConnectors.GFS_PHOTO;
import static com.github.bogdanlivadariu.screenshotwatcher.models.BaseScreenshotModel.DESCRIPTION;
import static com.github.bogdanlivadariu.screenshotwatcher.models.BaseScreenshotModel.TEST_BROWSER;
import static com.github.bogdanlivadariu.screenshotwatcher.models.BaseScreenshotModel.TEST_NAME;
import static com.github.bogdanlivadariu.screenshotwatcher.models.BaseScreenshotModel._ID;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.glassfish.grizzly.http.server.Request;

import com.github.bogdanlivadariu.screenshotwatcher.Main;
import com.github.bogdanlivadariu.screenshotwatcher.ScreenshotProcessing;
import com.github.bogdanlivadariu.screenshotwatcher.models.requests.CompareScreenshotRequest;
import com.github.bogdanlivadariu.screenshotwatcher.models.response.CompareScreenshotsResponse;
import com.github.bogdanlivadariu.screenshotwatcher.models.response.ScreenshotProcessingResponse;
import com.github.bogdanlivadariu.screenshotwatcher.util.EndpointUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

@Path("compare")
public class CompareScreenshots {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doCoolMagicInside(String jsonReq, @Context Request request) throws IOException {
        EndpointUtil.printClientInfo(request);
        JsonDeserializer<ObjectId> objectIdDeserializer = new JsonDeserializer<ObjectId>() {
            @Override
            public ObjectId deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
                return new ObjectId(je.getAsJsonObject().get("$oid").getAsString());
            }
        };
        Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, objectIdDeserializer).create();

        boolean baseImageFound = false;
        CompareScreenshotRequest itemToCompare = gson.fromJson(jsonReq, CompareScreenshotRequest.class);
        DBObject base = null;
        ObjectId baseImageObjectId = null;
        GridFSDBFile baseScreenshot = null;
        File baseFile = null;
        File tmpFile = new File("tmp");
        String reviewLink = "";
        ScreenshotProcessingResponse processedResponse = null;
        CompareScreenshotsResponse compareResponse = null;

        // assuming this image has been uploaded, create a search for it in the tmp & base images table
        BasicDBObject baseQuery = new BasicDBObject();
        baseQuery.put(TEST_NAME, itemToCompare.getTestName());
        baseQuery.put(TEST_BROWSER, itemToCompare.getTestBrowser());
        baseQuery.put(DESCRIPTION, itemToCompare.getDescription());

        BasicDBObject newScreenshotQuery = new BasicDBObject();
        newScreenshotQuery.put(_ID, itemToCompare.getImageId());

        BasicDBObject baseScreenshotQuery = new BasicDBObject();

        // pull the base image object
        DBCursor baseCursor = BASE_IMAGES.find(baseQuery);
        if (baseCursor.hasNext()) {
            baseImageFound = true;
            base = baseCursor.next();
            baseImageObjectId = new ObjectId(base.get("imageId").toString());
            baseScreenshotQuery.put(_ID, baseImageObjectId);
            baseScreenshot = GFS_PHOTO.find(baseScreenshotQuery).iterator().next();
            baseFile = new File("base");
            baseScreenshot.writeTo(baseFile);
        } else {
            // there is no base image stored for the baseQuery
        }

        GridFSDBFile newScreenshot = GFS_PHOTO.find(newScreenshotQuery).iterator().next();
        newScreenshot.writeTo(tmpFile);

        if (baseImageFound) {
            reviewLink =
                Main.getBaseUri() + "review/" + baseScreenshot.getId().toString() + "/"
                    + newScreenshot.getId().toString();
            processedResponse = ScreenshotProcessing.processScreenshots(baseFile, tmpFile);
            compareResponse =
                new CompareScreenshotsResponse(processedResponse.getStatus(), reviewLink);
        } else {
            reviewLink =
                Main.getBaseUri() + "review-single/" + newScreenshot.getId().toString();
            compareResponse = new CompareScreenshotsResponse(false, reviewLink);
        }

        return Response.ok().entity(new Gson().toJson(compareResponse, CompareScreenshotsResponse.class)).build();
    }
}
