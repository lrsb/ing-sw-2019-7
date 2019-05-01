package it.polimi.ingsw.common.models;

import it.polimi.ingsw.client.others.Utils;
import it.polimi.ingsw.client.views.sprite.Displayable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

/**
 * Each powerup card is composed in 2 parts: it has a specific type and a color.
 * For each type you have 3 card available, one for each color.
 */
public class PowerUp implements Displayable, Serializable {
    private static final long serialVersionUID = 1;

    private final @NotNull AmmoCard.Color ammoColor;
    private final @NotNull Type type;

    /**
     * PowerUp constructor.
     *
     * @param ammoColor Indicate the color of the card.
     * @param type      Indicate the the type of the powerUp card ( ex: Teleporter).
     */
    @Contract(pure = true)
    public PowerUp(@NotNull AmmoCard.Color ammoColor, @NotNull Type type) {
        this.ammoColor = ammoColor;
        this.type = type;
    }

    /**
     * Return the color of the powerUp card.
     *
     * @return Color of the card.
     */
    @Contract(pure = true)
    public @NotNull AmmoCard.Color getAmmoColor() {
        return ammoColor;
    }

    /**
     * Return the type of powerUp card
     *
     * @return Type of the card.
     */
    @Contract(pure = true)
    public @NotNull Type getType() {
        return type;
    }

    @Override
    public @NotNull BufferedImage getFrontImage() throws IOException {
        return Utils.readImage(getClass(), type.name().substring(0, 3) + ammoColor.name().substring(0, 1));
    }

    @Override
    public @NotNull BufferedImage getBackImage() throws IOException {
        return Utils.readImage(getClass(), "back");
    }

    /**
     * Return true if two powerUp cards are equals.
     *
     * @param obj The object that you want to compare.
     * @return True if the cards are equals, else otherwise.
     */
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj) {
        return obj instanceof PowerUp && ammoColor == ((PowerUp) obj).ammoColor && type == ((PowerUp) obj).type;
    }

    /**
     * {@link PowerUp} Type enum
     */
    public enum Type {
        TARGETING_SCOPE, NEWTON, TAGBACK_GRANADE, TELEPORTER
    }
}