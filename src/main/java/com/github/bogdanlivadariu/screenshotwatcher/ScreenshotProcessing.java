package com.github.bogdanlivadariu.screenshotwatcher;

import static com.github.bogdanlivadariu.screenshotwatcher.db.DBConnectors.GFS_DIFF_PHOTOS;
import static com.github.bogdanlivadariu.screenshotwatcher.db.DBConnectors.GFS_PHOTO;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.bson.types.ObjectId;

import com.github.bogdanlivadariu.screenshotwatcher.db.DBConnectors;
import com.github.bogdanlivadariu.screenshotwatcher.models.ProcessedScreenshots;
import com.github.bogdanlivadariu.screenshotwatcher.models.response.ScreenshotDiffResponse;
import com.github.bogdanlivadariu.screenshotwatcher.models.response.ScreenshotProcessingResponse;
import com.github.bogdanlivadariu.screenshotwatcher.util.GsonUtil;
import com.google.common.reflect.TypeToken;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class ScreenshotProcessing {

    public static ScreenshotProcessingResponse processScreenshots(File baseScreenshot, File newScreenshot,
        List<Rectangle> ignoreZones)
        throws IOException {
        BufferedImage bases = ImageIO.read(baseScreenshot);

        BufferedImage compare = ImageIO.read(newScreenshot);
        File diffFile = new File("diffs");

        ScreenshotDiffResponse diffResponse = getDifferences(compare, bases, ignoreZones);

        // ScreenshotDiffResponse diffResponse = getDifferences(compare, bases);
        ImageIO.write(diffResponse.getBufferedImage(), "PNG", diffFile);
        return new ScreenshotProcessingResponse(diffResponse.getStatus(), diffFile);
    }

    @SuppressWarnings("serial")
    public static ProcessedScreenshots processScreenshots(String baseScreenshotId, String newScreenshotId)
        throws IOException {
        String idOfTheDiffImage = "";
        String diffImageFileName = String.format("%s|%s|%s", baseScreenshotId, newScreenshotId, "-differences");
        boolean screenshotsHaveBeenReviewed = false;

        BasicDBObject diffImageQuery = new BasicDBObject();
        diffImageQuery.put("filename", diffImageFileName);

        boolean diffImageExists = GFS_DIFF_PHOTOS.find(diffImageQuery).iterator().hasNext();
        // in case the image has been already been generated provide the image, and not generate a new one
        if (diffImageExists) {
            GridFSDBFile diffFile = GFS_DIFF_PHOTOS.find(diffImageQuery).iterator().next();
            idOfTheDiffImage = diffFile.getId().toString();
            screenshotsHaveBeenReviewed =
                Boolean.parseBoolean(diffFile.get("screenshotsHaveBeenReviewed").toString());
        } else {
            // generate the img with the differences from the base and tmp
            BasicDBObject baseQuery = new BasicDBObject();
            baseQuery.put("_id", new ObjectId(baseScreenshotId));

            BasicDBObject tmpQuery = new BasicDBObject();
            tmpQuery.put("_id", new ObjectId(newScreenshotId));

            BasicDBObject tmpIgnoredZones = new BasicDBObject();
            tmpIgnoredZones.put("imageId", new ObjectId(newScreenshotId));
            Iterator<DBObject> its = DBConnectors.TMP_IMAGES.find(tmpIgnoredZones).iterator();

            its.hasNext();

            DBObject o = its.next();
            Type type = new TypeToken<List<Rectangle>>() {
            }.getType();
            List<Rectangle> ignoreZones = GsonUtil.gson.fromJson(o.get("ignoreZones").toString(), type);

            // pull images linked to the provided IDs from DB
            GridFSDBFile gfsBaseFile = GFS_PHOTO.find(baseQuery).iterator().next();
            GridFSDBFile gfsTmpFile = GFS_PHOTO.find(tmpQuery).iterator().next();
            // if the MD5 are a match, it means the images are the same
            if (gfsBaseFile.getMD5().equals(gfsTmpFile.getMD5())) {
                return new ProcessedScreenshots(true, "200");
            }

            File baseFile = new File(baseScreenshotId);
            File tmpFile = new File(newScreenshotId);

            gfsBaseFile.writeTo(baseFile);
            gfsTmpFile.writeTo(tmpFile);

            ScreenshotProcessingResponse processResponse =
                ScreenshotProcessing.processScreenshots(baseFile, tmpFile, ignoreZones);
            // persist the diff file in the DB
            GridFSInputFile fsDiffFile = GFS_DIFF_PHOTOS.createFile(processResponse.getFile());
            fsDiffFile.setFilename(diffImageFileName);
            fsDiffFile.put("screenshotsHaveBeenReviewed", false);
            fsDiffFile.save();

            idOfTheDiffImage = fsDiffFile.getId().toString();
            baseFile.delete();
            tmpFile.delete();
        }
        return new ProcessedScreenshots(screenshotsHaveBeenReviewed, idOfTheDiffImage);
    }

    // @SuppressWarnings("unused")
    // private static ScreenshotDiffResponse getDifferences(BufferedImage toCompare, BufferedImage baseImage) {
    // int w = toCompare.getWidth();
    // int h = toCompare.getHeight();
    // boolean diffFound = false;
    // for (int i = 0; i < w; i++) {
    // for (int j = 0; j < h; j++) {
    // if (toCompare.getRGB(i, j) != baseImage.getRGB(i, j)) {
    // int rgb = toCompare.getRGB(i, j);
    // toCompare.setRGB(i, j, (rgb | 0x00FF0000));
    // diffFound = true;
    // }
    // }
    // }
    // return new ScreenshotDiffResponse(!diffFound, toCompare);
    // }

    private static ScreenshotDiffResponse getDifferences(BufferedImage toCompare, BufferedImage baseImage,
        List<Rectangle> ignoreZones) {

        toCompare.getWidth();
        toCompare.getHeight();
        baseImage.getWidth();
        boolean diffFound = false;
        BufferedImage tempImage = deepCopy(toCompare);
        List<Point> ignorePoints = new ArrayList<>();

        for (Rectangle zone : ignoreZones) {
            // here we highlight with MAGENTA the zones that are going to be ignored
            Graphics baseGraph = baseImage.createGraphics();
            baseGraph.setColor(Color.MAGENTA);
            baseGraph.fillRect(zone.x, zone.y, zone.width, zone.height);

            Graphics tempGraph = tempImage.createGraphics();
            tempGraph.setColor(Color.MAGENTA);
            tempGraph.fillRect(zone.x, zone.y, zone.width, zone.height);

            Graphics toCompGraph = toCompare.createGraphics();
            toCompGraph.setColor(Color.MAGENTA);
            toCompGraph.drawRect(zone.x, zone.y, zone.width, zone.height);

            // add all points that are contained by a rectangle into a list of ignorePoints
            // this will prevent from iterating again over the ignoreZones when comparing pixels
            // and will easy the check if a point is inside of the ignoreZones when pixels are going to get compared
            for (int x = zone.x; x < zone.getWidth(); x++) {
                for (int y = zone.y; y < zone.getHeight(); y++) {
                    Point p = new Point(x, y);
                    ignorePoints.add(p);
                }
            }
        }
        int w = toCompare.getWidth();
        int h = toCompare.getHeight();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                // check to see if the current x,y are found inside the ignorePoints
                if (ignorePoints.contains(new Point(i, j))) {
                    continue;
                }
                // check to see if pixex from base & image to compare are the same
                if (tempImage.getRGB(i, j) != baseImage.getRGB(i, j)) {
                    // pixel found not be be the same, we apply a RED color filter to make things distinct
                    int rgb = tempImage.getRGB(i, j);
                    toCompare.setRGB(i, j, (rgb | 0x00FF0000));
                    diffFound = true;
                    /*
                     * if (((rgb & 0x00FF0000) >> 16) < 220) {
                     * toCompare.setRGB(i, j, (rgb | 0x00FF0000));
                     * } else if (((rgb & 0x0000FF00) >> 8) < 220) {
                     * toCompare.setRGB(i, j, (rgb | 0x0000FF00));
                     * } else if ((rgb & 0x000000FF) < 220) {
                     * toCompare.setRGB(i, j, (rgb | 0x000000FF));
                     * } else {
                     * toCompare.setRGB(i, j, (rgb & 0xFFFF0000));
                     * }
                     */
                }
            }
        }
        return new ScreenshotDiffResponse(!diffFound, toCompare);
    }

    private static BufferedImage deepCopy(BufferedImage bufferedImage) {
        ColorModel colorModel = bufferedImage.getColorModel();
        boolean isPreMultiplied = bufferedImage.isAlphaPremultiplied();
        WritableRaster raster = bufferedImage.copyData(bufferedImage.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(colorModel, raster, isPreMultiplied, null);
    }

}
