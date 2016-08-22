package com.github.bogdanlivadariu.screenshotwatcher.models;

/**
 * Model used to pull images from MongoDB.
 * This acts as a where clause.
 */
public class ScreenshotCompareModel {

    public String testName;

    public String testBrowser;

    public String description;

    public String getTestName() {
        return testName;
    }

    public String getTestBrowser() {
        return testBrowser;
    }

    public String getDescription() {
        return description;
    }

}
