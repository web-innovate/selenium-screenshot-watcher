package com.github.bogdanlivadariu.screenshotwatcher.models.response;

import java.io.File;

public class ScreenshotProcessingResponse {
    private boolean status;

    private File file;

    private float diffPercentage;

    public ScreenshotProcessingResponse(boolean status, File file, float diffPercentage) {
        this.status = status;
        this.file = file;
        this.diffPercentage = diffPercentage;
    }

    public boolean getStatus() {
        return status;
    }

    public File getFile() {
        return file;
    }

    public float getDiffPercentage() {
        return diffPercentage;
    }
}
