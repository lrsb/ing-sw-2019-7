package it.polimi.ingsw.common.models;

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
    private @Nullable Weapon.Name weaponName;
    private @Nullable Weapon.Name discardedWeaponName;
    private @Nullable Point destination;
    private @Nullable PowerUp.Type powerUpType;
    private @Nullable ArrayList<PowerUp> powerUpPayment;

    private boolean alternativeFire;
    private boolean firstAdditionalFire;
    private boolean secondAdditionalFire;
    private @Nullable ArrayList<UUID> basicTarget;
    private @Nullable Point basicTargetPoint;

    private @Nullable ArrayList<UUID> firstAdditionalTarget;
    private @Nullable Point firstAdditionalTargetPoint;

    private @Nullable ArrayList<UUID> secondAdditionalTarget;
    private @Nullable Point secondAdditionalTargetPoint;

    private @Nullable ArrayList<PowerUp> basicAlternativePayment;
    private @Nullable ArrayList<PowerUp> firstAdditionalPayment;
    private @Nullable ArrayList<PowerUp> secondAdditionalPayment;

    public @Nullable Type getActionType() {
        return actionType;
    }

    public @Nullable UUID getGameUuid() {
        return gameUuid;
    }

    public @Nullable Weapon.Name getWeaponName() {
        return weaponName;
    }

    public @Nullable Weapon.Name getDiscardedWeaponName() {
        return discardedWeaponName;
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

    public boolean getAlternativeFire() {
        return alternativeFire;
    }

    public boolean getFirstAdditionalFire() {
        return firstAdditionalFire;
    }

    public boolean getSecondAdditionalFire() {
        return secondAdditionalFire;
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
        MOVE, GRAB, FIRE, USE_POWER_UP, RELOAD, NEXT_TURN
    }
}