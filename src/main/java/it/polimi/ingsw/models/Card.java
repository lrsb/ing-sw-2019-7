package it.polimi.ingsw.models;

/**
 *
 */
public class Card {
    private Type type;

    /**
     *
     */
    public Card() {

    }

    /**
     * Type getter
     *
     * @return Type of the card
     */
    public Type getType() {
        return type;
    }

    enum Type {
        WEAPON,
        POWERUP
    }
}
