package it.polimi.ingsw.client.controllers;

import it.polimi.ingsw.client.controllers.base.BaseViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.views.sprite.Sprite;
import it.polimi.ingsw.client.views.sprite.SpriteBoard;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;

public class MainViewController extends BaseViewController {
    private JPanel panel;
    private SpriteBoard logo;
    private JButton elencoPartiteButton;
    private JButton nuovaPartitaButton;
    private JButton opzioniButton;
    private JButton CLIButton;

    public MainViewController(@NotNull NavigationController navigationController) throws IOException {
        super("Benvenuto", 400, 300, navigationController);
        setContentPane(panel);
        logo.addSprite(new Sprite(10, 40, 370, 70, ImageIO.read(MainViewController.class.getResourceAsStream("MainViewController/logo.png"))));
        nuovaPartitaButton.addActionListener(e -> getNavigationController().presentViewController(GameViewController.class));
        elencoPartiteButton.addActionListener(e -> getNavigationController().presentViewController(GamesListViewController.class));
        opzioniButton.addActionListener(e -> getNavigationController().presentViewController(SettingsViewController.class));
    }
}