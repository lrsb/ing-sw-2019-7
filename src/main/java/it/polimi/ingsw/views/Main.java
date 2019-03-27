package it.polimi.ingsw.views;

import javax.swing.*;

public class Main extends JFrame {
    private JPanel panel;
    private JButton elencoPartiteButton;
    private JButton nuovaPartitaButton;
    private JButton opzioniButton;
    private JButton CLIButton;

    public Main(Settings settings) {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 300);
        setContentPane(panel);
        elencoPartiteButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "adrenailne merda"));
        opzioniButton.addActionListener(e -> settings.setVisible(true));
        pack();
    }
}
