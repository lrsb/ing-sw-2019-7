package it.polimi.ingsw.client.controllers;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.client.controllers.base.BaseViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.views.interpolator.ExponentialInterpolator;
import it.polimi.ingsw.client.views.interpolator.LinearInterpolator;
import it.polimi.ingsw.client.views.sprite.Sprite;
import it.polimi.ingsw.client.views.sprite.SpriteBoard;
import it.polimi.ingsw.client.views.sprite.SpriteBoardListener;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GameViewController extends BaseViewController implements SpriteBoardListener {
    private JPanel panel;
    private SpriteBoard spriteBoard;

    public GameViewController(@NotNull NavigationController navigationController) {
        super(1300, 800, navigationController);
        setContentPane(panel);
        spriteBoard.setBoardListener(this);
        try {
            var sprite = new Sprite(0, 0, new Dimension(100, 100), () -> ImageIO.read(Client.class.getResourceAsStream("am.png")));
            var sprite2 = new Sprite(0, 0, new Dimension(100, 150), () -> ImageIO.read(Client.class.getResourceAsStream("vv.png")));
            var sprite3 = new Sprite(400, 400, new Dimension(100, 150), () -> ImageIO.read(Client.class.getResourceAsStream("vv.png")));
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
