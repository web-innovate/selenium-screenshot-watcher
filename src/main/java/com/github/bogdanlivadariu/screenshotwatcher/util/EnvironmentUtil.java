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
}
