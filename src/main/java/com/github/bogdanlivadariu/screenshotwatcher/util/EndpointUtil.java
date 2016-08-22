package com.github.bogdanlivadariu.screenshotwatcher.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.http.server.Request;

/**
 * Contains either print lines used by the endpoints or to perform different actions.
 * This should evolve once the support for the server gets extended.
 */
public class EndpointUtil {

    private static Logger logger = LogManager.getLogger(EndpointUtil.class);

    public static void printClientInfo(Request request) {
        logger.info(
            "REQUEST from: " + "Remote host: " + request.getRemoteHost() + "\n"
                + "Request URL: " + request.getRequest() + "\n");

    }
}
