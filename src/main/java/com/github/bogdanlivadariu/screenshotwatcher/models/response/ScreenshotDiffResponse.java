package com.github.bogdanlivadariu.screenshotwatcher.models.response;

import java.awt.image.BufferedImage;

public class ScreenshotDiffResponse {

    private boolean status;

    private BufferedImage bufferedImage;

    private float diffPercentage;

    public ScreenshotDiffResponse(boolean status, BufferedImage bufferedImage, float diffPercentage) {
        this.status = status;
        this.bufferedImage = bufferedImage;
        this.diffPercentage = diffPercentage;
    }

    public boolean getStatus() {
        return status;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public float getDiffPercentage() {
        return diffPercentage;
    }

}
