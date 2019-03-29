package it.polimi.ingsw.views;

import it.polimi.ingsw.library.JNavigationFrame;

import javax.swing.*;

public class GameFrame extends JNavigationFrame {
    private JPanel panel;
    private JLabel gameLabel;

    public GameFrame() {
        super(1000, 800, false);
        setContentPane(panel);
    }

    public JLabel getGameLabel() {
        return gameLabel;
    }
}
