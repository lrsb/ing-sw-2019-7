package it.polimi.ingsw.controllers;

import it.polimi.ingsw.library.BaseViewController;
import it.polimi.ingsw.views.MainFrame;

public class MainViewController extends BaseViewController<MainFrame> {
    public MainViewController() {
        super(new MainFrame());
        getFrame().nuovaPartitaButton.addActionListener(e -> getNavigationController().presentViewController(new GameViewController()));
        getFrame().elencoPartiteButton.addActionListener(e -> getNavigationController().presentViewController(new GameListViewController()));
        getFrame().opzioniButton.addActionListener(e -> getNavigationController().presentViewController(new SettingsViewController()));
    }
}
