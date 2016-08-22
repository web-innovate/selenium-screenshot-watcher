package com.github.bogdanlivadariu.screenshotwatcher.models.response;

import java.io.File;

public class ScreenshotProcessResponse {
    private boolean status;

    private File file;

    public ScreenshotProcessResponse(boolean status, File file) {
        this.status = status;
        this.file = file;
    }

    public boolean isStatus() {
        return status;
    }

    public File getFile() {
        return file;
    }
}
