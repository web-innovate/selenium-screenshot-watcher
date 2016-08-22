# selenium-screenshot-watcher [![Build Status](https://travis-ci.org/web-innovate/selenium-screenshot-watcher.svg?branch=master)](https://travis-ci.org/web-innovate/selenium-screenshot-watcher)
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
        // This is where the tool runs
        String baseURL = "http://localhost:9090/";
        WebDriver driver = new ChromeDriver();
        driver.get("http://www.google.ro");

        // These are the parameters that we send to the tool, to store & restore images
        String testName = "pixel-2-pixel-test-1";
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

#Where are all the images stored ?
* In a mongoDB instance, either on cloud or locally
