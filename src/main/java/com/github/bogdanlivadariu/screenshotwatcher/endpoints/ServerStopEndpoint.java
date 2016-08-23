package com.github.bogdanlivadariu.screenshotwatcher.endpoints;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.Request;

import com.github.bogdanlivadariu.screenshotwatcher.Main;
import com.github.bogdanlivadariu.screenshotwatcher.util.EndpointUtil;
import com.github.bogdanlivadariu.screenshotwatcher.util.EnvironmentUtil;
import com.mongodb.util.JSON;

@Path("/stop")
public class ServerStopEndpoint {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(@Context Request request) throws InterruptedException {
        EndpointUtil.printClientInfo(request);
        if ("heroku".equalsIgnoreCase(EnvironmentUtil.getEnvironmentName())) {
            return Response.status(403)
                .entity(JSON.serialize("App NOT is going down, just because you want so :)")).build();
        }
        Main.SERVER.shutdownNow();
        return null;
    }
}
