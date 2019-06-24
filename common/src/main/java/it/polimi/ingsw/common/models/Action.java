package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class defines actions that a player can do during a game.
 */
public class Action implements Serializable {
    private static final long serialVersionUID = 1;

    private @NotNull Type actionType;
    private @NotNull UUID gameUuid;
    private @Nullable Weapon weapon;
    private @Nullable Weapon discardedWeapon;
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

    public @Nullable Weapon getWeapon() {
        return weapon;
    }

    public @Nullable Weapon getDiscardedWeapon() {
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

    public @Nullable List<PowerUp> getPowerUpPayment() {
        return powerUpPayment;
    }

    public boolean getAlternativeFire() {
        return alternativeFire;
    }

    public int getOptions() {
        return options;
    }

    public @Nullable List<UUID> getBasicTarget() {
        return basicTarget;
    }

    public @Nullable Point getBasicTargetPoint() {
        return basicTargetPoint;
    }

    public @Nullable List<UUID> getFirstAdditionalTarget() {
        return firstAdditionalTarget;
    }

    public @Nullable Point getFirstAdditionalTargetPoint() {
        return firstAdditionalTargetPoint;
    }

    public @Nullable List<UUID> getSecondAdditionalTarget() {
        return secondAdditionalTarget;
    }

    public @Nullable Point getSecondAdditionalTargetPoint() {
        return secondAdditionalTargetPoint;
    }

    public @Nullable UUID getTarget() {
        return target;
    }

    /**
     * Each type of this enum represents a specific action that a player can do
     */

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
         * @return the constructed action to move
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
         * @return the constructed action to grab a weapon
         */
        public @NotNull Action buildWeaponGrabAction(@Nullable Point destination, @NotNull Weapon weapon, @Nullable Weapon discardedWeapon, @Nullable List<PowerUp> alternativePayment) {
            var action = new Action(Type.GRAB_WEAPON, gameUuid);
            action.destination = destination;
            action.weapon = weapon;
            action.discardedWeapon = discardedWeapon;
            if (alternativePayment != null) action.powerUpPayment = new ArrayList<>(alternativePayment);
            return action;
        }

        /**
         * Call this method to construct an Action in order to grab an ammoCard
         *
         * @param destination if present indicates in which square player wants to grab the ammocard,
         *                    if not present the player wants to grab the ammocard in his actual position
         * @return the constructed action to grab an ammo card
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
         * @return the constructed action to fire
         */

        public @NotNull Action buildFireAction(@NotNull Weapon weapon, @Nullable Point destination,
                                               @Nullable List<PowerUp> powerUpPayment, boolean alternativeFire,
                                               int options, @NotNull List<UUID> basicTarget,
                                               @Nullable Point basicTargetPoint,
                                               @NotNull List<UUID> firstAdditionalTarget,
                                               @Nullable Point firstAdditionalTargetPoint,
                                               @NotNull List<UUID> secondAdditionalTarget,
                                               @Nullable Point secondAdditionalTargetPoint) {
            var action = new Action(Type.FIRE, gameUuid);
            action.weapon = weapon;
            action.destination = destination;
            if (powerUpPayment != null) action.powerUpPayment = new ArrayList<>(powerUpPayment);
            action.alternativeFire = alternativeFire;
            action.options = options;
            action.basicTarget = new ArrayList<>(basicTarget);
            action.basicTargetPoint = basicTargetPoint;
            action.firstAdditionalTarget = new ArrayList<>(firstAdditionalTarget);
            action.firstAdditionalTargetPoint = firstAdditionalTargetPoint;
            action.secondAdditionalTarget = new ArrayList<>(secondAdditionalTarget);
            action.secondAdditionalTargetPoint = secondAdditionalTargetPoint;
            return action;
        }

        /**
         * Call this method to construct an Action in order to use a Power Up's effect
         *
         * @param powerUpType the type of power up a player wants to use
         * @param color       the color of the power up a plater wants to use
         * @param destination fit this param with a point of the map if the specific power up needs it
         * @param target      fit this param with a player if the specific power up needs it
         * @return the constructed action to use a power up
         */
        public @NotNull Action buildUsePowerUp(@NotNull PowerUp.Type powerUpType, @NotNull AmmoCard.Color color, @Nullable Point destination,
                                               @Nullable UUID target) {
            var action = new Action(Type.USE_POWER_UP, gameUuid);
            action.powerUpType = powerUpType;
            action.color = color;
            action.destination = destination;
            action.target = target;
            return action;
        }

        /**
         * Call this method to construct an Action in order to reload a weapon
         *
         * @param weapon         fit this param with the name of the weapon player wants to reload
         * @param powerUpPayment the power ups player wants to use as payment instead of using cubes
         * @return the constructed action to reload a weapon
         */
        public @NotNull Action buildReload(@NotNull Weapon weapon, @Nullable List<PowerUp> powerUpPayment) {
            var action = new Action(Type.RELOAD, gameUuid);
            action.weapon = weapon;
            if (powerUpPayment != null) action.powerUpPayment = new ArrayList<>(powerUpPayment);
            return action;
        }

        /**
         * Call this method to pass the turn to the next player or to player who can respond with action
         *
         * @return the action to finish the turn of the actual player
         */
        public @NotNull Action buildNextTurn() {
            return new Action(Type.NEXT_TURN, gameUuid);
        }

        /**
         * Call this method to allow a player to respawn when he dies
         *
         * @param powerUpType the type of the power up he wants to discard
         * @param color       the color of the power up he wants to discard
         * @return the constructed action to respawn
         */
        public @NotNull Action buildReborn(@NotNull PowerUp.Type powerUpType, @NotNull AmmoCard.Color color) {
            var action = new Action(Type.REBORN, gameUuid);
            action.powerUpType = powerUpType;
            action.color = color;
            return action;
        }
    }
}