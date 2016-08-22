package com.github.bogdanlivadariu.screenshotwatcher.models.response;

import java.awt.image.BufferedImage;

public class ScreenshotDiffResponse {

    private boolean status;

    private BufferedImage bufferedImage;

    public ScreenshotDiffResponse(boolean status, BufferedImage bufferedImage) {
        this.status = status;
        this.bufferedImage = bufferedImage;
    }

    public boolean getStatus() {
        return status;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }
}
