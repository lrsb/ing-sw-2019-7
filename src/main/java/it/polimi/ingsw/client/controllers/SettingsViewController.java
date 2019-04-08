package it.polimi.ingsw.client.controllers;

import it.polimi.ingsw.client.controllers.base.BaseViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class SettingsViewController extends BaseViewController {
    private JPanel panel;
    private JButton button1;
    private JButton muteButton;
    private JButton button3;
    private JButton button4;
    private JTextField ServerIP;
    private JButton backButton;

    public SettingsViewController(@NotNull NavigationController navigationController) {
        super(400, 300, navigationController);
        setContentPane(panel);
        backButton.addActionListener(e -> getNavigationController().popViewController());
    }
}