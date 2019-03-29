package it.polimi.ingsw.views;

import it.polimi.ingsw.library.JNavigationFrame;

import javax.swing.*;

public class MainFrame extends JNavigationFrame {
    public JPanel panel;
    public JButton elencoPartiteButton;
    public JButton nuovaPartitaButton;
    public JButton opzioniButton;
    public JButton CLIButton;

    public MainFrame() {
        super(400, 300, true);
        setContentPane(panel);
    }
}