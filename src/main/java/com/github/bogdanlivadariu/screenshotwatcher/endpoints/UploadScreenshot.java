package com.github.bogdanlivadariu.screenshotwatcher.endpoints;

import static com.github.bogdanlivadariu.screenshotwatcher.db.DBConnectors.GFS_PHOTO;
import static com.github.bogdanlivadariu.screenshotwatcher.db.DBConnectors.TMP_IMAGES;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.util.Base64Utils;

import com.github.bogdanlivadariu.screenshotwatcher.models.BaseScreenshotModel;
import com.github.bogdanlivadariu.screenshotwatcher.util.EndpointUtil;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSInputFile;
import com.mongodb.util.JSON;

@Path("upload")
public class UploadScreenshot {
    /*
     * JSON STRUCTURE THAT THIS ENDPOINT DIGESTS
     * {
     * "testName":"test name from post",
     * "testBrowser":"firefox",
     * "description":"outlook 2013",
     * "imageData":"Base64Encoded string"
     * }
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertImageInDb(String jsonRequest, @Context Request request) throws IOException {
        EndpointUtil.printClientInfo(request);

        DBObject json = ((DBObject) JSON.parse(jsonRequest));
        String imageData = (String) json.get("imageData");
        byte[] screenshotBytes = Base64Utils.decode(imageData);

        String testName = json.get(BaseScreenshotModel.TEST_NAME).toString();
        String testBrowser = json.get(BaseScreenshotModel.TEST_BROWSER).toString();
        String description = json.get(BaseScreenshotModel.DESCRIPTION).toString();
        List<Rectangle> re = (List<Rectangle>) json.get(BaseScreenshotModel.IGNORE_ZONES);

        File tmpFile = new File("tmpFile");
        FileUtils.writeByteArrayToFile(tmpFile, screenshotBytes);
        GridFSInputFile gfsFile = GFS_PHOTO.createFile(tmpFile);

        gfsFile.setFilename(String.format("%s|%s|%s", testName, testBrowser, description));
        gfsFile.save();
        // after the file has been saved, get the id and add it into the table of base_images
        BaseScreenshotModel up = new BaseScreenshotModel(testName, testBrowser, description,
            new ObjectId(gfsFile.getId().toString()), re);

        TMP_IMAGES.save(up);
        tmpFile.delete();
        return Response.ok().entity(JSON.serialize(up)).build();
    }
}
