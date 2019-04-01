package it.polimi.ingsw.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface Displayable {
    BufferedImage getImage() throws IOException;
}