package it.polimi.ingsw.views;

import javax.swing.*;
import java.awt.*;

public class Main {
    private JPanel panel;
    private JButton elencoPartiteButton;
    private JButton nuovaPartitaButton;
    private JButton opzioniButton;

    public static void main(String[] args) {
        var frame = new JFrame("Main");
        var main = new Main();
        frame.setContentPane(main.panel);
        frame.setPreferredSize(new Dimension(400, 300));
        main.elencoPartiteButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "adrenailne merda"));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
