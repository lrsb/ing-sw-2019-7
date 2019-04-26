package it.polimi.ingsw.client.controllers.views;

import it.polimi.ingsw.client.controllers.views.base.BaseViewController;
import it.polimi.ingsw.client.controllers.views.base.NavigationController;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GamesListViewController extends BaseViewController {
    private JPanel panel;
    private JTextField cercaTextField;
    private JButton helpButton;
    private JTable table;

    public GamesListViewController(@NotNull NavigationController navigationController) {
        super("Elenco partite", 800, 600, navigationController);
        setContentPane(panel);
    }
}