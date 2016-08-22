package com.github.bogdanlivadariu.screenshotwatcher.models.config;

public class ScreenshotWatcherConfigurationModel {

    private String hostAddress;

    private int portPreference;

    private String mongoDBHost;

    private int mongoDBPort;

    private String mongoDBUserName;

    private String mongoDBPassword;

    private String mongoDB;

    public String getHostAddress() {
        return hostAddress;
    }

    public int getPortPreference() {
        return portPreference;
    }

    public String getMongoDBHost() {
        return mongoDBHost;
    }

    public int getMongoDBPort() {
        return mongoDBPort;
    }

    public String getMongoDBUserName() {
        return mongoDBUserName;
    }

    public String getMongoDBPassword() {
        return mongoDBPassword;
    }

    public String getMongoDB() {
        return mongoDB;
    }

}
