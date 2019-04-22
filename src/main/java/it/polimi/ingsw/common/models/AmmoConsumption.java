package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;

public class AmmoConsumption implements Serializable {
    private static final long serialVersionUID = 1;

    private int redAmmos;
    private int yellowAmmos;
    private int blueAmmos;

    @Contract(pure = true)
    public AmmoConsumption(int redAmmos, int yellowAmmos, int blueAmmos) {
        this.redAmmos = redAmmos;
        this.yellowAmmos = yellowAmmos;
        this.blueAmmos = blueAmmos;
    }

    @Contract(pure = true)
    public int getRedAmmos() {
        return redAmmos;
    }

    @Contract(pure = true)
    public int getYellowAmmos() {
        return yellowAmmos;
    }

    @Contract(pure = true)
    public int getBlueAmmos() {
        return blueAmmos;
    }
}
