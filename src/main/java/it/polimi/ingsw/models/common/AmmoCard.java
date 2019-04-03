package it.polimi.ingsw.models.common;

import it.polimi.ingsw.views.Displayable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class AmmoCard implements Displayable {
    private Type type;
    private Color left;
    private Color right;

    @Contract(pure = true)
    public AmmoCard(Type type, Color left, Color right) {
        this.type = type;
        this.left = left;
        this.right = right;
    }

    @Contract(pure = true)
    public Type getType() {
        return type;
    }

    @Contract(pure = true)
    public Color getLeft() {
        return left;
    }

    @Contract(pure = true)
    public Color getRight() {
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