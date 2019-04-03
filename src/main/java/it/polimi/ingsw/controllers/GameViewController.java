package it.polimi.ingsw.controllers;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.controllers.base.BaseViewController;
import it.polimi.ingsw.controllers.base.NavigationController;
import it.polimi.ingsw.views.interpolator.ExponentialInterpolator;
import it.polimi.ingsw.views.interpolator.LinearInterpolator;
import it.polimi.ingsw.views.sprite.Sprite;
import it.polimi.ingsw.views.sprite.SpriteBoard;
import it.polimi.ingsw.views.sprite.SpriteBoardListener;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class GameViewController extends BaseViewController implements SpriteBoardListener {
    private JPanel panel;
    private SpriteBoard spriteBoard;

    public GameViewController(@NotNull NavigationController navigationController) {
        super(1300, 800, navigationController);
        setContentPane(panel);
        spriteBoard.setBoardListener(this);
        try {
            var sprite = new Sprite(0, 0, new Dimension(100, 100), () -> ImageIO.read(Client.class.getResourceAsStream("am.png")));
            var sprite2 = new Sprite(0, 0, new Dimension(100, 150), () -> {
                InputStream inputStream = Client.class.getResourceAsStream("vv.png");
                return ImageIO.read(inputStream);
            });
            var sprite3 = new Sprite(400, 400, new Dimension(100, 150), () -> {
                InputStream inputStream = Client.class.getResourceAsStream("vv.png");
                return ImageIO.read(inputStream);
            });
            sprite.setDraggable(true);
            sprite2.setDraggable(true);
            sprite3.setDraggable(true);
            spriteBoard.addSprite(sprite);
            spriteBoard.addSprite(sprite2);
            spriteBoard.addSprite(sprite3);
            sprite.moveTo(new LinearInterpolator(sprite.getPosition(), System.currentTimeMillis(), new Point(300, 300), 2000) {
                @Override
                public void onInterpolationCompleted() {
                    sprite.remove();
                }
            });
            sprite2.moveTo(new ExponentialInterpolator(sprite.getPosition(), System.currentTimeMillis(), new Point(500, 300), 2000) {
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

    private void createUIComponents() {
        spriteBoard = new SpriteBoard();
    }
}
