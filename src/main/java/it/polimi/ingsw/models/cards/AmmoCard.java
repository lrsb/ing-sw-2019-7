package it.polimi.ingsw.models.cards;

import org.jetbrains.annotations.Contract;

public class AmmoCard implements Card {
    private Type type;
    private Color left;
    private Color right;

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

    enum Type {
        RED, YELLOW, BLUE, POWERUP
    }

    enum Color {
        RED, YELLOW, BLUE
    }
}