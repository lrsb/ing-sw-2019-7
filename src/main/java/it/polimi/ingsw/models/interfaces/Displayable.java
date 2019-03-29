package it.polimi.ingsw.models.interfaces;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface Displayable {
    BufferedImage getImage() throws IOException;
}
