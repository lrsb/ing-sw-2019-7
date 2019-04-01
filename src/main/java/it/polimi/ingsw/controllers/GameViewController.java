package it.polimi.ingsw.controllers;

import it.polimi.ingsw.controllers.base.BaseViewController;
import it.polimi.ingsw.graphics.sprite.Sprite;
import it.polimi.ingsw.graphics.sprite.SpriteBoardListener;
import it.polimi.ingsw.views.GameFrame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class GameViewController extends BaseViewController<GameFrame> implements SpriteBoardListener {
    public GameViewController() {
        super(new GameFrame());
        getFrame().getSpriteBoard().setBoardListener(this);
        try {
            var sprite = new Sprite(0, 0, new Dimension(100, 150), () -> {
                InputStream inputStream = GameFrame.class.getResourceAsStream("vv.png");
                return ImageIO.read(inputStream);
            });
            var sprite2 = new Sprite(300, 300, new Dimension(100, 150), () -> {
                InputStream inputStream = GameFrame.class.getResourceAsStream("vv.png");
                return ImageIO.read(inputStream);
            });
            sprite.setDraggable(true);
            sprite2.setDraggable(true);
            getFrame().getSpriteBoard().addSprite(sprite);
            getFrame().getSpriteBoard().addSprite(sprite2);
            sprite.moveTo(300, 300, 1500);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSpriteClicked(Sprite sprite) {

    }

    @Override
    public void onSpriteDragged(Sprite sprite) {

    }
}