package com.github.bogdanlivadariu.screenshotwatcher;

import java.io.IOException;
import java.net.URI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.github.bogdanlivadariu.screenshotwatcher.configuration.ScreenshotWatcherConfiguration;
import com.github.bogdanlivadariu.screenshotwatcher.db.DBConnectors;
import com.github.bogdanlivadariu.screenshotwatcher.models.config.ScreenshotWatcherConfigurationModel;

/**
 * Main class.
 */
public class Main {
    private static final ScreenshotWatcherConfigurationModel CONFIG = ScreenshotWatcherConfiguration.getConfig();

    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = CONFIG.getHostAddress() + ":" + CONFIG.getPortPreference() + "/";

    public static HttpServer SERVER = null;

    private static Logger logger = LogManager.getLogger(Main.class);

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        final ResourceConfig rc = new ResourceConfig().packages("com.github.bogdanlivadariu.screenshotwatcher");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        SERVER = startServer();
        new DBConnectors();
        logger.info(String.format("Jersey app started with WADL available at %sapplication.wadl", BASE_URI));
        logger.info(String.format("Send a empty POST request to %sstop in order to stop the server", BASE_URI));
    }
}
