package com.github.bogdanlivadariu.screenshotwatcher.models.response;

public class CompareScreenshotsResponse {

    private boolean sameImage;

    private String reviewLink;

    private float diffPercentage;

    public CompareScreenshotsResponse(boolean compare, String reviewLink, float diffPercentage) {
        this.sameImage = compare;
        this.reviewLink = reviewLink;
        this.diffPercentage = diffPercentage;
    }

    public boolean isSameImage() {
        return sameImage;
    }

    public String getReviewLink() {
        return reviewLink;
    }

    public float getDiffPercentage() {
        return diffPercentage;
    }
}
