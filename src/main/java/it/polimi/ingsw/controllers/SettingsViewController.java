package it.polimi.ingsw.controllers;

import it.polimi.ingsw.controllers.base.BaseViewController;
import it.polimi.ingsw.controllers.base.NavigationController;

import javax.swing.*;

public class SettingsViewController extends BaseViewController {
    public JPanel panel;
    public JButton button1;
    public JButton muteButton;
    public JButton button3;
    public JButton button4;
    public JTextField ServerIP;
    public JButton backButton;

    public SettingsViewController(NavigationController navigationController) {
        super(400, 300, navigationController);
        setContentPane(panel);
        backButton.addActionListener(e -> getNavigationController().popViewController());
    }
}
