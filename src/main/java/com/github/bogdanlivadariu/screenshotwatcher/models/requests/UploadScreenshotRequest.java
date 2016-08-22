package com.github.bogdanlivadariu.screenshotwatcher.models.requests;

public class UploadScreenshotRequest {

    public String testName;

    public String testBrowser;

    public String description;

    public String imageData;

    public UploadScreenshotRequest(String testName, String testBrowser, String description, String imageData) {
        this.testName = testName;
        this.testBrowser = testBrowser;
        this.description = description;
        this.imageData = imageData;
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
