package it.polimi.ingsw.client.views.boards;

import it.polimi.ingsw.client.views.sprite.Sprite;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.PowerUp;
import it.polimi.ingsw.common.models.Weapon;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class GameBoard extends AbstractBoard {
    private @NotNull Dimension WEAPON_DIMEMSION = transformDim(0.091, 0.214);
    private @NotNull Dimension POWERUP_DIMEMSION = transformDim(0.068, 0.136);

    private @NotNull Point WEAPON_POSITION = transformPoint(0.877, 0.269);
    private @NotNull Point POWERUP_POSITION = transformPoint(0.902, 0.055);

    private final @NotNull Game game;

    public GameBoard(@NotNull Dimension dimension, @NotNull Game game) throws IOException {
        super(dimension);
        this.game = game;

        addSprite(new Sprite(0, 0, new Dimension(width / 2, height), () -> ImageIO.read(Game.class.getResourceAsStream("Game/" + game.getType().getLeft() + ".png"))));
        addSprite(new Sprite(dimension.width / 2, 0, new Dimension(width / 2, height), () -> ImageIO.read(Game.class.getResourceAsStream("Game/" + game.getType().getRight() + ".png"))));

        var weapon = new Sprite(WEAPON_POSITION.x, WEAPON_POSITION.y, WEAPON_DIMEMSION, () -> ImageIO.read(Weapon.class.getResourceAsStream("Weapon/" + "back" + ".png")));
        weapon.setDraggable(true);
        weapon.setTag("weapon");
        addSprite(weapon);

        var powerup = new Sprite(POWERUP_POSITION.x, POWERUP_POSITION.y, POWERUP_DIMEMSION, () -> ImageIO.read(PowerUp.class.getResourceAsStream("PowerUp/" + "back" + ".png")));
        powerup.setDraggable(true);
        powerup.setTag("powerup");
        addSprite(powerup);
    }

    @Override
    public void onSpriteDragged(@NotNull Sprite sprite) {
        if (sprite.getTag() == null) super.onSpriteDragged(sprite);
        else switch (sprite.getTag()) {
            case "weapon":
                sprite.moveTo(WEAPON_POSITION);
                return;
            case "powerup":
                sprite.moveTo(POWERUP_POSITION);
                return;
            default:
                super.onSpriteDragged(sprite);
        }
    }
}