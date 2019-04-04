package it.polimi.ingsw.common.models;

import it.polimi.ingsw.client.views.Displayable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class AmmoCard implements Displayable {
    private @NotNull Type type;
    private @NotNull Color left;
    private @NotNull Color right;
    private @Nullable BufferedImage bufferedImage;

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
        if (bufferedImage == null) bufferedImage = ImageIO.read(AmmoCard.class.getResourceAsStream("card.png"));
        return bufferedImage;
    }

    enum Type {
        RED, YELLOW, BLUE, POWER_UP
    }

    public enum Color {
        RED, YELLOW, BLUE
    }
}