package com.github.bogdanlivadariu.screenshotwatcher.configuration;

import java.util.LinkedHashMap;

import com.github.bogdanlivadariu.screenshotwatcher.models.config.ScreenshotWatcherConfigurationModel;
import com.github.bogdanlivadariu.screenshotwatcher.util.EnvironmentUtil;

public class ScreenshotWatcherServersConfiguration {

    private LinkedHashMap<String, ScreenshotWatcherConfigurationModel> serversConfig;

    public ScreenshotWatcherConfigurationModel getServerConfig() {
        String envName = "";
        try {
            envName = EnvironmentUtil.getEnvironmentName();
        } catch (NullPointerException e) {
            envName = "default";
        }
        return serversConfig.get(envName);
    }
}
