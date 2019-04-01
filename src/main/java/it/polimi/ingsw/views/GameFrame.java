package it.polimi.ingsw.views;

import it.polimi.ingsw.graphics.sprite.SpriteBoard;
import it.polimi.ingsw.views.base.JNavigationFrame;

import javax.swing.*;

public class GameFrame extends JNavigationFrame {
    private JPanel panel;
    private SpriteBoard spriteBoard;

    public GameFrame() {
        super(1300, 800, false);
        setContentPane(panel);
    }

    public SpriteBoard getSpriteBoard() {
        return spriteBoard;
    }

    private void createUIComponents() {
        spriteBoard = new SpriteBoard();
    }
}
