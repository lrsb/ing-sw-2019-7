package it.polimi.ingsw.client.others;

import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.controllers.startup.LoginViewController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class Utils {
    public static @NotNull BufferedImage readPngImage(@NotNull Class aClass, @NotNull String name) throws IOException {
        return ImageIO.read(aClass.getResourceAsStream(aClass.getSimpleName() + "/" + name + ".png"));
    }

    public static @NotNull BufferedImage readJpgImage(@NotNull Class aClass, @NotNull String name) throws IOException {
        return ImageIO.read(aClass.getResourceAsStream(aClass.getSimpleName() + "/" + name + ".jpg"));
    }

    public static @NotNull URL getUrl(@NotNull Class aClass, @NotNull String name, @NotNull String filetype) {
        return aClass.getResource(aClass.getSimpleName() + "/" + name + "." + filetype);
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

    public static void jumpBackToLogin(@Nullable NavigationController navigationController) {
        Preferences.deleteToken();
        if (navigationController != null) {
            navigationController.popToRootViewController();
            navigationController.presentViewController(true, LoginViewController.class);
        }
    }

    public static @NotNull BufferedImage applyColorToMask(@NotNull BufferedImage mask, @NotNull Color color) {
        var maskPixels = mask.getRGB(0, 0, mask.getWidth(), mask.getHeight(), null, 0, mask.getWidth());
        var masked = new BufferedImage(mask.getWidth(), mask.getHeight(), BufferedImage.TYPE_INT_ARGB);
        var rgb = color.getRGB();
        masked.setRGB(0, 0, mask.getWidth(), mask.getHeight(),
                Arrays.stream(maskPixels).parallel().map(e -> (e & 0xFF000000) | rgb).toArray(), 0, mask.getWidth());
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

    public static @Nullable Color hexToColor(@NotNull String hex) {
        hex = hex.replace("#", "");
        switch (hex.length()) {
            case 6:
                return new Color(
                        Integer.valueOf(hex.substring(0, 2), 16),
                        Integer.valueOf(hex.substring(2, 4), 16),
                        Integer.valueOf(hex.substring(4, 6), 16));
            case 8:
                return new Color(
                        Integer.valueOf(hex.substring(0, 2), 16),
                        Integer.valueOf(hex.substring(2, 4), 16),
                        Integer.valueOf(hex.substring(4, 6), 16),
                        Integer.valueOf(hex.substring(6, 8), 16));
        }
        return null;
    }

    public static @NotNull BufferedImage blurBorder(@NotNull BufferedImage input, double border) {
        int w = input.getWidth();
        int h = input.getHeight();
        var output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        var g = output.createGraphics();
        g.drawImage(input, 0, 0, null);
        g.setComposite(AlphaComposite.DstOut);
        var c0 = new Color(0, 0, 0, 255);
        var c1 = new Color(0, 0, 0, 0);
        g.setPaint(new GradientPaint(
                new Point2D.Double(0, border), c0,
                new Point2D.Double(border, border), c1));
        g.fill(new Rectangle2D.Double(
                0, border, border, h - border - border));

        // Right
        g.setPaint(new GradientPaint(
                new Point2D.Double(w - border, border), c1,
                new Point2D.Double(w, border), c0));
        g.fill(new Rectangle2D.Double(
                w - border, border, border, h - border - border));

        // Top
        g.setPaint(new GradientPaint(
                new Point2D.Double(border, 0), c0,
                new Point2D.Double(border, border), c1));
        g.fill(new Rectangle2D.Double(
                border, 0, w - border - border, border));

        // Bottom
        g.setPaint(new GradientPaint(
                new Point2D.Double(border, h - border), c1,
                new Point2D.Double(border, h), c0));
        g.fill(new Rectangle2D.Double(
                border, h - border, w - border - border, border));


        // Top Left
        g.setPaint(new RadialGradientPaint(
                new Rectangle2D.Double(0, 0, border + border, border + border),
                new float[]{0, 1}, new Color[]{c1, c0}, MultipleGradientPaint.CycleMethod.NO_CYCLE));
        g.fill(new Rectangle2D.Double(0, 0, border, border));

        // Top Right
        g.setPaint(new RadialGradientPaint(
                new Rectangle2D.Double(w - border - border, 0, border + border, border + border),
                new float[]{0, 1}, new Color[]{c1, c0}, MultipleGradientPaint.CycleMethod.NO_CYCLE));
        g.fill(new Rectangle2D.Double(w - border, 0, border, border));

        // Bottom Left
        g.setPaint(new RadialGradientPaint(
                new Rectangle2D.Double(0, h - border - border, border + border, border + border),
                new float[]{0, 1}, new Color[]{c1, c0}, MultipleGradientPaint.CycleMethod.NO_CYCLE));
        g.fill(new Rectangle2D.Double(0, h - border, border, border));

        // Bottom Right
        g.setPaint(new RadialGradientPaint(
                new Rectangle2D.Double(w - border - border, h - border - border, border + border, border + border),
                new float[]{0, 1}, new Color[]{c1, c0}, MultipleGradientPaint.CycleMethod.NO_CYCLE));
        g.fill(new Rectangle2D.Double(w - border, h - border, border, border));

        g.dispose();

        return output;
    }
}