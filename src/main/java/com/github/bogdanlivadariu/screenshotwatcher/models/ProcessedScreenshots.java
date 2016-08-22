package com.github.bogdanlivadariu.screenshotwatcher.models;

public class ProcessedScreenshots {

    private boolean screenshotsHaveBeenReviewed;

    private String idOfTheDiffImage;

    public ProcessedScreenshots(boolean screenshotsHaveBeenReviewed, String idOfTheDiffImage) {
        this.screenshotsHaveBeenReviewed = screenshotsHaveBeenReviewed;
        this.idOfTheDiffImage = idOfTheDiffImage;
    }

    public boolean isScreenshotsHaveBeenReviewed() {
        return screenshotsHaveBeenReviewed;
    }

    public String getIdOfTheDiffImage() {
        return idOfTheDiffImage;
    }
}
