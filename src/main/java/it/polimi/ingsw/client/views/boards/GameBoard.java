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
    private final @NotNull Dimension WEAPON_DIMEMSION = transformDim(0.09, 0.2);
    private final @NotNull Dimension POWERUP_DIMEMSION = transformDim(0.068, 0.13);
    private final @NotNull Sprite weapon;

    private final @NotNull Point WEAPON_POSITION = transformPoint(0.879, 0.26);
    private final @NotNull Point POWERUP_POSITION = transformPoint(0.902, 0.05);
    private final @NotNull Sprite powerup;

    private final @NotNull Dimension AMMOCARD_DIMEMSION = transformDim(0.07, 0.1);
    private final @NotNull Point AMMOCARD_POSITION = transformPoint(0.015, 0.88);
    private final @NotNull Sprite ammoCard;

    private @NotNull Game game;

    public GameBoard(@NotNull Dimension dimension, @NotNull Game game) throws IOException {
        super(dimension, Utils.joinBufferedImage(game.getBackImage(), game.getFrontImage()));
        this.game = game;

        weapon = new Sprite(WEAPON_POSITION, WEAPON_DIMEMSION, Utils.readPngImage(Weapon.class, "back"));
        weapon.setDraggable(true);
        weapon.setTag("weapon");
        addSprite(weapon);

        powerup = new Sprite(POWERUP_POSITION, POWERUP_DIMEMSION, Utils.readPngImage(PowerUp.class, "back"));
        powerup.setDraggable(true);
        powerup.setTag("powerup");
        addSprite(powerup);

        ammoCard = new Sprite(AMMOCARD_POSITION, AMMOCARD_DIMEMSION, Utils.readPngImage(AmmoCard.class, "back"));
        ammoCard.setDraggable(true);
        ammoCard.setTag("ammocard");
        addSprite(ammoCard);
    }

    public void updateGame(@NotNull Game game) {
        this.game = game;
        //TODO: update board
    }

    @Override
    public void onSpriteClicked(@NotNull Sprite sprite) {

    }

    @Override
    public void onSpriteDragged(@NotNull Sprite sprite) {
        if (sprite.getTag() != null) switch (sprite.getTag()) {
            case "weapon":
                sprite.moveTo(new LinearInterpolator(sprite.getPosition(), WEAPON_POSITION, 250) {
                });
                return;
            case "powerup":
                sprite.moveTo(new LinearInterpolator(sprite.getPosition(), POWERUP_POSITION, 250) {
                });
                return;
            case "ammocard":
                sprite.moveTo(new LinearInterpolator(sprite.getPosition(), AMMOCARD_POSITION, 250) {
                });
                return;
            default:
                //super.onSpriteDragged(sprite);
        }
    }
}