package com.github.bogdanlivadariu.screenshotwatcher.endpoints;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.glassfish.grizzly.http.server.Request;

import com.github.bogdanlivadariu.screenshotwatcher.Main;
import com.github.bogdanlivadariu.screenshotwatcher.util.EndpointUtil;

@Path("/stop")
public class ServerStopEndpoint {
    @POST
    public void post(@Context Request request) throws InterruptedException {
        EndpointUtil.printClientInfo(request);
        Main.SERVER.shutdownNow();
    }
}
