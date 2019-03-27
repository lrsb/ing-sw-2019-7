package it.polimi.ingsw.views;

import javax.swing.*;

public class SettingsFrame extends JNavigationFrame {
    private JPanel panel;
    private JButton button1;
    private JButton muteButton;
    private JButton button3;
    private JButton button4;
    private JTextField ServerIP;
    private JButton backButton;

    public SettingsFrame() {
        super(400, 300, false);
        setContentPane(panel);
        button1.addActionListener(e -> JOptionPane.showMessageDialog(null, "adrenailne merda"));
        pack();
    }
}
