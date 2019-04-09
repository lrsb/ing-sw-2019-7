package it.polimi.ingsw.client.controllers;

import it.polimi.ingsw.client.controllers.base.BaseViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.views.sprite.Sprite;
import it.polimi.ingsw.client.views.sprite.SpriteBoard;
import it.polimi.ingsw.client.views.sprite.SpriteBoardListener;
import it.polimi.ingsw.common.models.Deck;
import it.polimi.ingsw.common.models.exceptions.EmptyDeckException;
import org.jetbrains.annotations.NotNull;

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
            var deck = Deck.Creator.newAmmoDeck();
            for (int i = 0; i < 10; i++) {
                try {
                    var card = deck.discardCard();
                    var sprite = new Sprite(0, 0, new Dimension(100, 100), card);
                    sprite.setDraggable(true);
                    spriteBoard.addSprite(sprite);
                } catch (EmptyDeckException e) {
                    e.printStackTrace();
                }
            }
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
