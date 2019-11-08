package com.github.bogdanlivadariu.screenshotwatcher.util;

public class EnvironmentUtil {
    private static String environmentName;

    /**
     * @return value from the 'env' variable.
     */
    public static String getEnvironmentName() {
        if (environmentName == null) {
            String env = null;
            if ((env = System.getProperty("env")) != null) {
                environmentName = env;
            } else {
                throw new NullPointerException("The 'env' system property was not set, "
                    + "make sure that you have it set using -Denv=local , or any other environment of choice");
            }
        }
        return environmentName;
    }

    //     "hostAddress": "http://0.0.0.0",
    //         "portPreference": 8080,
    //         "mongoDBHost": "ds039311.mongolab.com",
    //         "mongoDBPort": 39311,
    //         "mongoDBUserName": "test",
    //         "mongoDBPassword": "test",
    //         "mongoDB": "test_reports"

    public static String getHostAddress() {
        return extractEnvProperty("hostAddress");
    }

    private static String extractEnvProperty(String envProperty) {
        String val;
        return (val = System.getProperty(envProperty)) != null ? val : null;
    }

    public static String getPort() {
        return extractEnvProperty("port");
    }

    public static String getMongoHost() {
        return extractEnvProperty("mongoHost");
    }

    public static String getMongoPort() {
        return extractEnvProperty("mongoPort");
    }

    public static String getMongoDbAuth() {
        return extractEnvProperty("mongoAuthDB");
    }

    public static String getMongoUser() {
        return extractEnvProperty("mongoUser");
    }

    public static String getMongoPassword() {
        return extractEnvProperty("mongoPassword");
    }

    public static String getMongoDbName() {
        return extractEnvProperty("mongoDbName");
    }

    public static String getPublicUri() {
        return extractEnvProperty("publicUri");
    }

}
