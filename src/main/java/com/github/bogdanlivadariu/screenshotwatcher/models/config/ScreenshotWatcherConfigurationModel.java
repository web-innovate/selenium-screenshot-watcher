package com.github.bogdanlivadariu.screenshotwatcher.models.config;

import com.github.bogdanlivadariu.screenshotwatcher.util.EnvironmentUtil;

import java.util.Objects;

public class ScreenshotWatcherConfigurationModel {

    private String hostAddress;

    private int port;

    private String mongoDBHost;

    private int mongoDBPort;

    private String mongoDBAuth;

    private String mongoDBUserName;

    private String mongoDBPassword;

    private String mongoDB;

    private String publicUri;

    public String getHostAddress() {
        return checkAndGet(EnvironmentUtil.getHostAddress(), hostAddress);
    }

    private String checkAndGet(String actual, String defaultValue) {
        try {
            return actual == null ? defaultValue : actual;
        } catch (Exception e) {
            return null;
        }
    }

    public int getPort() {
        return port;
    }

    public String getMongoDBHost() {
        return checkAndGet(EnvironmentUtil.getMongoHost(), mongoDBHost);
    }

    public int getMongoDBPort() {
        return Integer.parseInt(
            Objects.requireNonNull(checkAndGet(EnvironmentUtil.getMongoPort(), String.valueOf(mongoDBPort))));
    }

    public String getMongoDBAuth() {
        return checkAndGet(EnvironmentUtil.getMongoDbAuth(), mongoDBAuth);
    }

    public String getMongoDBUserName() {
        return checkAndGet(EnvironmentUtil.getMongoUser(), mongoDBUserName);
    }

    public String getMongoDBPassword() {
        return checkAndGet(EnvironmentUtil.getMongoPassword(), mongoDBPassword);
    }

    public String getMongoDB() {
        return checkAndGet(EnvironmentUtil.getMongoDbName(), mongoDB);
    }

    public String getPublicUri() {
        return checkAndGet(EnvironmentUtil.getPublicUri(), publicUri);
    }
}
