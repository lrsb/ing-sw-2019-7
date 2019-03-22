package it.polimi.ingsw.models;

public class AmmoCard {
    private Type type;
    private Color left, right;

    enum Type {
        RED, YELLOW, BLUE, POWERUP
    }

    enum Color {
        RED, YELLOW, BLUE
    }
}
