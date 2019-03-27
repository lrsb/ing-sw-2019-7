package it.polimi.ingsw.views;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class GameFrame extends JNavigationFrame {
    private JPanel panel;
    private JLabel gameLabel;

    public GameFrame() {
        super(1000, 800, false);
        setContentPane(panel);
        try {
            InputStream inputStream = GameFrame.class.getResourceAsStream("board/board.png");
            var imageIcon = new ImageIcon(ImageIO.read(inputStream));
            var image = imageIcon.getImage().getScaledInstance(1000, 800, Image.SCALE_SMOOTH);
            gameLabel.setIcon(new ImageIcon(image));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
