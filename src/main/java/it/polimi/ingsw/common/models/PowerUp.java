package it.polimi.ingsw.common.models;

import it.polimi.ingsw.client.others.Utils;
import it.polimi.ingsw.client.views.sprite.Displayable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.stream.Stream;

/**
 * Each powerup card is composed in 2 parts: it has a specific type and a color.
 * For each type you have 3 card available, one for each color.
 */
public class PowerUp implements Displayable, Serializable {
    private static final long serialVersionUID = 1;

    private final @NotNull AmmoCard.Color ammoColor;
    private final @NotNull Type type;
    private final @NotNull Game game;
    private @Nullable Player target;
    private @Nullable Point targetPoint;


    /**
     * PowerUp constructor.
     *
     * @param ammoColor Indicate the color of the card.
     * @param type      Indicate the the type of the powerUp card ( ex: Teleporter).
     */
    @Contract(pure = true)
    public PowerUp(@NotNull AmmoCard.Color ammoColor, @NotNull Type type, @NotNull Game game) {
        this.ammoColor = ammoColor;
        this.type = type;
        this.game = game;
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

    public void addTarget(@NotNull Player target) {
        this.target = target;
    }

    public void addTargetPoint(@NotNull Point targetPoint) {
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
     * Return true if two powerUp cards are equals.
     *
     * @param obj The object that you want to compare.
     * @return True if the cards are equals, else otherwise.
     */
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj) {
        return obj instanceof PowerUp && ammoColor == ((PowerUp) obj).ammoColor && type == ((PowerUp) obj).type;
    }

    /**
     * {@link PowerUp} Type enum
     */
    public enum Type {
        TARGETING_SCOPE, NEWTON, TAGBACK_GRENADE, TELEPORTER
    }

    public boolean use() {
        if ((this.getType() == Type.TAGBACK_GRENADE && target.hasPowerUp(this) ||
                game.getActualPlayer().hasPowerUp(this)) && canBeUsed()) effect();
        else return false;
        return true;
    }

    private boolean canBeUsed() {
        switch (getType()) {
            case TARGETING_SCOPE:
                return game.getLastsDamaged().contains(target);
            case NEWTON:
                return game.getPlayers().contains(target) && !game.getActualPlayer().equals(target) &&
                        !target.getPosition().equals(targetPoint) && Stream.of(Bounds.Direction.values())
                        .anyMatch(e -> target.isPointAtMaxDistanceInDirection(targetPoint, game.getCells(), 2, e));
            case TAGBACK_GRENADE:
                return game.getTagbackPlayers().contains(target);
            case TELEPORTER:
                //TODO: controlla -> metto un numero esagerato di passi per verificare che non inserisca una casella fuori da game
                return targetPoint != null && game.canMove(game.getActualPlayer().getPosition(), targetPoint, 20);
        }
        return false;
    }

    private void effect() {
        switch (getType()) {
            case TARGETING_SCOPE:
                if (target.getDamagesTaken().size() < 12) target.getDamagesTaken().add(game.getActualPlayer().getUuid());
                break;
            case NEWTON:
                target.setPosition(targetPoint);
                break;
            case TAGBACK_GRENADE:
                //TODO: controlla -> qui dico che target è chi lo usa!!!
                if (game.getActualPlayer().getMarksTaken().stream().filter(e -> e.equals(target.getUuid())).count() < 3)
                    game.getActualPlayer().getMarksTaken().add(target.getUuid());
                target.removePowerUp(this);
                return;
            case TELEPORTER:
                game.getActualPlayer().setPosition(targetPoint);
                break;
        }
        game.getActualPlayer().removePowerUp(this);
    }
}