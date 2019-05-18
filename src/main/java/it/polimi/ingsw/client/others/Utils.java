package it.polimi.ingsw.client.others;

import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.controllers.startup.LoginViewController;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * The type Utils.
 */
public class Utils {
    /**
     * Read png image buffered image.
     *
     * @param aClass the a class
     * @param name   the name
     * @return the buffered image
     * @throws IOException the io exception
     */
    public static @NotNull BufferedImage readPngImage(@NotNull Class aClass, @NotNull String name) throws IOException {
        return ImageIO.read(aClass.getResourceAsStream(aClass.getSimpleName() + "/" + name + ".png"));
    }

    /**
     * Read jpg image buffered image.
     *
     * @param aClass the a class
     * @param name   the name
     * @return the buffered image
     * @throws IOException the io exception
     */
    public static @NotNull BufferedImage readJpgImage(@NotNull Class aClass, @NotNull String name) throws IOException {
        return ImageIO.read(aClass.getResourceAsStream(aClass.getSimpleName() + "/" + name + ".jpg"));
    }

    /**
     * Join buffered image buffered image.
     *
     * @param img1 the img 1
     * @param img2 the img 2
     * @return the buffered image
     */
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

    /**
     * Jump back to login.
     *
     * @param navigationController the navigation controller
     */
    public static void jumpBackToLogin(@NotNull NavigationController navigationController) {
        Preferences.deleteToken();
        navigationController.popToRootViewController();
        navigationController.presentViewController(LoginViewController.class, true);
    }
}