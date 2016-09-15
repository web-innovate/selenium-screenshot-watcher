package com.github.bogdanlivadariu.screenshotwatcher.models;

import java.awt.Rectangle;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
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

    public static final String IGNORE_ZONES = "ignoreZones";

    public final List<Rectangle> IGNORE_LIST_RECTANGLE;

    public BaseScreenshotModel(String testName, String testBrowser, String description, ObjectId imageId,
        List<Rectangle> ignoreZones) {
        IGNORE_LIST_RECTANGLE = ignoreZones;
        put(_ID, new ObjectId());
        put(TEST_NAME, testName);
        put(TEST_BROWSER, testBrowser);
        put(DESCRIPTION, description);
        put(IMAGE_ID, imageId);
        put(IGNORE_ZONES, new Gson().toJson(IGNORE_LIST_RECTANGLE));
    }

    public BaseScreenshotModel(String testName, String testBrowser, String description, ObjectId imageId) {
        IGNORE_LIST_RECTANGLE = new ArrayList<>();
        put(_ID, new ObjectId());
        put(TEST_NAME, testName);
        put(TEST_BROWSER, testBrowser);
        put(DESCRIPTION, description);
        put(IMAGE_ID, imageId);
        put(IGNORE_ZONES, IGNORE_LIST_RECTANGLE);
    }

    public BaseScreenshotModel(BasicDBObject object) {
        IGNORE_LIST_RECTANGLE = null;
        put(_ID, object.get(_ID));
        put(TEST_NAME, object.get(TEST_NAME));
        put(TEST_BROWSER, object.get(TEST_BROWSER));
        put(DESCRIPTION, object.get(DESCRIPTION));
        put(IMAGE_ID, object.get(IMAGE_ID));
        put(IGNORE_ZONES, object.get(IGNORE_ZONES));
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

    @SuppressWarnings("serial")
    public List<Rectangle> getIgnoreZones() {
        Type type = new TypeToken<List<Rectangle>>() {
        }.getType();
        return new Gson().fromJson(getString(IGNORE_ZONES), type);
    }
}
