package it.polimi.ingsw.client.controllers;

import it.polimi.ingsw.client.controllers.base.BaseViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MainViewController extends BaseViewController {
    public JPanel panel;
    public JButton elencoPartiteButton;
    public JButton nuovaPartitaButton;
    public JButton opzioniButton;
    public JButton CLIButton;

    public MainViewController(@NotNull NavigationController navigationController) {
        super(400, 300, navigationController);
        setContentPane(panel);
        nuovaPartitaButton.addActionListener(e -> getNavigationController().presentViewController(GameViewController.class));
        elencoPartiteButton.addActionListener(e -> getNavigationController().presentViewController(GamesListViewController.class));
        opzioniButton.addActionListener(e -> getNavigationController().presentViewController(SettingsViewController.class));
    }
}