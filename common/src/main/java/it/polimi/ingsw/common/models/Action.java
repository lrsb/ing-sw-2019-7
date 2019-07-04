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

    /**
     *
     * @return the type of action that a player is trying to make
     */
    public @NotNull Type getActionType() {
        return actionType;
    }

    /**
     *
     * @return the identifier of the game which this action is referred to
     */
    public @NotNull UUID getGameUuid() {
        return gameUuid;
    }

    /**
     * an action can contain a weapon, its meaning depends on action type:
     * -GRAB WEAPON: it's the weapon a player wants to grab;
     * -FIRE: it's the weapon a player wants to use to fire
     * -RELOAD: it's the weapon a player wants to reload
     *
     * @return the weapon
     */
    public @Nullable Weapon getWeapon() {
        return weapon;
    }

    /**
     * this field is needed when a player wants to grab a weapon,
     * but he already has got 3 weapons; this weapon will be drop
     * where @getWeapon() it will be grabbed
     *
     * @return the weapon
     */
    public @Nullable Weapon getDiscardedWeapon() {
        return discardedWeapon;
    }

    /**
     * the meaning of the point depends on action type:
     * - MOVE: the destination of the player;
     * - GRAB_AMMOCARD: the point where a player wants to grab the tile;
     * - GRAB_WEAPON: the point where a player wants to grab a weapon;
     * - USE_POWER_UP: depends on type of using power up;
     * - FIRE: if player can move before shooting, this point represents
     *          the position that he wants to reach before shooting
     *
     * @return a point of the board
     */
    public @Nullable Point getDestination() {
        return destination;
    }

    /**
     * this field is used for two types of action:
     * - USE_POWER_UP: the type of the using power up;
     * - REBORN: the type of the power up used to spawn.
     *
     * @return the type of the using power up
     */
    public @Nullable PowerUp.Type getPowerUpType() {
        return powerUpType;
    }

    /**
     * this field is used when a power up is used and
     * represents its color
     *
     * @return the color of the power up
     */
    public @Nullable AmmoCard.Color getColor() {
        return color;
    }

    /**
     * when a player has to pay for his action he can
     * use some of his power ups to pay
     *
     * @return the power ups the player wants to use to pay
     */
    public @Nullable List<PowerUp> getPowerUpPayment() {
        return powerUpPayment;
    }

    /**
     * when a player wants to fire, depending on the using weapon,
     * maybe he has to choose which effect wants to use
     *
     * @return true if he wants to use the alternative effect of the weapon,
     *          false otherwise
     */
    public boolean getAlternativeFire() {
        return alternativeFire;
    }

    /**
     * indicates modes of fire (depends on the specific weapon):
     * 0 -> only basic effect;
     * 1 -> basic effect + first additional effect
     * 2 -> basic effect + second additional effect
     * 3 -> basic effect + first + second additional effect
     *
     * @return modes of fire
     */
    public int getOptions() {
        return options;
    }

    /**
     * if action is FIRE, gets the targets of the basic effect
     *
     * @return targets of basic effect
     */
    public @Nullable List<UUID> getBasicTarget() {
        return basicTarget;
    }

    /**
     * if action is FIRE, gets a point, which meaning depends on the specific weapon
     *
     * @return a point
     */
    public @Nullable Point getBasicTargetPoint() {
        return basicTargetPoint;
    }

    /**
     * if action is FIRE, gets the targets of the first additional effect
     *
     * @return targets of first additional effect
     */
    public @Nullable List<UUID> getFirstAdditionalTarget() {
        return firstAdditionalTarget;
    }

    /**
     * if action is FIRE, gets a point, which meaning depends on the specific weapon
     *
     * @return a point
     */
    public @Nullable Point getFirstAdditionalTargetPoint() {
        return firstAdditionalTargetPoint;
    }

    /**
     * if action is FIRE, gets the targets of the second additional effect
     *
     * @return targets of second additional effect
     */
    public @Nullable List<UUID> getSecondAdditionalTarget() {
        return secondAdditionalTarget;
    }

    /**
     * if action is FIRE, gets a point, which meaning depends on the specific weapon
     *
     * @return a point
     */
    public @Nullable Point getSecondAdditionalTargetPoint() {
        return secondAdditionalTargetPoint;
    }

    /**
     * if action is USE_POWER_UP..
     *
     * @return the target of the power up
     */
    public @Nullable UUID getTarget() {
        return target;
    }

    /**
     * Each type of this enum represents a specific action that a player can do in the game
     */
    public enum Type implements Serializable {
        MOVE, GRAB_WEAPON, GRAB_AMMOCARD, FIRE, USE_POWER_UP, RELOAD, NEXT_TURN, REBORN
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
         * @param color       the color of the power up a player wants to use
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