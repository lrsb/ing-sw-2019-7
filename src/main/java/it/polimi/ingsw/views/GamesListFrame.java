package it.polimi.ingsw.views;

import it.polimi.ingsw.library.JNavigationFrame;

import javax.swing.*;

public class GamesListFrame extends JNavigationFrame {
    private JPanel panel;
    private JTextField cercaTextField;
    private JButton helpButton;
    private JTable table1;

    public GamesListFrame() {
        super(800, 600, false);
        setContentPane(panel);
    }
}
