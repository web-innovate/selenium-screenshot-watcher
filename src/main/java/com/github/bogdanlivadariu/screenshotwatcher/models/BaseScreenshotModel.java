package com.github.bogdanlivadariu.screenshotwatcher.models;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;

/**
 * Model used to upload images into the MongoDB.
 */
public class BaseScreenshotModel extends BasicDBObject {

    private static final long serialVersionUID = -468472284865143062L;

    public static final String _ID = "_id";

    public static final String TEST_NAME = "testName";

    public static final String TEST_BROWSER = "testBrowser";

    public static final String DESCRIPTION = "description";

    public static final String IMAGE_ID = "imageId";

    public BaseScreenshotModel(String testName, String testBrowser, String description, ObjectId imageId) {
        put(_ID, new ObjectId());
        put(TEST_NAME, testName);
        put(TEST_BROWSER, testBrowser);
        put(DESCRIPTION, description);
        put(IMAGE_ID, imageId);
    }

    public BaseScreenshotModel(BasicDBObject object) {
        put(_ID, object.get(_ID));
        put(TEST_NAME, object.get(TEST_NAME));
        put(TEST_BROWSER, object.get(TEST_BROWSER));
        put(DESCRIPTION, object.get(DESCRIPTION));
        put(IMAGE_ID, object.get(IMAGE_ID));
    }

    public ObjectId getId() {
        return new ObjectId(getString(_ID));
    }

    public String getTestName() {
        return getString(TEST_NAME);
    }

    public String getTestBrowser() {
        return getString(TEST_BROWSER);
    }

    public String getDescription() {
        return getString(DESCRIPTION);
    }

    public String getImageId() {
        return getString(IMAGE_ID);
    }
}
