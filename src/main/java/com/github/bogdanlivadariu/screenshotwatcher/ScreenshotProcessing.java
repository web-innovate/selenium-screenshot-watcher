package com.github.bogdanlivadariu.screenshotwatcher;

import static com.github.bogdanlivadariu.screenshotwatcher.db.DBConnectors.GFS_DIFF_PHOTOS;
import static com.github.bogdanlivadariu.screenshotwatcher.db.DBConnectors.GFS_PHOTO;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.bson.types.ObjectId;

import com.github.bogdanlivadariu.screenshotwatcher.models.ProcessedScreenshots;
import com.github.bogdanlivadariu.screenshotwatcher.models.response.ScreenshotDiffResponse;
import com.github.bogdanlivadariu.screenshotwatcher.models.response.ScreenshotProcessingResponse;
import com.github.bogdanlivadariu.screenshotwatcher.util.IgnoreAreas;
import com.mongodb.BasicDBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class ScreenshotProcessing {

    public static ScreenshotProcessingResponse processScreenshots(File baseScreenshot, File newScreenshot)
        throws IOException {
        BufferedImage bases = ImageIO.read(baseScreenshot);
        // ArrayList<Rectangle> zone = new ArrayList<>();
        // zone.add(new Rectangle(135, 575, 1500, 100));

        BufferedImage compare = ImageIO.read(newScreenshot);
        File diffFile = new File("diffs");

        // TODO implement a different way pass difference areas, maybe pass then directly from the test :)
        // use this if you want to process screenshots and ignore some areas
        ArrayList<Rectangle> ignoreAreas = new ArrayList<>();
        ignoreAreas.add(IgnoreAreas.FIXED_AREA);
        ScreenshotDiffResponse diffResponse = getDifferences(compare, bases, ignoreAreas);

        // ScreenshotDiffResponse diffResponse = getDifferences(compare, bases);
        ImageIO.write(diffResponse.getBufferedImage(), "PNG", diffFile);
        return new ScreenshotProcessingResponse(diffResponse.getStatus(), diffFile);
    }

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

            ScreenshotProcessingResponse processResponse = ScreenshotProcessing.processScreenshots(baseFile, tmpFile);
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

    private static ScreenshotDiffResponse getDifferences(BufferedImage toCompare, BufferedImage baseImage) {
        int w = toCompare.getWidth();
        int h = toCompare.getHeight();
        boolean diffFound = false;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (toCompare.getRGB(i, j) != baseImage.getRGB(i, j)) {
                    int rgb = toCompare.getRGB(i, j);
                    toCompare.setRGB(i, j, (rgb | 0x00FF0000));
                    diffFound = true;
                }
            }
        }
        return new ScreenshotDiffResponse(!diffFound, toCompare);
    }

    private static ScreenshotDiffResponse getDifferences(BufferedImage toCompare, BufferedImage baseImage,
        ArrayList<Rectangle> ignoreZones) {
        System.out.println(String.format("%s, %s", toCompare, baseImage));
        toCompare.getWidth();
        toCompare.getHeight();
        baseImage.getWidth();
        boolean diffFound = false;
        BufferedImage tempImage = deepCopy(toCompare);
        for (Rectangle zone : ignoreZones) {
            Graphics baseGraph = baseImage.createGraphics();
            baseGraph.setColor(Color.MAGENTA);
            baseGraph.fillRect(zone.x, zone.y, zone.width, zone.height);

            Graphics tempGraph = tempImage.createGraphics();
            tempGraph.setColor(Color.MAGENTA);
            tempGraph.fillRect(zone.x, zone.y, zone.width, zone.height);

            Graphics toCompGraph = toCompare.createGraphics();
            toCompGraph.setColor(Color.MAGENTA);
            toCompGraph.drawRect(zone.x, zone.y, zone.width, zone.height);
        }
        int w = toCompare.getWidth();
        int h = toCompare.getHeight();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (tempImage.getRGB(i, j) != baseImage.getRGB(i, j)) {
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
