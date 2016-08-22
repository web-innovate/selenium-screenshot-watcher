package com.github.bogdanlivadariu.screenshotwatcher.endpoints;

import static com.github.bogdanlivadariu.screenshotwatcher.db.DBConnectors.GFS_DIFF_PHOTOS;
import static com.github.bogdanlivadariu.screenshotwatcher.db.DBConnectors.GFS_PHOTO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.glassfish.grizzly.http.server.Request;

import com.github.bogdanlivadariu.screenshotwatcher.util.EndpointUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.gridfs.GridFSDBFile;

@Path("image/{collection}/{_id}")
public class RetrieveScreenshot {
    @GET
    @Produces("image/png")
    public Response getScreenshotFromDb(@PathParam("collection") String collection, @PathParam("_id") String imgId,
        @Context Request request) {
        EndpointUtil.printClientInfo(request);
        BasicDBObject screenshotQuery = new BasicDBObject();
        screenshotQuery.put("_id", new ObjectId(imgId));
        GridFSDBFile screenshotFound = null;
        switch (collection.toLowerCase()) {
            case "photo":
                screenshotFound = GFS_PHOTO.findOne(screenshotQuery);
                break;
            case "diff_photos":
                screenshotFound = GFS_DIFF_PHOTOS.findOne(screenshotQuery);
                break;
        }
        return Response.ok().entity(screenshotFound.getInputStream()).build();
    }
}
