package com.github.bogdanlivadariu.screenshotwatcher;

import java.awt.Rectangle;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.glassfish.grizzly.http.util.Base64Utils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.github.bogdanlivadariu.screenshotwatcher.models.BaseScreenshotModel;
import com.github.bogdanlivadariu.screenshotwatcher.models.requests.CompareScreenshotRequest;
import com.github.bogdanlivadariu.screenshotwatcher.models.requests.UploadScreenshotRequest;
import com.github.bogdanlivadariu.screenshotwatcher.models.response.CompareScreenshotsResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

public class ScreenshotWatcher {

    private WebDriver driver;

    private String baseURL;

    public ScreenshotWatcher(WebDriver driver, String baseURL) {
        this.driver = driver;
        this.baseURL = baseURL;
    }

    public BaseScreenshotModel blink(String testName, String testBrowser, String description) {
        // Dimension oldResolution = driver.manage().window().getSize();
        // System.out.println(oldResolution.toString());
        // driver.manage().window().setSize(new Dimension(1680, 1050));
        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        String imageEncoded = Base64Utils.encodeToString(screenshot, false);
        // driver.manage().window().setSize(oldResolution);
        UploadScreenshotRequest uploadRequest =
            new UploadScreenshotRequest(testName, testBrowser, description, imageEncoded);
        return new BaseScreenshotModel((BasicDBObject) JSON.parse(sendPost(baseURL + "upload", uploadRequest)));
    }

    public BaseScreenshotModel blink(String testName, String testBrowser, String description,
        List<Rectangle> ignoreZones) {
        // Dimension oldResolution = driver.manage().window().getSize();
        // System.out.println(oldResolution.toString());
        // driver.manage().window().setSize(new Dimension(1680, 1050));
        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        String imageEncoded = Base64Utils.encodeToString(screenshot, false);
        // driver.manage().window().setSize(oldResolution);
        UploadScreenshotRequest uploadRequest =
            new UploadScreenshotRequest(testName, testBrowser, description, imageEncoded, ignoreZones);
        return new BaseScreenshotModel((BasicDBObject) JSON.parse(sendPost(baseURL + "upload", uploadRequest)));
    }

    public BaseScreenshotModel blink(String base64EncodedImage, String testName, String testBrowser,
        String description) {
        UploadScreenshotRequest uploadRequest =
            new UploadScreenshotRequest(testName, testBrowser, description, base64EncodedImage);
        return new BaseScreenshotModel((BasicDBObject) JSON.parse(sendPost(baseURL + "upload", uploadRequest)));
    }

    public CompareScreenshotsResponse compare(BaseScreenshotModel objectForCompare) {
        CompareScreenshotRequest compareRequest = new CompareScreenshotRequest(objectForCompare);
        return new Gson().fromJson(
            sendPost(baseURL + "compare", compareRequest), CompareScreenshotsResponse.class);
    }

    private String sendPost(String url, Object postRequest) {

        JsonSerializer<ObjectId> objectIDSerializer = new JsonSerializer<ObjectId>() {
            @Override
            public JsonElement serialize(ObjectId src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject jo = new JsonObject();
                jo.addProperty("$oid", src.toString());
                return jo;
            }
        };
        Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, objectIDSerializer).create();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);

        Response responseObject = target.request().post(Entity.json(gson.toJson(postRequest)));
        String responseJson = responseObject.readEntity(String.class);

        return responseJson;
    }
}
