package it.polimi.ingsw.client.others;

import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.controllers.startup.LoginViewController;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

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

    public static void jumpBackToLogin(@NotNull NavigationController navigationController) {
        Preferences.deleteToken();
        navigationController.popToRootViewController();
        navigationController.presentViewController(true, LoginViewController.class);
    }

    public static @NotNull BufferedImage applyColorToMask(@NotNull BufferedImage mask, @NotNull Color color) {
        int[] maskPixels = mask.getRGB(0, 0, mask.getWidth(), mask.getHeight(), null, 0, mask.getWidth());
        var masked = new BufferedImage(mask.getWidth(), mask.getHeight(), BufferedImage.TYPE_INT_ARGB);
        var rgb = color.getRGB();
        masked.setRGB(0, 0, mask.getWidth(), mask.getHeight(),
                Arrays.stream(maskPixels).parallel().map(e -> e != 0 ? rgb : e).toArray(), 0, mask.getWidth());
        return masked;
    }

    public static boolean isRetina() {
        var isRetina = false;
        GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        try {
            //TODO: Illegal reflective access
            var field = graphicsDevice.getClass().getDeclaredField("scale");
            if (field != null) {
                field.setAccessible(true);
                Object scale = field.get(graphicsDevice);
                if (scale instanceof Integer && (Integer) scale == 2) isRetina = true;
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
        return isRetina;
    }
}