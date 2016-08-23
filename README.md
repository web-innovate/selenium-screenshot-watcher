# selenium-screenshot-watcher [![Build Status](https://travis-ci.org/web-innovate/selenium-screenshot-watcher.svg?branch=master)](https://travis-ci.org/web-innovate/selenium-screenshot-watcher) [![donate](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=NCEP5PRTZXMMA)

[![Join the chat at https://gitter.im/web-innovate/selenium-screenshot-watcher](https://badges.gitter.im/web-innovate/selenium-screenshot-watcher.svg)](https://gitter.im/web-innovate/selenium-screenshot-watcher?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Allows you to take a screenshot of the page on which your automated tests are on, that can be compared later on.


How it works ?
* `"tell to the tool when to take a screenshot"` along with some params user to store the image & later to identify it
* `"tell to the tool to do a compare"` of your screenshot
  * the tool is going to look for any image existence in the DB based on the params above, and report back to you
* tell your tests when to PASS or FAIL based on what the tool `speaks` back

Example:
```java
public class ScreenTest {
    @Test
    public void blinkTest() {
        // Provide the URL where the tool runs
        // In this example you could rely on the heroku instance
        String baseURL = "http://selenium-screenshot-watcher.herokuapp.com/";
        WebDriver driver = new ChromeDriver();
        driver.get("http://www.google.ro");

        String testName = "heroku sample test - 1";
        String testBrowser = "chrome";
        String description = "running on localhost";

        // Initialize the tool
        ScreenshotWatcher watcher = new ScreenshotWatcher(driver, baseURL);

        // Takes a screenshot
        BaseScreenshotModel scrTaken = watcher.blink(testName, testBrowser, description);

        // Compares the screenshot
        CompareScreenshotsResponse compareResponse = watcher.compare(scrTaken);

        // assert if the images are the same, if not, they visit the review link
        assertTrue(compareResponse.getReviewLink(), compareResponse.isSameImage());
    }
}
```

Here is an example project that uses this tool: https://github.com/web-innovate/selenium-screenshot-watcher-sample

#Where are all the images stored ?
* In a mongoDB instance, either on cloud or locally
