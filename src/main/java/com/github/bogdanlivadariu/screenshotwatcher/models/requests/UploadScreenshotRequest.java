package com.github.bogdanlivadariu.screenshotwatcher.models.requests;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class UploadScreenshotRequest {

    public String testName;

    public String testBrowser;

    public String description;

    public String imageData;

    public List<Rectangle> ignoreZones;

    public UploadScreenshotRequest(String testName, String testBrowser, String description, String imageData) {
        this.testName = testName;
        this.testBrowser = testBrowser;
        this.description = description;
        this.imageData = imageData;
        this.ignoreZones = new ArrayList<>();
    }

    public UploadScreenshotRequest(String testName, String testBrowser, String description, String imageData,
        List<Rectangle> ignoreZones) {
        this.testName = testName;
        this.testBrowser = testBrowser;
        this.description = description;
        this.imageData = imageData;
        this.ignoreZones = ignoreZones;
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

    public String getImageData() {
        return imageData;
    }
}
