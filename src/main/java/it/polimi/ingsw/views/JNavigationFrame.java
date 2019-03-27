package it.polimi.ingsw.views;

import javax.swing.*;
import java.awt.*;

class JNavigationFrame extends JFrame {
    JNavigationFrame(int width, int height, boolean isInitialFrame) {
        setDefaultCloseOperation(isInitialFrame ? WindowConstants.EXIT_ON_CLOSE : WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - width / 2, Toolkit.getDefaultToolkit().getScreenSize().height / 2 - height / 2, width, height);
        setResizable(false);
    }
}