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
    private @Nullable Weapon.Name weaponName;
    private @Nullable Weapon.Name discardedWeaponName;
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
    public Action(@NotNull Type actionType, @NotNull UUID gameUuid) {
        this.actionType = actionType;
        this.gameUuid = gameUuid;
    }

    public @Nullable Type getActionType() {
        return actionType;
    }

    public @Nullable UUID getGameUuid() {
        return gameUuid;
    }

    public @Nullable Weapon.Name getWeaponName() {
        return weaponName;
    }

    public void setWeaponName(@Nullable Weapon.Name weaponName) {
        this.weaponName = weaponName;
    }

    public @Nullable Weapon.Name getDiscardedWeaponName() {
        return discardedWeaponName;
    }

    public void setDiscardedWeaponName(@Nullable Weapon.Name discardedWeaponName) {
        this.discardedWeaponName = discardedWeaponName;
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

    public void setPowerUpType(@Nullable PowerUp.Type powerUpType) {
        this.powerUpType = powerUpType;
    }

    public @Nullable ArrayList<PowerUp> getPowerUpPayment() {
        return powerUpPayment;
    }

    public void setPowerUpPayment(@Nullable ArrayList<PowerUp> powerUpPayment) {
        this.powerUpPayment = powerUpPayment;
    }

    public boolean getAlternativeFire() {
        return alternativeFire;
    }

    public void setAlternativeFire(boolean alternativeFire) {
        this.alternativeFire = alternativeFire;
    }

    public int getOptions() {
        return options;
    }

    public void setOptions(int options) {
        this.options = options;
    }

    public @Nullable ArrayList<UUID> getBasicTarget() {
        return basicTarget;
    }

    public void setBasicTarget(@Nullable ArrayList<UUID> basicTarget) {
        this.basicTarget = basicTarget;
    }

    public @Nullable Point getBasicTargetPoint() {
        return basicTargetPoint;
    }

    public void setBasicTargetPoint(@Nullable Point basicTargetPoint) {
        this.basicTargetPoint = basicTargetPoint;
    }

    public @Nullable ArrayList<UUID> getFirstAdditionalTarget() {
        return firstAdditionalTarget;
    }

    public void setFirstAdditionalTarget(@Nullable ArrayList<UUID> firstAdditionalTarget) {
        this.firstAdditionalTarget = firstAdditionalTarget;
    }

    public @Nullable Point getFirstAdditionalTargetPoint() {
        return firstAdditionalTargetPoint;
    }

    public void setFirstAdditionalTargetPoint(@Nullable Point firstAdditionalTargetPoint) {
        this.firstAdditionalTargetPoint = firstAdditionalTargetPoint;
    }

    public @Nullable ArrayList<UUID> getSecondAdditionalTarget() {
        return secondAdditionalTarget;
    }

    public void setSecondAdditionalTarget(@Nullable ArrayList<UUID> secondAdditionalTarget) {
        this.secondAdditionalTarget = secondAdditionalTarget;
    }

    public @Nullable Point getSecondAdditionalTargetPoint() {
        return secondAdditionalTargetPoint;
    }

    public void setSecondAdditionalTargetPoint(@Nullable Point secondAdditionalTargetPoint) {
        this.secondAdditionalTargetPoint = secondAdditionalTargetPoint;
    }

    public @Nullable ArrayList<PowerUp> getBasicAlternativePayment() {
        return basicAlternativePayment;
    }

    public void setBasicAlternativePayment(@Nullable ArrayList<PowerUp> basicAlternativePayment) {
        this.basicAlternativePayment = basicAlternativePayment;
    }

    public @Nullable ArrayList<PowerUp> getFirstAdditionalPayment() {
        return firstAdditionalPayment;
    }

    public void setFirstAdditionalPayment(@Nullable ArrayList<PowerUp> firstAdditionalPayment) {
        this.firstAdditionalPayment = firstAdditionalPayment;
    }

    public @Nullable ArrayList<PowerUp> getSecondAdditionalPayment() {
        return secondAdditionalPayment;
    }

    public void setSecondAdditionalPayment(@Nullable ArrayList<PowerUp> secondAdditionalPayment) {
        this.secondAdditionalPayment = secondAdditionalPayment;
    }

    public enum Type implements Serializable {
        MOVE, GRAB, FIRE, USE_POWER_UP, RELOAD, NEXT_TURN
    }
}