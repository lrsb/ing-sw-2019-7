package it.polimi.ingsw.client.controllers;

import it.polimi.ingsw.client.controllers.base.BaseViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GamesListViewController extends BaseViewController {
    private JPanel panel;
    private JTextField cercaTextField;
    private JButton helpButton;
    private JTable table1;

    public GamesListViewController(@NotNull NavigationController navigationController) {
        super(800, 600, navigationController);
        setContentPane(panel);
    }
}