package com.pcs.daejeon.common;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.requests.IGRequest;
import com.github.instagram4j.instagram4j.requests.media.MediaConfigureTimelineRequest;
import com.github.instagram4j.instagram4j.requests.upload.RuploadPhotoRequest;
import com.github.instagram4j.instagram4j.responses.media.MediaResponse;
import com.github.instagram4j.instagram4j.responses.media.RuploadPhotoResponse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class InstagramUtil {

    private IGClient igClient(String instaId, String instaPwd) throws IGLoginException {
        IGClient client = IGClient.builder()
                .username(instaId)
                .password(instaPwd)
                .login();

        return client;
    }

    public void uploadMeal(String instaId, String instaPwd, String salt) throws Exception {
        uploadToInstagram(instaId, instaPwd, salt, true);
    }

    public void uploadToInstagram(String instaId, String instaPwd, String salt) throws Exception {
        uploadToInstagram(instaId, instaPwd, salt, false);
    }

    private void uploadToInstagram(String instaId, String instaPwd, String salt, boolean isMeal) throws Exception {
        String decryptedInstaId = Util.decrypt(instaId, salt);
        String decryptedInstaPwd = Util.decrypt(instaPwd, salt);

        IGClient client = igClient(decryptedInstaId, decryptedInstaPwd);
        File file = new File(isMeal ?
                System.getProperty("user.dir") + "/src/mealTemplate.jpg" :
                System.getProperty("user.dir")+"/src/textImage.jpg");
        byte[] imgData = Files.readAllBytes(file.toPath());
        IGRequest<RuploadPhotoResponse> uploadReq = new RuploadPhotoRequest(imgData, "1");
        String id = client.sendRequest(uploadReq).join().getUpload_id();
        IGRequest<MediaResponse.MediaConfigureTimelineResponse> configReq = new MediaConfigureTimelineRequest(
                new MediaConfigureTimelineRequest.MediaConfigurePayload().upload_id(id).caption(""));
        MediaResponse.MediaConfigureTimelineResponse response = client.sendRequest(configReq).join();
    }

    public void convertPngToJpg(boolean isMealUpload) {
        try {
            File inputFile = new File(isMealUpload ?
                    System.getProperty("user.dir") + "/src/mealTemplate.png" :
                    System.getProperty("user.dir")+"/src/textImage.png");
            BufferedImage inputImage = ImageIO.read(inputFile);

            BufferedImage result = new BufferedImage(
                    inputImage.getWidth(),
                    inputImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            result.createGraphics().drawImage(inputImage, 0, 0, Color.WHITE, null);
            File outputFile = new File(isMealUpload ?
                    System.getProperty("user.dir") + "/src/mealTemplate.jpg" :
                    System.getProperty("user.dir")+"/src/textImage.jpg");

            ImageIO.write(result, "jpg", outputFile);
        } catch (IOException ex) {
            System.err.println("Error converting PNG to JPEG: " + ex.getMessage());
        }
    }

    public void mealUploadCaption(Object[] lines) {
        String[] meal = Arrays.stream(lines).map(Object::toString).toArray(String[]::new);

        imageCaption("",  meal);
        convertPngToJpg(true);
    }

    public void postImageCaption(String description) {
        imageCaption(description, null);
        convertPngToJpg(false);
    }

    private void imageCaption(String description, String[] meals) {
        try {
            String imagePath = meals != null ?
                    System.getProperty("user.dir") + "/src/mealTemplate.png" :
                    System.getProperty("user.dir") + "/src/template.png";

            BufferedImage image = ImageIO.read(new File(imagePath));

            // create a graphics context for the image
            Graphics2D g2d = image.createGraphics();

            // set the font and color for the caption
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(System.getProperty("user.dir") + "/src/NotoSansKR-Bold.otf")).deriveFont(Font.BOLD, 50f);
            Color color = Color.BLACK;

            // get the dimensions of the image and caption text
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            int captionMaxWidth = imageWidth - 400;
            int captionHeight = g2d.getFontMetrics(font).getHeight();

            String[] lines = null;

            if (meals != null) {
                lines = splitTextIntoLines(description, font, captionMaxWidth);
            }

            // calculate the position to draw the caption in the center of the image
            int x = (imageWidth - captionMaxWidth) / 2;
            int y = (imageHeight + captionHeight) / 2;

            // draw the caption on the image
            g2d.setFont(font);
            g2d.setColor(color);

            g2d.setFont(font);
            g2d.setColor(color);
            for (int i = 0; i < (lines != null ? lines.length : meals.length); i++) {
                g2d.drawString(lines[i], x, y + (i * captionHeight));
            }

            // dispose of the graphics context
            g2d.dispose();

            // save the image with the caption
            String newImagePath = meals != null ?
                    System.getProperty("user.dir") + "/src/mealTemplate.png" :
                    System.getProperty("user.dir") + "/src/textImage.png";
            ImageIO.write(image, "png", new File(newImagePath));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String[] splitTextIntoLines(String text, Font font, int maxWidth) {
        String[] words = text.split("");
        StringBuilder currentLine = new StringBuilder();
        java.util.List<String> lines = new java.util.ArrayList<String>();

        for (String word : words) {
            if (font.getStringBounds(currentLine + word, new java.awt.font.FontRenderContext(null, true, true)).getWidth() <= maxWidth) {
                currentLine.append(word);
            } else {
                lines.add(currentLine.toString().trim());
                currentLine = new StringBuilder(word);
            }
        }
        lines.add(currentLine.toString().trim());

        return lines.toArray(new String[lines.size()]);
    }
}
