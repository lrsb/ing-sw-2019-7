package it.polimi.ingsw.client.views.sprite;

import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Implemented when an object have a image associated with it.
 */
public interface Displayable {
    /**
     * Get the image associated. Each call read the image from fs and store it in an {@link BufferedImage}, so there is a delay when the method is invoked.
     *
     * @return The image associated.
     * @throws IOException Thrown when there was a problem during the reading.
     */
    @NotNull BufferedImage getImage() throws IOException;
}