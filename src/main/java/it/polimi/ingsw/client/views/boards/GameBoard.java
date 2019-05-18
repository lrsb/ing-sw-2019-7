package it.polimi.ingsw.client.views.boards;

import it.polimi.ingsw.client.others.Utils;
import it.polimi.ingsw.client.views.sprite.Sprite;
import it.polimi.ingsw.client.views.sprite.interpolators.LinearInterpolator;
import it.polimi.ingsw.common.models.AmmoCard;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.PowerUp;
import it.polimi.ingsw.common.models.Weapon;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;

/**
 * The type Game board.
 */
public class GameBoard extends AbstractBoard {
    private @NotNull Game game;

    /**
     * Instantiates a new Game board.
     *
     * @param dimension the dimension
     * @param game      the game
     * @throws IOException the io exception
     */
    public GameBoard(@NotNull Dimension dimension, @NotNull Game game) throws IOException {
        super(dimension, Utils.joinBufferedImage(game.getBackImage(), game.getFrontImage()));
        this.game = game;

        var weapon = new Sprite(1052, 227, 108, 177, Utils.readPngImage(Weapon.class, "back"));
        weapon.setDraggable(true);
        weapon.setTag("weapon");
        addSprite(weapon);

        var powerup = new Sprite(1081, 46, 81, 115, Utils.readPngImage(PowerUp.class, "back"));
        powerup.setDraggable(true);
        powerup.setTag("powerup");
        addSprite(powerup);

        var ammoCard = new Sprite(57, 725, 84, 84, Utils.readPngImage(AmmoCard.class, "back"));
        ammoCard.setDraggable(true);
        ammoCard.setTag("ammocard");
        addSprite(ammoCard);
    }

    /**
     * Update game.
     *
     * @param game the game
     */
    public void updateGame(@NotNull Game game) {
        this.game = game;
        //TODO: update board
    }

    @Override
    public void onSpriteClicked(@NotNull Sprite sprite) {
        //TODO
    }

    @Override
    public void onSpriteDragged(@NotNull Sprite sprite) {
        if (sprite.getTag() != null) switch (sprite.getTag()) {
            case "weapon":
                sprite.moveTo(new LinearInterpolator(sprite.getPosition(), new Point(1052, 227), 250) {
                });
                return;
            case "powerup":
                sprite.moveTo(new LinearInterpolator(sprite.getPosition(), new Point(1081, 46), 250) {
                });
                return;
            case "ammocard":
                sprite.moveTo(new LinearInterpolator(sprite.getPosition(), new Point(57, 725), 250) {
                });
                return;
            default:
                if (sprite.getX() > 205 && sprite.getX() + sprite.getDimension().getWidth() / 2 < 994 &&
                        sprite.getY() > 175 && sprite.getY() + sprite.getDimension().getHeight() / 2 < 744)
                    spriteMovedTo(sprite, new Point((int) ((sprite.getX() + sprite.getDimension().getWidth() / 2 - 205) / 220),
                            (int) ((sprite.getY() + sprite.getDimension().getWidth() / 2 - 175) / 190)));
        }
    }

    private void spriteMovedTo(@NotNull Sprite sprite, @NotNull Point point) {
        sprite.moveTo(new LinearInterpolator(sprite.getPosition(), new Point((int) (250 + point.getX() * 220), (int) (210 + point.getY() * 190)), 250) {
        });
    }
}