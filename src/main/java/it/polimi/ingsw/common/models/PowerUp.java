package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;

public class PowerUp {
    private AmmoCard.Color ammoColor;
    private Type type;

    @Contract(pure = true)
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