package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public abstract class Action implements Serializable {
    private static final long serialVersionUID = 1;

    private @NotNull Type actionType;
    private @NotNull UUID gameUuid;
    private @Nullable Weapon.Name weaponName;
    private @Nullable Point destination;
    private @Nullable PowerUp.Type powerUpType;
    private @Nullable ArrayList<PowerUp> powerUpPayment;

    public @NotNull Type getActionType() {
        return actionType;
    }

    public @NotNull UUID getGameUuid() {
        return gameUuid;
    }

    public @Nullable Weapon.Name getWeaponName() {
        return weaponName;
    }

    public @Nullable Point getDestination() {
        return destination;
    }

    public void setDestination(@NotNull Point point) {
        this.destination = point;
    }

    public @Nullable PowerUp.Type getPowerUpType() {
        return powerUpType;
    }

    public @Nullable ArrayList<PowerUp> getPowerUpPayment() {
        return powerUpPayment;
    }

    public void setPowerUpPayment(@NotNull ArrayList<PowerUp> powerUpPayment) {
        this.powerUpPayment = powerUpPayment;
    }

    @Contract(pure = true)
    Action(@NotNull Type actionType, @NotNull UUID gameUuid, @Nullable Weapon.Name weaponName, @Nullable Point destination, @Nullable PowerUp.Type powerUpType, @Nullable ArrayList<PowerUp> powerUpPayment) {
        this.actionType = actionType;
        this.gameUuid = gameUuid;
        this.weaponName = weaponName;
        this.destination = destination;
        this.powerUpType = powerUpType;
        this.powerUpPayment = powerUpPayment;
    }

    public enum Type implements Serializable {
        MOVE, GRAB, FIRE, USE_POWER_UP, RELOAD, NEXT_TURN
    }
}