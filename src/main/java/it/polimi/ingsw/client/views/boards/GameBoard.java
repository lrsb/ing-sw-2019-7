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

public class GameBoard extends AbstractBoard {
    private @NotNull Game game;

    public GameBoard(@NotNull Dimension dimension, @NotNull Game game) throws IOException {
        super(dimension, Utils.joinBufferedImage(game.getBackImage(), game.getFrontImage()));
        this.game = game;

        var weapon = new Sprite(966, 208, 99, 160, Utils.readPngImage(Weapon.class, "back"));
        weapon.setDraggable(true);
        weapon.setTag("weapon");
        addSprite(weapon);

        var powerup = new Sprite(992, 40, 74, 104, Utils.readPngImage(PowerUp.class, "back"));
        powerup.setDraggable(true);
        powerup.setTag("powerup");
        addSprite(powerup);

        var ammoCard = new Sprite(16, 650, 80, 80, Utils.readPngImage(AmmoCard.class, "back"));
        ammoCard.setDraggable(true);
        //ammoCard.setTag("ammocard");
        addSprite(ammoCard);
    }

    public void updateGame(@NotNull Game game) {
        this.game = game;
        //TODO: update board
    }

    @Override
    public void onSpriteClicked(@NotNull Sprite sprite) {

    }

    private void spriteMovedTo(@NotNull Sprite sprite, @NotNull Point point) {
        sprite.moveTo(new LinearInterpolator(sprite.getPosition(), new Point((int) (210 + point.getX() * 172.5), (int) (180 + point.getY() * 150)), 250) {
        });
    }

    @Override
    public void onSpriteDragged(@NotNull Sprite sprite) {
        if (sprite.getTag() != null) switch (sprite.getTag()) {
            case "weapon":
                sprite.moveTo(new LinearInterpolator(sprite.getPosition(), new Point(966, 208), 250) {
                });
                return;
            case "powerup":
                sprite.moveTo(new LinearInterpolator(sprite.getPosition(), new Point(992, 40), 250) {
                });
                return;
            case "ammocard":
                sprite.moveTo(new LinearInterpolator(sprite.getPosition(), new Point(16, 650), 250) {
                });
                return;
            default:
                if (sprite.getX() > 210 && sprite.getX() + sprite.getDimension().getWidth() < 900 &&
                        sprite.getY() > 180 && sprite.getY() + sprite.getDimension().getHeight() < 630)
                    spriteMovedTo(sprite, new Point((int) ((sprite.getX() - 210 + sprite.getDimension().getWidth() / 2) / 172.5),
                            (sprite.getY() - 180) / 150));
        }
        else {
            if (sprite.getX() > 210 && sprite.getX() + sprite.getDimension().getWidth() < 900 &&
                    sprite.getY() > 180 && sprite.getY() + sprite.getDimension().getHeight() < 630)
                spriteMovedTo(sprite, new Point((int) ((sprite.getX() - 210 + sprite.getDimension().getWidth() / 2) / 172.5),
                        (sprite.getY() - 180) / 150));
        }
    }

    //ne = 217, 182
    //nw = 902, 177
    //se = 202, 636
    //sw = 900, 627
}