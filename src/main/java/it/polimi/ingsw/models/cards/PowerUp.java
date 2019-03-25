package it.polimi.ingsw.models.cards;

import org.jetbrains.annotations.Contract;

public class PowerUp implements Card {
    private AmmoCard.Color ammoColor;
    private Type type;

    public PowerUp(AmmoCard.Color ammoColor, Type type) {
        this.ammoColor = ammoColor;
        this.type = type;
    }

    @Contract(pure = true)
    public AmmoCard.Color getAmmoColor() {
        return ammoColor;
    }

    @Contract(pure = true)
    public Type getType() {
        return type;
    }

    enum Type {
        TARGETING_SCOPE, NEWTON, TAGBACK_GRANADE, TELEPORTER
    }
}