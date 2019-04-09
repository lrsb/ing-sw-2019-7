package it.polimi.ingsw.common.models;

import it.polimi.ingsw.client.views.Displayable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * This class contains all the informations about the ammo card.
 * Each card is composed of three elements, one on top, and two on bottom, one left and one right.
 * The top element can be an ammo ( with color ) or a power up.
 * The two bottom elements are ammo, they can be of the same or different ( just in case of power up on top ) color.
 */
public class AmmoCard implements Displayable {
    private @NotNull Type type;
    private @NotNull Color left;
    private @NotNull Color right;
    private @Nullable BufferedImage bufferedImage;

    /**
     * AmmoCard constructor.
     * @param type Indicate the type of top element, it can be a power up or a color.
     * @param left Indicate the color of the bottom-left element.
     * @param right Indicate the color of the bottom-right element.
     */
    @Contract(pure = true)
    public AmmoCard(@NotNull Type type, @NotNull Color left, @NotNull Color right) {
        this.type = type;
        this.left = left;
        this.right = right;
    }

    /**
     *Get the type of the top element.
     * @return Type of top element.
     */
    @Contract(pure = true)
    public @NotNull Type getType() {
        return type;
    }

    /**
     *Get the color of the bottom-left element.
     * @return Color of bottom-left element.
     */
    @Contract(pure = true)
    public @NotNull Color getLeft() {
        return left;
    }

    /**
     * Get the color of the bottom-right element.
     * @return Color of bottom-right element.
     */
    @Contract(pure = true)
    public @NotNull Color getRight() {
        return right;
    }


    @Override
    public @NotNull BufferedImage getImage() throws IOException {
        if (bufferedImage == null)
            bufferedImage = ImageIO.read(AmmoCard.class.getResourceAsStream("AmmoCard/" + type.name().substring(0, 1) +
                    left.name().substring(0, 1) + right.name().substring(0, 1) + ".png"));
        return bufferedImage;
    }

    enum Type {
        RED, YELLOW, BLUE, POWER_UP
    }

    public enum Color {
        RED, YELLOW, BLUE
    }
}