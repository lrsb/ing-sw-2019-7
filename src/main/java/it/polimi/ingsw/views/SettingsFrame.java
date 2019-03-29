package it.polimi.ingsw.views;

import it.polimi.ingsw.library.JNavigationFrame;

import javax.swing.*;

public class SettingsFrame extends JNavigationFrame {
    public JPanel panel;
    public JButton button1;
    public JButton muteButton;
    public JButton button3;
    public JButton button4;
    public JTextField ServerIP;
    public JButton backButton;

    public SettingsFrame() {
        super(400, 300, false);
        setContentPane(panel);
    }
}
