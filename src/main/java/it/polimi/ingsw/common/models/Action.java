package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * This class defines actions that a player can do during a game.
 */
public class Action implements Serializable {
    private static final long serialVersionUID = 1;

    private @NotNull Type actionType;
    private @NotNull UUID gameUuid;
    private @Nullable Weapon.Name weapon;
    private @Nullable Weapon.Name discardedWeapon;
    private @Nullable Point destination;
    private @Nullable PowerUp.Type powerUpType;
    private @Nullable AmmoCard.Color color;
    private @Nullable ArrayList<PowerUp> powerUpPayment;

    private boolean alternativeFire;
    private int options;
    private @Nullable ArrayList<UUID> basicTarget;
    private @Nullable Point basicTargetPoint;

    private @Nullable ArrayList<UUID> firstAdditionalTarget;
    private @Nullable Point firstAdditionalTargetPoint;

    private @Nullable ArrayList<UUID> secondAdditionalTarget;
    private @Nullable Point secondAdditionalTargetPoint;

    private @Nullable UUID target;

    @Contract(pure = true)
    private Action(@NotNull Type actionType, @NotNull UUID gameUuid) {
        this.actionType = actionType;
        this.gameUuid = gameUuid;
    }

    public @NotNull Type getActionType() {
        return actionType;
    }

    public @NotNull UUID getGameUuid() {
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

    public @Nullable AmmoCard.Color getColor() {
        return color;
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

    public @Nullable UUID getTarget() {
        return target;
    }

    public enum Type implements Serializable {
        NOTHING, MOVE, GRAB_WEAPON, GRAB_AMMOCARD, FIRE, USE_POWER_UP, RELOAD, NEXT_TURN, REBORN
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

        /**
         * Call this method to construct a Move Action
         *
         * @param destination is the point where player wants to be at the end of the Action
         * @return the constructed Move Action
         */
        public @NotNull Action buildMoveAction(@NotNull Point destination) {
            var action = new Action(Type.MOVE, gameUuid);
            action.destination = destination;
            return action;
        }

        /**
         * Call this method to construct an Action in order to grab a weapon
         *
         * @param destination        if present, defines a position where player want to grab the weapon,
         *                           if not present he has to be on a spawnpoint
         * @param weapon             the weapon he wants to grab
         * @param discardedWeapon    if the player has already 3 weapons this indicates the weapon
         *                           he wants to drop in order to grab @param weapon
         * @param alternativePayment powerUps that player wants to discard in order to pay
         *                           the cost to grab the @param weapon
         * @return the Action to grab a weapon
         */
        public @NotNull Action buildWeaponGrabAction(@Nullable Point destination, @NotNull Weapon.Name weapon, @Nullable Weapon.Name discardedWeapon, @Nullable ArrayList<PowerUp> alternativePayment) {
            var action = new Action(Type.GRAB_WEAPON, gameUuid);
            action.destination = destination;
            action.weapon = weapon;
            action.discardedWeapon = discardedWeapon;
            action.powerUpPayment = alternativePayment;
            return action;
        }

        /**
         * Call this method to construct an Action in order to grab an ammoCard
         *
         * @param destination if present indicates in which square player wants to grab the ammocard,
         *                    if not present the player wants to grab the ammocard in his actual position
         * @return the action
         */
        public @NotNull Action buildAmmoCardGrabAction(@Nullable Point destination) {
            var action = new Action(Type.GRAB_AMMOCARD, gameUuid);
            action.destination = destination;
            return action;
        }

        /**
         * Call this method to construct an Action in order to fire an enemy
         *
         * @param weapon                      indicates the weapon
         * @param destination                 indicates where he wants to move before shooting
         * @param powerUpPayment              indicates powerUps he wants to pay costs
         * @param alternativeFire             indicates if he wants to use the alternative mode of @param weapon
         * @param options                     indicates which @param weapon's effects he wants to use
         * @param basicTarget                 indicates targets of basic mode fire
         * @param basicTargetPoint            indicates a point, meaning depends on @param weapon's basic mode fire
         * @param firstAdditionalTarget       indicates targets of first additional mode fire
         * @param firstAdditionalTargetPoint  indicates a point, meaning depends on @param weapon's first
         *                                    additional mode fire
         * @param secondAdditionalTarget      indicates targets of second additional mode fire
         * @param secondAdditionalTargetPoint indicates a point, meaning depends on @param weapon's second
         *                                    additional mode fire
         * @return the action
         */

        public @NotNull Action buildFireAction(@NotNull Weapon.Name weapon, @NotNull Point destination,
                                               @Nullable ArrayList<PowerUp> powerUpPayment, boolean alternativeFire,
                                               int options, @NotNull ArrayList<UUID> basicTarget,
                                               @Nullable Point basicTargetPoint,
                                               @NotNull ArrayList<UUID> firstAdditionalTarget,
                                               @Nullable Point firstAdditionalTargetPoint,
                                               @NotNull ArrayList<UUID> secondAdditionalTarget,
                                               @Nullable Point secondAdditionalTargetPoint) {
            var action = new Action(Type.FIRE, gameUuid);
            action.weapon = weapon;
            action.destination = destination;
            action.powerUpPayment = powerUpPayment;
            action.alternativeFire = alternativeFire;
            action.options = options;
            action.basicTarget = basicTarget;
            action.basicTargetPoint = basicTargetPoint;
            action.firstAdditionalTarget = firstAdditionalTarget;
            action.firstAdditionalTargetPoint = firstAdditionalTargetPoint;
            action.secondAdditionalTarget = secondAdditionalTarget;
            action.secondAdditionalTargetPoint = secondAdditionalTargetPoint;
            return action;
        }

        public @NotNull Action buildUsePowerUp(@NotNull PowerUp.Type powerUpType, @NotNull AmmoCard.Color color, @Nullable Point destination,
                                               @Nullable UUID target) {
            var action = new Action(Type.USE_POWER_UP, gameUuid);
            action.powerUpType = powerUpType;
            action.color = color;
            action.destination = destination;
            action.target = target;
            return action;
        }

        public @NotNull Action buildReload(@NotNull Weapon.Name weapon, @Nullable ArrayList<PowerUp> powerUpPayment) {
            var action = new Action(Type.RELOAD, gameUuid);
            action.weapon = weapon;
            action.powerUpPayment = powerUpPayment;
            return action;
        }

        public @NotNull Action buildNextTurn() {
            return new Action(Type.NEXT_TURN, gameUuid);
        }

        public @NotNull Action buildReborn(@NotNull PowerUp.Type powerUpType, @NotNull AmmoCard.Color color) {
            var action = new Action(Type.REBORN, gameUuid);
            action.powerUpType = powerUpType;
            action.color = color;
            return action;
        }
    }
}