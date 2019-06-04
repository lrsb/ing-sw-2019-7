package it.polimi.ingsw.common.models;

import it.polimi.ingsw.client.others.Utils;
import it.polimi.ingsw.client.views.gui.sprite.Displayable;
import it.polimi.ingsw.common.models.modelsExceptions.PlayerNotFoundException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.stream.Stream;

/**
 * Each PowerUp card is composed in 2 parts: it has a specific type and a color.
 * For each type you have 3 card available, one for each color.
 */
public class PowerUp implements Displayable, Serializable {
    private static final long serialVersionUID = 1;

    private final @NotNull AmmoCard.Color ammoColor;
    private final @NotNull Type type;
    private @Nullable Player target;
    private @Nullable Point targetPoint;

    /**
     * PowerUp constructor.
     *
     * @param ammoColor Indicate the color of the card.
     * @param type      Indicate the the type of the powerUp card (ex: Teleporter).
     */
    @Contract(pure = true)
    public PowerUp(@NotNull AmmoCard.Color ammoColor, @NotNull Type type) {
        this.ammoColor = ammoColor;
        this.type = type;
    }

    /**
     * Return the color of the powerUp card.
     *
     * @return Color of the card.
     */
    @Contract(pure = true)
    public @NotNull AmmoCard.Color getAmmoColor() {
        return ammoColor;
    }

    /**
     * Return the type of powerUp card
     *
     * @return Type of the card.
     */
    @Contract(pure = true)
    public @NotNull Type getType() {
        return type;
    }

    /**
     *
     * @param target if present, is the player who suffers the effect
     */

    public void setTarget(@Nullable Player target) {
        this.target = target;
    }

    /**
     *
     * @param targetPoint if present, defines a point which meaning changes
     *                    according to the specific type of the powerUp
     */
    public void setTargetPoint(@Nullable Point targetPoint) {
        this.targetPoint = targetPoint;
    }

    @Override
    public @NotNull BufferedImage getFrontImage() throws IOException {
        return Utils.readPngImage(getClass(), type.name().substring(0, 3) + ammoColor.name().substring(0, 1));
    }

    @Override
    public @NotNull BufferedImage getBackImage() throws IOException {
        return Utils.readPngImage(getClass(), "back");
    }

    /**
     * This method controls if two powerUps are the same one, talking about the game
     *
     * @param obj The object that you want to compare.
     * @return True if the cards are equals, false otherwise.
     */
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj) {
        return obj instanceof PowerUp && ammoColor == ((PowerUp) obj).ammoColor && type == ((PowerUp) obj).type;
    }

    /**
     * Use this method to verify if a player can use the powerUp he wants to use.
     *
     * @param game the game
     * @return true if powerUp has been used successfully, false otherwise
     */
    public boolean use(@NotNull Game game) {
        if (game.getActualPlayer().hasPowerUp(this) && canBeUsed(game)) {
            useImpl(game);
            return true;
        }
        return false;
    }

    private boolean canBeUsed(@NotNull Game game) {
        switch (getType()) {
            case TARGETING_SCOPE:
                if (target == null) throw new PlayerNotFoundException();
                return game.getLastsDamaged().contains(target.getUuid());
            case NEWTON:
                assert target != null && targetPoint != null && target.getPosition() != null;
                return game.getPlayers().contains(target) && !game.getActualPlayer().equals(target) &&
                        !target.getPosition().equals(targetPoint) && Stream.of(Bounds.Direction.values())
                        .anyMatch(e -> target.isPointAtMaxDistanceInDirection(targetPoint, game.getCells(), 2, e));
            case TAGBACK_GRENADE:
                return game.isATagbackResponse();
            case TELEPORTER:
                return game.getCell(targetPoint) != null;
        }
        return false;
    }

    private void useImpl(@NotNull Game game) {
        switch (getType()) {
            case TARGETING_SCOPE:
                if (target == null) throw new PlayerNotFoundException();
                if (target.getDamagesTaken().size() < 12)
                    target.getDamagesTaken().add(game.getActualPlayer().getUuid());
                break;
            case NEWTON:
                assert targetPoint != null && target != null : "No target or point delivery selected";
                target.setPosition(targetPoint);
                break;
            case TAGBACK_GRENADE:
                game.getTagbackedPlayer().addMark(game.getActualPlayer());
                break;
            case TELEPORTER:
                game.getActualPlayer().setPosition(targetPoint);
                break;
        }
    }

    /**
     * {@link PowerUp} Type enum
     */
    public enum Type {
        TARGETING_SCOPE, NEWTON, TAGBACK_GRENADE, TELEPORTER
    }
}