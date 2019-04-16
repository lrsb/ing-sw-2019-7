package it.polimi.ingsw.client.controllers;

import it.polimi.ingsw.client.controllers.base.BaseViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MainViewController extends BaseViewController {
    private JPanel panel;
    private JButton elencoPartiteButton;
    private JButton nuovaPartitaButton;
    private JButton opzioniButton;
    private JButton CLIButton;

    public MainViewController(@NotNull NavigationController navigationController) {
        super("Benvenuto", 400, 300, navigationController);
        setContentPane(panel);
        nuovaPartitaButton.addActionListener(e -> getNavigationController().presentViewController(GameViewController.class));
        elencoPartiteButton.addActionListener(e -> getNavigationController().presentViewController(GamesListViewController.class));
        opzioniButton.addActionListener(e -> getNavigationController().presentViewController(SettingsViewController.class));
    }
}