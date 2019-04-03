package it.polimi.ingsw.models.common;

import it.polimi.ingsw.views.Displayable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class AmmoCard implements Displayable {
    private @NotNull Type type;
    private @NotNull Color left;
    private @NotNull Color right;

    @Contract(pure = true)
    public AmmoCard(@NotNull Type type, @NotNull Color left, @NotNull Color right) {
        this.type = type;
        this.left = left;
        this.right = right;
    }

    @Contract(pure = true)
    public @NotNull Type getType() {
        return type;
    }

    @Contract(pure = true)
    public @NotNull Color getLeft() {
        return left;
    }

    @Contract(pure = true)
    public @NotNull Color getRight() {
        return right;
    }

    @Override
    public @NotNull BufferedImage getImage() throws IOException {
        return ImageIO.read(AmmoCard.class.getResourceAsStream("card.png"));
    }

    enum Type {
        RED, YELLOW, BLUE, POWER_UP
    }

    public enum Color {
        RED, YELLOW, BLUE
    }
}