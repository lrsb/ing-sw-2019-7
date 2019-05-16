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

    private @NotNull Type actionType;
    private @NotNull UUID gameUuid;
    private @Nullable Weapon.Name weaponName;
    private @Nullable Weapon.Name discardedWeaponName;
    private @Nullable Point destination;
    private @Nullable PowerUp.Type powerUpType;
    private @Nullable ArrayList<PowerUp> powerUpPayment;

    private boolean alternativeFire;
    private boolean firstAdditionalFire;
    private boolean secondAdditionalFire;
    private @NotNull ArrayList<UUID> basicTarget;
    private @Nullable Point basicTargetPoint;

    private @NotNull ArrayList<UUID> firstAdditionalTarget;
    private @Nullable Point firstAdditionalTargetPoint;

    private @NotNull ArrayList<UUID> secondAdditionalTarget;
    private @Nullable Point secondAdditionalTargetPoint;

    private @NotNull ArrayList<PowerUp> basicAlternativePayment;
    private @NotNull ArrayList<PowerUp> firstAdditionalPayment;
    private @NotNull ArrayList<PowerUp> secondAdditionalPayment;

    public @NotNull Type getActionType() {
        return actionType;
    }

    public @NotNull UUID getGameUuid() {
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

    public ArrayList<UUID> getBasicTarget() {
        return basicTarget;
    }

    public Point getBasicTargetPoint() {
        return basicTargetPoint;
    }

    public ArrayList<UUID> getFirstAdditionalTarget() {
        return firstAdditionalTarget;
    }

    public Point getFirstAdditionalTargetPoint() {
        return firstAdditionalTargetPoint;
    }

    public ArrayList<UUID> getSecondAdditionalTarget() {
        return secondAdditionalTarget;
    }

    public Point getSecondAdditionalTargetPoint() {
        return secondAdditionalTargetPoint;
    }

    public ArrayList<PowerUp> getBasicAlternativePayment() {
        return basicAlternativePayment;
    }

    public ArrayList<PowerUp> getFirstAdditionalPayment() {
        return firstAdditionalPayment;
    }

    public ArrayList<PowerUp> getSecondAdditionalPayment() {
        return secondAdditionalPayment;
    }

    @Contract(pure = true)
    Action(@NotNull Type actionType, @NotNull UUID gameUuid, @Nullable Weapon.Name weaponName,
           @Nullable Weapon.Name discardedWeaponName, @Nullable Point destination, @Nullable PowerUp.Type powerUpType,
           @Nullable ArrayList<PowerUp> powerUpPayment, boolean alternativeFire, boolean firstAdditionalFire,
           boolean secondAdditionalFire, @NotNull ArrayList<UUID> basicTarget, @Nullable Point basicTargetPoint,
           @NotNull ArrayList<UUID> firstAdditionalTarget, @Nullable Point firstAdditionalTargetPoint,
           @NotNull ArrayList<UUID> secondAdditionalTarget, @Nullable Point secondAdditionalTargetPoint,
           @NotNull ArrayList<PowerUp> basicAlternativePayment, @NotNull ArrayList<PowerUp> firstAdditionalPayment,
           @NotNull ArrayList<PowerUp> secondAdditionalPayment) {
        this.actionType = actionType;
        this.gameUuid = gameUuid;
        this.weaponName = weaponName;
        this.discardedWeaponName = discardedWeaponName;
        this.destination = destination;
        this.powerUpType = powerUpType;
        this.powerUpPayment = powerUpPayment;
        this.alternativeFire = alternativeFire;
        this.firstAdditionalFire = firstAdditionalFire;
        this.secondAdditionalFire = secondAdditionalFire;
        this.basicTarget = basicTarget;
        this.basicTargetPoint = basicTargetPoint;
        this.firstAdditionalTarget = firstAdditionalTarget;
        this.firstAdditionalTargetPoint = firstAdditionalTargetPoint;
        this.secondAdditionalTarget = secondAdditionalTarget;
        this.secondAdditionalTargetPoint = secondAdditionalTargetPoint;
        this.basicAlternativePayment = basicAlternativePayment;
        this.firstAdditionalPayment = firstAdditionalPayment;
        this.secondAdditionalPayment = secondAdditionalPayment;
    }

    public enum Type implements Serializable {
        MOVE, GRAB, FIRE, USE_POWER_UP, RELOAD, NEXT_TURN
    }
}