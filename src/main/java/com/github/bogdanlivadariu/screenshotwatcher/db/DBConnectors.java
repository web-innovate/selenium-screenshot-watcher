package com.github.bogdanlivadariu.screenshotwatcher.db;

import com.github.bogdanlivadariu.screenshotwatcher.configuration.ScreenshotWatcherConfiguration;
import com.github.bogdanlivadariu.screenshotwatcher.models.config.ScreenshotWatcherConfigurationModel;
import com.mongodb.*;
import com.mongodb.gridfs.GridFS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.UnknownHostException;
import java.util.Arrays;

public class DBConnectors {

    private static final ScreenshotWatcherConfigurationModel CONFIG = ScreenshotWatcherConfiguration.getConfig();

    private final static MongoCredential CREDENTIALS = MongoCredential
        .createCredential(
            CONFIG.getMongoDBUserName(),
            CONFIG.getMongoDB(),
            CONFIG.getMongoDBPassword().toCharArray());

    private static Logger logger = LogManager.getLogger(DBConnectors.class);

    // TODO: Find a better way of initializing this
    public static MongoClient mongoClient = connect();

    private static final DB DB = mongoClient.getDB(CONFIG.getMongoDB());

    public static final DBCollection BASE_IMAGES = mongoClient.getDB(CONFIG.getMongoDB()).getCollection("base_images");

    public static final DBCollection TMP_IMAGES = mongoClient.getDB(CONFIG.getMongoDB()).getCollection("tmp_images");

    public static final GridFS GFS_PHOTO = new GridFS(DB, "photo");

    public static final GridFS GFS_DIFF_PHOTOS = new GridFS(DB, "diff_photos");

    private static MongoClient connect() {
        logger.info("Connecting to the Mongo Database");
        MongoClient client = null;
        try {
            client =
                new MongoClient(new ServerAddress(CONFIG.getMongoDBHost(), CONFIG.getMongoDBPort()),
                    Arrays.asList(CREDENTIALS));
        } catch (UnknownHostException e) {
            logger.info("There was a problem with the connection to the DB", e.getMessage());
            //            throw new RuntimeException("There was a problem with the connection to the DB");
        } catch (Exception e) {
            logger.info("DB connection was not established");
        }

        if (client != null) {
            logger.info("Connected to mongo instance");
        }

        return client;
    }
}
