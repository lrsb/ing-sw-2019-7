package it.polimi.ingsw.graphics;

import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface Displayable {
    @NotNull BufferedImage getImage() throws IOException;
}