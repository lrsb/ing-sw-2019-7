package it.polimi.ingsw.controllers;

import it.polimi.ingsw.controllers.base.BaseViewController;
import it.polimi.ingsw.graphics.interpolator.ExponentialInterpolator;
import it.polimi.ingsw.graphics.interpolator.LinearInterpolator;
import it.polimi.ingsw.graphics.sprite.Sprite;
import it.polimi.ingsw.graphics.sprite.SpriteBoardListener;
import it.polimi.ingsw.views.GameFrame;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class GameViewController extends BaseViewController<GameFrame> implements SpriteBoardListener {
    public GameViewController() {
        super(new GameFrame());
        getFrame().getSpriteBoard().setBoardListener(this);
        try {
            var sprite = new Sprite(0, 0, new Dimension(100, 100), () -> {
                InputStream inputStream = GameFrame.class.getResourceAsStream("am.png");
                return ImageIO.read(inputStream);
            });
            var sprite2 = new Sprite(0, 0, new Dimension(100, 150), () -> {
                InputStream inputStream = GameFrame.class.getResourceAsStream("vv.png");
                return ImageIO.read(inputStream);
            });
            var sprite3 = new Sprite(400, 400, new Dimension(100, 150), () -> {
                InputStream inputStream = GameFrame.class.getResourceAsStream("vv.png");
                return ImageIO.read(inputStream);
            });
            sprite.setDraggable(true);
            sprite2.setDraggable(true);
            sprite3.setDraggable(true);
            getFrame().getSpriteBoard().addSprite(sprite);
            getFrame().getSpriteBoard().addSprite(sprite2);
            getFrame().getSpriteBoard().addSprite(sprite3);
            sprite.moveTo(new LinearInterpolator(sprite.getPosition(), System.currentTimeMillis(), new Point(300, 300), 2000) {
                @Override
                public void onInterpolationCompleted() {
                    //sprite.remove();
                }
            });
            sprite2.moveTo(new ExponentialInterpolator(sprite.getPosition(), System.currentTimeMillis(), new Point(500, 300), 2000) {
                @Override
                public void onInterpolationCompleted() {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSpriteClicked(@NotNull Sprite sprite) {

    }

    @Override
    public void onSpriteDragged(@NotNull Sprite sprite) {

    }
}