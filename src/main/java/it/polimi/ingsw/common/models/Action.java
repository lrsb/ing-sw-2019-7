package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Action implements Serializable {
    private static final long serialVersionUID = 1;

    private @Nullable Type actionType;
    private @Nullable UUID gameUuid;
    private @Nullable Weapon.Name weapon;
    private @Nullable Weapon.Name discardedWeapon;
    private @Nullable Point destination;
    private @Nullable PowerUp.Type powerUpType;
    private @Nullable ArrayList<PowerUp> powerUpPayment;

    private boolean alternativeFire;
    private int options;
    private @Nullable ArrayList<UUID> basicTarget;
    private @Nullable Point basicTargetPoint;

    private @Nullable ArrayList<UUID> firstAdditionalTarget;
    private @Nullable Point firstAdditionalTargetPoint;

    private @Nullable ArrayList<UUID> secondAdditionalTarget;
    private @Nullable Point secondAdditionalTargetPoint;

    private @Nullable ArrayList<PowerUp> basicAlternativePayment;
    private @Nullable ArrayList<PowerUp> firstAdditionalPayment;
    private @Nullable ArrayList<PowerUp> secondAdditionalPayment;

    @Contract(pure = true)
    private Action(@NotNull Type actionType, @NotNull UUID gameUuid) {
        this.actionType = actionType;
        this.gameUuid = gameUuid;
    }

    public @Nullable Type getActionType() {
        return actionType;
    }

    public @Nullable UUID getGameUuid() {
        return gameUuid;
    }

    public @Nullable Weapon.Name getWeapon() {
        return weapon;
    }

    public @Nullable Weapon.Name getDiscardedWeapon() {
        return discardedWeapon;
    }

    public @Nullable Point getDestination() {
        return destination;
    }

    public @Nullable PowerUp.Type getPowerUpType() {
        return powerUpType;
    }

    public @Nullable ArrayList<PowerUp> getPowerUpPayment() {
        return powerUpPayment;
    }

    public boolean getAlternativeFire() {
        return alternativeFire;
    }

    public int getOptions() {
        return options;
    }

    public @Nullable ArrayList<UUID> getBasicTarget() {
        return basicTarget;
    }

    public @Nullable Point getBasicTargetPoint() {
        return basicTargetPoint;
    }

    public @Nullable ArrayList<UUID> getFirstAdditionalTarget() {
        return firstAdditionalTarget;
    }

    public @Nullable Point getFirstAdditionalTargetPoint() {
        return firstAdditionalTargetPoint;
    }

    public @Nullable ArrayList<UUID> getSecondAdditionalTarget() {
        return secondAdditionalTarget;
    }

    public @Nullable Point getSecondAdditionalTargetPoint() {
        return secondAdditionalTargetPoint;
    }

    public @Nullable ArrayList<PowerUp> getBasicAlternativePayment() {
        return basicAlternativePayment;
    }

    public @Nullable ArrayList<PowerUp> getFirstAdditionalPayment() {
        return firstAdditionalPayment;
    }

    public @Nullable ArrayList<PowerUp> getSecondAdditionalPayment() {
        return secondAdditionalPayment;
    }

    public enum Type implements Serializable {
        MOVE, GRAB_WEAPON, GRAB_AMMOCARD, FIRE, USE_POWER_UP, RELOAD, NEXT_TURN
    }

    public static class Builder {
        private @NotNull UUID gameUuid;

        @Contract(pure = true)
        private Builder(@NotNull UUID gameUuid) {
            this.gameUuid = gameUuid;
        }

        @Contract(value = "_ -> new", pure = true)
        public static @NotNull Builder create(@NotNull UUID gameUuid) {
            return new Builder(gameUuid);
        }

        public @NotNull Action buildMoveAction(@NotNull Point destination) {
            var action = new Action(Type.MOVE, gameUuid);
            action.destination = destination;
            return action;
        }

        public @NotNull Action buildWeaponGrabAction(@NotNull Point destination, @Nullable Weapon.Name weapon, @Nullable Weapon.Name discardedWeapon, @Nullable ArrayList<PowerUp> alternativePayment) {
            var action = new Action(Type.GRAB_WEAPON, gameUuid);
            action.destination = destination;
            action.weapon = weapon;
            action.discardedWeapon = discardedWeapon;
            action.powerUpPayment = alternativePayment;
            return action;
        }

        public @NotNull Action buildAmmoCardGrabAction(@NotNull Point destination) {
            var action = new Action(Type.GRAB_AMMOCARD, gameUuid);
            action.destination = destination;
            return action;
        }
    }
}