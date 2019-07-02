package it.polimi.ingsw.client.others;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.controllers.game.GameViewController;
import it.polimi.ingsw.client.controllers.game.PdfViewController;
import it.polimi.ingsw.client.controllers.startup.LoginViewController;
import it.polimi.ingsw.client.views.cli.GameCli;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;

public class Utils {
    public static void jumpBackToLogin(@Nullable NavigationController navigationController) {
        Preferences.deleteToken();
        if (navigationController != null) {
            navigationController.popToRootViewController();
            navigationController.presentViewController(true, LoginViewController.class);
        }
    }

    public static void swingOpenRules() {
        //noinspection unchecked
        var list = new JList(new String[]{"Regole gioco", "Regole armi"});
        JOptionPane.showMessageDialog(null, list, "Che regole vuoi visualizzare?", JOptionPane.PLAIN_MESSAGE);
        var index = list.getSelectedIndices();
        if (index.length > 0) {
            if (index[0] == 0)
                new PdfViewController(GameViewController.class.getResource("rules.pdf").getFile()).setVisible(true);
            else new PdfViewController(GameViewController.class.getResource("weapons.pdf").getFile()).setVisible(true);
        }
    }

    public static @NotNull BufferedImage applyColorToMask(@NotNull BufferedImage mask, @NotNull Color color) {
        var maskPixels = mask.getRGB(0, 0, mask.getWidth(), mask.getHeight(), null, 0, mask.getWidth());
        var masked = new BufferedImage(mask.getWidth(), mask.getHeight(), BufferedImage.TYPE_INT_ARGB);
        var rgb = color.getRGB();
        masked.setRGB(0, 0, mask.getWidth(), mask.getHeight(),
                Arrays.stream(maskPixels).parallel().map(e -> (e & 0xff000000) | (rgb & 0x00ffffff)).toArray(), 0, mask.getWidth());
        return masked;
    }

    public static JsonObject getStrings(@NotNull String... args) throws FileNotFoundException {
        var json = new JsonParser().parse(new JsonReader(new FileReader(new File(GameCli.class.getResource("strings.json").getFile())))).getAsJsonObject();
        for (var arg : args) json = json.get(arg).getAsJsonObject();
        return json;
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
        g.setPaint(new GradientPaint(new Point2D.Double(0, border), c0, new Point2D.Double(border, border), c1));
        g.fill(new Rectangle2D.Double(0, border, border, h - border - border));
        // Right
        g.setPaint(new GradientPaint(new Point2D.Double(w - border, border), c1, new Point2D.Double(w, border), c0));
        g.fill(new Rectangle2D.Double(w - border, border, border, h - border - border));
        // Top
        g.setPaint(new GradientPaint(
                new Point2D.Double(border, 0), c0,
                new Point2D.Double(border, border), c1));
        g.fill(new Rectangle2D.Double(border, 0, w - border - border, border));
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