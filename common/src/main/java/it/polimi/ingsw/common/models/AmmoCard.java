package it.polimi.ingsw.common.models;

import it.polimi.ingsw.common.others.Displayable;
import it.polimi.ingsw.common.others.Utils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

/**
 * This class contains all the information about an ammo card.
 * Each card is composed of three elements, one on top, and two on bottom, one left and one right.
 * The top element can be an ammo ( with color ) or a power up.
 * The two bottom elements are ammo, they can be of the same or different ( just in case of power up on top ) color.
 */
public class AmmoCard implements Displayable, Serializable {
    private static final long serialVersionUID = 1;

    private final @NotNull Type type;
    private final @NotNull Color left;
    private final @NotNull Color right;

    /**
     * AmmoCard constructor.
     *
     * @param type  Indicates the type of top element, it can be a power up or a color.
     * @param left  Indicates the color of the bottom-left element.
     * @param right Indicates the color of the bottom-right element.
     */
    @Contract(pure = true)
    public AmmoCard(@NotNull Type type, @NotNull Color left, @NotNull Color right) {
        this.type = type;
        this.left = left;
        this.right = right;
    }

    /**
     * Create a copy of the given AmmoCard.
     *
     * @param ammoCard The AmmoCard.
     */
    @Contract(pure = true)
    public AmmoCard(@NotNull AmmoCard ammoCard) {
        this.type = ammoCard.type;
        this.left = ammoCard.left;
        this.right = ammoCard.right;
    }

    /**
     * Get the type of the top element.
     *
     * @return Type of top element.
     */
    @Contract(pure = true)
    public @NotNull Type getType() {
        return type;
    }

    /**
     * Get the color of the bottom-left element.
     *
     * @return Color of bottom-left element.
     */
    @Contract(pure = true)
    public @NotNull Color getLeft() {
        return left;
    }

    /**
     * Get the color of the bottom-right element.
     *
     * @return Color of bottom-right element.
     */
    @Contract(pure = true)
    public @NotNull Color getRight() {
        return right;
    }

    @Override
    public @NotNull BufferedImage getFrontImage() throws IOException {
        return Utils.readPngImage(getClass(), type.name().substring(0, 1) + left.name().substring(0, 1) + right.name().substring(0, 1));
    }

    @Override
    public @NotNull BufferedImage getBackImage() throws IOException {
        return Utils.readPngImage(getClass(), "back");
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AmmoCard &&
                ((AmmoCard) obj).type == type && ((AmmoCard) obj).left == left && ((AmmoCard) obj).right == right;
    }

    /**
     * The Type.
     */
    public enum Type {
        /**
         * Red type.
         */
        RED,
        /**
         * Yellow type.
         */
        YELLOW,
        /**
         * Blue type.
         */
        BLUE,
        /**
         * Power up type.
         */
        POWER_UP
    }

    /**
     * The Color.
     */
    public enum Color {
        /**
         * Red color.
         */
        RED(0, "\u001B[31m"),
        /**
         * Yellow color.
         */
        YELLOW(1, "\u001B[33m"),
        /**
         * Blue color.
         */
        BLUE(2, "\u001b[38;5;34m");

        private int index;

        private @NotNull String escape;

        @Contract(pure = true)
        Color(int index, @NotNull String escape) {
            this.index = index;
            this.escape = escape;
        }

        /**
         * Gets index.
         *
         * @return the index
         */
        @Contract(pure = true)
        public int getIndex() {
            return index;
        }

        @Contract(pure = true)
        public @NotNull String escape() {
            return escape;
        }
    }
}