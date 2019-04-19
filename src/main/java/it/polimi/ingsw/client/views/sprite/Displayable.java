package it.polimi.ingsw.client.views.sprite;

import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface Displayable {
    @Nullable BufferedImage getImage() throws IOException;
}