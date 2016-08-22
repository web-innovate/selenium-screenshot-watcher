package com.github.bogdanlivadariu.screenshotwatcher.db;

import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.bogdanlivadariu.screenshotwatcher.configuration.ScreenshotWatcherConfiguration;
import com.github.bogdanlivadariu.screenshotwatcher.models.config.ScreenshotWatcherConfigurationModel;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.gridfs.GridFS;

public class DBConnectors {

    private static Logger logger = LogManager.getLogger(DBConnectors.class);

    private static final ScreenshotWatcherConfigurationModel CONFIG = ScreenshotWatcherConfiguration.getConfig();

    private final static MongoCredential CREDENTIALS = MongoCredential
        .createCredential(
            CONFIG.getMongoDBUserName(),
            CONFIG.getMongoDB(),
            CONFIG.getMongoDBPassword().toCharArray());

    // TODO: Find a better way of initializing this
    public static MongoClient mongoClient = connect();

    public static final DBCollection BASE_IMAGES = mongoClient.getDB(CONFIG.getMongoDB()).getCollection("base_images");

    public static final DBCollection TMP_IMAGES = mongoClient.getDB(CONFIG.getMongoDB()).getCollection("tmp_images");

    private static final DB DB = mongoClient.getDB(CONFIG.getMongoDB());

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
            throw new RuntimeException("There was a problem with the connection to the DB");
        }
        return client;
    }
}
