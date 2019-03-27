package it.polimi.ingsw.views;

import javax.swing.*;
import java.awt.*;

public class Main {
    private JPanel panel;
    private JButton elencoPartiteButton;
    private JButton nuovaPartitaButton;
    private JButton opzioniButton;
    private JButton CLIButton;

    public static void main(String[] args) {
        var frame = new JFrame("Main");
        var main = new Main();
        frame.setContentPane(main.panel);
        frame.setPreferredSize(new Dimension(400, 300));
        main.elencoPartiteButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "adrenailne merda"));
        main.opzioniButton.addActionListener();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
