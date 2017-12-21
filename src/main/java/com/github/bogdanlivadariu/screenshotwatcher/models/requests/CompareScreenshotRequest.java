package com.github.bogdanlivadariu.screenshotwatcher.models.requests;

import com.github.bogdanlivadariu.screenshotwatcher.models.BaseScreenshotModel;
import org.bson.types.ObjectId;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CompareScreenshotRequest {

    public ObjectId _id;

    public String testName;

    public String testBrowser;

    public String description;

    public ObjectId imageId;

    public List<Rectangle> ignoreZones;

    public CompareScreenshotRequest(BaseScreenshotModel object) {
        this._id = object.getId();
        this.testName = object.getTestName();
        this.testBrowser = object.getTestBrowser();
        this.description = object.getDescription();
        this.imageId = new ObjectId(object.getImageId());
        this.ignoreZones = object.getIgnoreZones();
    }

    public ObjectId getId() {
        return _id;
    }

    public String getTestName() {
        return testName;
    }

    public String getTestBrowser() {
        return testBrowser;
    }

    public String getDescription() {
        return description;
    }

    public ObjectId getImageId() {
        return imageId;
    }

    public List<Rectangle> getIgnoreZones() {
        return ignoreZones == null ? new ArrayList<>() : ignoreZones;
    }
}
