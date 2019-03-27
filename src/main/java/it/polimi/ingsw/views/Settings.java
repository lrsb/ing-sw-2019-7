package it.polimi.ingsw.views;

import javax.swing.*;

public class Settings extends JFrame {
    private JPanel panel;
    private JButton button1;
    private JButton muteButton;
    private JButton button3;
    private JButton button4;
    private JTextField ServerIP;

    public Settings() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 400, 300);
        setContentPane(panel);
        button1.addActionListener(e -> JOptionPane.showMessageDialog(null, "adrenailne merda"));
        pack();
    }
}
