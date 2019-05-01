package it.polimi.ingsw.client.others;

import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Utils {
    public static @NotNull BufferedImage readImage(@NotNull Class aClass, @NotNull String name) throws IOException {
        return ImageIO.read(aClass.getResourceAsStream(aClass.getSimpleName() + "/" + name + ".png"));
    }
}
