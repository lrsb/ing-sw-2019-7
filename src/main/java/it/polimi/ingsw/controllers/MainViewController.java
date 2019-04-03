package it.polimi.ingsw.controllers;

import it.polimi.ingsw.controllers.base.BaseViewController;
import it.polimi.ingsw.controllers.base.NavigationController;
import it.polimi.ingsw.models.client.HandyMannySocketImpl;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;

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
        elencoPartiteButton.addActionListener(e -> {
            try {
                var eee = new HandyMannySocketImpl("localhost");
                eee.createRoom("wfrln");
            } catch (IOException er) {
                er.printStackTrace();
            }
            //getNavigationController().presentViewController(GamesListViewController.class);
        });
        opzioniButton.addActionListener(e -> {
            try {
                var eee = new HandyMannySocketImpl("localhost");
                System.out.println(eee.getRooms());
            } catch (IOException er) {
                er.printStackTrace();
            }
            //getNavigationController().presentViewController(SettingsViewController.class);
        });
        CLIButton.addActionListener(e -> getNavigationController().presentViewController(ProvaViewController.class));
    }

    @Override
    public void nextViewControllerInstantiated(BaseViewController viewController) {
        if (viewController instanceof ProvaViewController) ((ProvaViewController) viewController).setName1("ciao");
    }
}