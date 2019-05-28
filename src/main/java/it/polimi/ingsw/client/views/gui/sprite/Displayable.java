package it.polimi.ingsw.client.views.gui.sprite;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Implemented when an object is a displayable card.
 */
public interface Displayable {
    /**
     * Get the front image associated. Each call read the image from fs and store it in an {@link BufferedImage}, so there is a delay when the method is invoked.
     *
     * @return The image associated.
     * @throws IOException Thrown when there was a problem during the reading.
     */
    @Contract(pure = true)
    @NotNull BufferedImage getFrontImage() throws IOException;

    /**
     * Get the back image associated. Each call read the image from fs and store it in an {@link BufferedImage}, so there is a delay when the method is invoked.
     *
     * @return The image associated.
     * @throws IOException Thrown when there was a problem during the reading.
     */
    @Contract(pure = true)
    @NotNull BufferedImage getBackImage() throws IOException;
}