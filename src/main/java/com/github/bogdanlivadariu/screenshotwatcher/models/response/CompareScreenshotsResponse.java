package com.github.bogdanlivadariu.screenshotwatcher.models.response;

public class CompareScreenshotsResponse {

    private boolean sameImage;

    private String reviewLink;

    public CompareScreenshotsResponse(boolean compare, String reviewLink) {
        this.sameImage = compare;
        this.reviewLink = reviewLink;
    }

    public boolean isSameImage() {
        return sameImage;
    }

    public String getReviewLink() {
        return reviewLink;
    }
}
