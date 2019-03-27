package it.polimi.ingsw.views;

import javax.swing.*;

public class MainFrame extends JNavigationFrame {
    private JPanel panel;
    private JButton elencoPartiteButton;
    private JButton nuovaPartitaButton;
    private JButton opzioniButton;
    private JButton CLIButton;

    public MainFrame(SettingsFrame settingsFrame) {
        super(400, 300, true);
        setContentPane(panel);
        var gameFrame = new GameFrame();
        elencoPartiteButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "adrenailne merda"));
        nuovaPartitaButton.addActionListener(e -> gameFrame.setVisible(true));
        opzioniButton.addActionListener(e -> settingsFrame.setVisible(true));
        pack();
    }
}
