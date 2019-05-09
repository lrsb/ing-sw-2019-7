package it.polimi.ingsw.client.others;

import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Utils {
    public static @NotNull BufferedImage readPngImage(@NotNull Class aClass, @NotNull String name) throws IOException {
        return ImageIO.read(aClass.getResourceAsStream(aClass.getSimpleName() + "/" + name + ".png"));
    }

    public static @NotNull BufferedImage readJpgImage(@NotNull Class aClass, @NotNull String name) throws IOException {
        return ImageIO.read(aClass.getResourceAsStream(aClass.getSimpleName() + "/" + name + ".jpg"));
    }

    public static @NotNull BufferedImage joinBufferedImage(@NotNull BufferedImage img1, @NotNull BufferedImage img2) {
        var wid = img1.getWidth() + img2.getWidth();
        var height = Math.max(img1.getHeight(), img2.getHeight());
        BufferedImage newImage = new BufferedImage(wid, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, wid, height);
        g2.setColor(oldColor);
        g2.drawImage(img1, null, 0, 0);
        g2.drawImage(img2, null, img1.getWidth(), 0);
        g2.dispose();
        return newImage;
    }
}