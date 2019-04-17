package it.polimi.ingsw.common.models;

import it.polimi.ingsw.client.views.sprite.Displayable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PowerUp implements Displayable {
    private @NotNull AmmoCard.Color ammoColor;
    private @NotNull Type type;
    private @Nullable BufferedImage bufferedImage;

    /**
     * PowerUp constructor, each powerup card is composed in 2 parts: it has a specific type and a color.
     * For each type you have 3 card available, one for each color.
     * @param ammoColor Indicate the color of the card.
     * @param type Indicate the the type of the powerUp card ( ex: Teleporter).
     */
    @Contract(pure = true)
    public PowerUp(@NotNull AmmoCard.Color ammoColor, @NotNull Type type) {
        this.ammoColor = ammoColor;
        this.type = type;
    }

    /**
     * Return the color of the powerUp card.
     * @return Color of the card.
     */
    @Contract(pure = true)
    public @NotNull AmmoCard.Color getAmmoColor() {
        return ammoColor;
    }

    /**
     * Return the type of powerUp card
     * @return Type of the card.
     */
    @Contract(pure = true)
    public @NotNull Type getType() {
        return type;
    }

    /**
     * Load the image file as an attribute.
     * @return Images saved as attribute.
     * @throws IOException If can't find the indicated file.
     */
    @Override
    public @NotNull BufferedImage getImage() throws IOException {
        if (bufferedImage == null)
            bufferedImage = ImageIO.read(AmmoCard.class.getResourceAsStream("PowerUp/" + type.name().substring(0, 3) +
                    ammoColor.name().substring(0, 1) + ".png"));
        return bufferedImage;
    }

    /**
     * Return true if two powerUp cards are equals.
     * @param obj The object that you want to compare.
     * @return True if the cards are equals, else otherwise.
     */
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PowerUp) return ammoColor == ((PowerUp) obj).ammoColor && type == ((PowerUp) obj).type;
        else return false;
    }

    enum Type {
        TARGETING_SCOPE, NEWTON, TAGBACK_GRANADE, TELEPORTER
    }
}