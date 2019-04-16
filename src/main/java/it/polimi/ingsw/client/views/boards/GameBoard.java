package it.polimi.ingsw.client.views.boards;

import it.polimi.ingsw.client.views.sprite.Sprite;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Weapon;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class GameBoard extends AbstractBoard {
    private final @NotNull Game game;

    public GameBoard(@NotNull Dimension dimension, @NotNull Game game) throws IOException {
        super(dimension);
        this.game = game;

        addSprite(new Sprite(0, 0, new Dimension(width / 2, height), () -> ImageIO.read(Game.class.getResourceAsStream("Game/" + game.getType().getLeft() + ".png"))));
        addSprite(new Sprite(dimension.width / 2, 0, new Dimension(width / 2, height), () -> ImageIO.read(Game.class.getResourceAsStream("Game/" + game.getType().getRight() + ".png"))));

        var point = transformPoint(0.877, 0.269);
        var weapon = new Sprite(point.x, point.y, transformDim(Const.WEAPON_WIDTH, Const.WEAPON_HEIGHT), () -> ImageIO.read(Weapon.class.getResourceAsStream("Weapon/" + "02" + ".png")));
        weapon.setDraggable(true);
        addSprite(weapon);
    }

    @Override
    public void onSpriteDragged(@NotNull Sprite sprite) {
        sprite.moveTo(sprite.getX() / 50 * 50, sprite.getY() / 50 * 50);
        //se vuoi che si propaghi al controller
        super.onSpriteDragged(sprite);
    }

    private static class Const {
        private static double WEAPON_WIDTH = 0.091;
        private static double WEAPON_HEIGHT = 0.214;
    }
}