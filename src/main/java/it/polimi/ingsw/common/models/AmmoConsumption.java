package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;

public class AmmoConsumption {
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
