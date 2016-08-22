package com.github.bogdanlivadariu.screenshotwatcher.models.response;

import java.io.File;

public class ScreenshotProcessingResponse {
    private boolean status;

    private File file;

    public ScreenshotProcessingResponse(boolean status, File file) {
        this.status = status;
        this.file = file;
    }

    public boolean getStatus() {
        return status;
    }

    public File getFile() {
        return file;
    }
}
