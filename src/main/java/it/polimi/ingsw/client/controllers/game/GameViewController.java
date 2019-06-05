package it.polimi.ingsw.client.controllers.game;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.client.controllers.base.BaseViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.others.Preferences;
import it.polimi.ingsw.client.others.Utils;
import it.polimi.ingsw.client.views.gui.boards.GameBoard;
import it.polimi.ingsw.client.views.gui.boards.GameBoardListener;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Weapon;
import it.polimi.ingsw.common.network.exceptions.UserRemoteException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Objects;
import java.util.UUID;

public class GameViewController extends BaseViewController implements GameBoardListener {
    private static final @NotNull Color BACKGROUND_COLOR = Objects.requireNonNull(Utils.hexToColor("1F1E1A"));
    private static final @NotNull Color PURPLE_ACCENT = Objects.requireNonNull(Utils.hexToColor("98155E"));
    private static final @NotNull Color WHITE_ACCENT = Objects.requireNonNull(Utils.hexToColor("F9F7FB"));
    private static final @NotNull Color YELLOW_ACCENT = Objects.requireNonNull(Utils.hexToColor("F1E380"));
    private static final @NotNull Color RED_ACCENT = Objects.requireNonNull(Utils.hexToColor("BD151A"));

    private JPanel panel;
    private GameBoard gameBoard;
    private JButton playersBoardButton;
    private JButton exitButton;
    private JPanel buttonPanel;
    private JLabel actualPlayerLabel;

    private Game game;

    private @Nullable PlayersBoardsViewController playersBoardsViewController;

    public GameViewController(@NotNull NavigationController navigationController, @NotNull Object... params) throws IOException {
        super("Gioca", 1200, 900, navigationController);
        game = (Game) params[0];
        $$$setupUI$$$();
        setContentPane(panel);
        setResizable(true);
        setEnableFullscreen(true);

        updateBoards(game);

        panel.setBackground(BACKGROUND_COLOR);
        gameBoard.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBackground(BACKGROUND_COLOR);
        playersBoardButton.setBackground(BACKGROUND_COLOR);
        playersBoardButton.setForeground(BACKGROUND_COLOR);
        exitButton.setBackground(BACKGROUND_COLOR);
        exitButton.setForeground(BACKGROUND_COLOR);

        playersBoardButton.addActionListener(e -> {
            if (playersBoardsViewController != null) playersBoardsViewController.dispose();
            playersBoardsViewController = new PlayersBoardsViewController(null, gameBoard.getGame());
            playersBoardsViewController.setVisible(true);
        });
        exitButton.addActionListener(e -> Preferences.getTokenOrJumpBack(getNavigationController()).ifPresent(f -> {
            try {
                Client.API.removeGameListener(f, game.getUuid());
                Client.API.quitGame(f, game.getUuid());
                navigationController.popViewController();
            } catch (UserRemoteException ex) {
                ex.printStackTrace();
                Utils.jumpBackToLogin(getNavigationController());
            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }));
        Preferences.getTokenOrJumpBack(getNavigationController()).ifPresent(e -> {
            try {
                Client.API.addGameListener(e, game.getUuid(), (f, message) -> {
                    if (message != null) JOptionPane.showMessageDialog(null, message);
                    try {
                        updateBoards(f);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            } catch (UserRemoteException ex) {
                ex.printStackTrace();
                Utils.jumpBackToLogin(getNavigationController());
            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });
    }

    private void updateBoards(@NotNull Game game) throws IOException {
        this.game = game;
        if (gameBoard != null) gameBoard.setGame(game);
        //actualPlayerLabel.setText("E il turno di: " + game.getActualPlayer().getNickname());
    }

    @Override
    public void spriteSelected(@Nullable Object data) {
        if (data instanceof Weapon.Name) new WeaponExpoViewController(null, data).setVisible(true);
    }

    @Override
    public boolean spriteMoved(@Nullable Object data, int x, int y) {
        if (data == null) return false;
        if (data instanceof UUID) {
            //TODO: movimento giocatore;
        }
        return false;
        /*
        Preferences.getTokenOrJumpBack(getNavigationController()).ifPresent(e -> {
            try {
                Client.API.doAction(e, action);
            } catch (UserRemoteException ex) {
                ex.printStackTrace();
                Utils.jumpBackToLogin(getNavigationController());
            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });
         */
    }

    private void createUIComponents() {
        try {
            gameBoard = new GameBoard(game);
            gameBoard.setBoardListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(gameBoard, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(buttonPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 40), new Dimension(-1, 40), new Dimension(-1, 40), 0, false));
        playersBoardButton = new JButton();
        playersBoardButton.setText("Visualizza plance");
        buttonPanel.add(playersBoardButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exitButton = new JButton();
        exitButton.setText("Esci");
        buttonPanel.add(exitButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Label");
        buttonPanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}