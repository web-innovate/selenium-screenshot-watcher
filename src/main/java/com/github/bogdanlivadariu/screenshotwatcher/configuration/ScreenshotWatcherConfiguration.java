package com.github.bogdanlivadariu.screenshotwatcher.configuration;

import java.io.IOException;

import com.github.bogdanlivadariu.screenshotwatcher.models.config.ScreenshotWatcherConfigurationModel;
import com.github.bogdanlivadariu.screenshotwatcher.util.JSONReader;

public class ScreenshotWatcherConfiguration {
    private static final String CONFIGURATION_FILE = "configuration/screenshotwatcher-config.json";

    public static ScreenshotWatcherConfigurationModel getConfig() {
        JSONReader<ScreenshotWatcherServersConfiguration> reader =
            new JSONReader<>(CONFIGURATION_FILE, ScreenshotWatcherServersConfiguration.class);
        try {
            return reader.readJSONFile().getServerConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("Could not load configuration file");
    }
}
