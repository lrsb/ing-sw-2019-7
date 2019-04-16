package it.polimi.ingsw.client.controllers;

import it.polimi.ingsw.client.controllers.base.BaseViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.views.boards.PlayerBoard;
import it.polimi.ingsw.common.models.Player;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Optional;

public class PlayerBoardViewController extends BaseViewController {
    public static final int HEIGHT = 200;
    public static final int WIDTH = (int) (HEIGHT * 4.06);

    private JPanel panel;
    private PlayerBoard playerBoard;
    private GameViewController gameViewController;

    public PlayerBoardViewController(@NotNull NavigationController navigationController) {
        super(WIDTH, HEIGHT, navigationController);
        setContentPane(panel);
    }

    @Override
    public void controllerPopped() {
        Optional.ofNullable(gameViewController).ifPresent(GameViewController::playerBoardViewControllerPopped);
    }

    public void setGameViewController(GameViewController gameViewController) {
        this.gameViewController = gameViewController;
    }

    private void createUIComponents() throws IOException {
        playerBoard = new PlayerBoard(new Dimension(WIDTH, (int) (HEIGHT * 0.89)), new Player("ciao"));
    }
}