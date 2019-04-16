package it.polimi.ingsw.client.controllers;

import it.polimi.ingsw.client.controllers.base.BaseViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.views.boards.GameBoard;
import it.polimi.ingsw.client.views.sprite.Sprite;
import it.polimi.ingsw.client.views.sprite.SpriteBoardListener;
import it.polimi.ingsw.common.models.Game;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class GameViewController extends BaseViewController implements SpriteBoardListener {
    public static final int HEIGHT = 800;
    public static final int WIDTH = (int) (HEIGHT * 1.375);

    private JPanel panel;
    private GameBoard gameBoard;
    private NavigationController playerBoardNavigationController = new NavigationController(PlayerBoardViewController.class);

    public GameViewController(@NotNull NavigationController navigationController) {
        super(WIDTH, HEIGHT, navigationController);
        setContentPane(panel);
    }

    @Override
    public void controllerPopped() {
        playerBoardNavigationController.close();
    }

    @Override
    public void onSpriteClicked(@NotNull Sprite sprite) {

    }

    @Override
    public void onSpriteDragged(@NotNull Sprite sprite) {

    }

    private void createUIComponents() throws IOException {
        gameBoard = new GameBoard(new Dimension(WIDTH, (int) (HEIGHT * 0.98)), Game.Creator.newGame(UUID.randomUUID(), new ArrayList<>()));
        gameBoard.setBoardListener(this);
    }
}
