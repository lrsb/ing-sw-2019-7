package it.polimi.ingsw.views;

import javax.swing.*;

public class Main {
    private JButton button1;
    private JPanel panel1;

    public static void main(String[] args) {
        var frame = new JFrame("Main");
        var main = new Main();
        frame.setContentPane(main.panel1);
        main.button1.addActionListener(e -> JOptionPane.showMessageDialog(null, "adrenailne merda"));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
