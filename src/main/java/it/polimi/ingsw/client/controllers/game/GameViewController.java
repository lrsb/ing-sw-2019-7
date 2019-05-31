package it.polimi.ingsw.client.controllers.game;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import it.polimi.ingsw.client.controllers.base.BaseViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.views.gui.boards.GameBoard;
import it.polimi.ingsw.client.views.gui.boards.GameBoardListener;
import it.polimi.ingsw.common.models.Action;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.models.User;
import it.polimi.ingsw.server.models.GameImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameViewController extends BaseViewController implements GameBoardListener {
    private JPanel panel;
    private GameBoard gameBoard;
    private JButton playersBoardButton;
    private JButton exitButton;

    private @Nullable NavigationController lastViewController;

    public GameViewController(@NotNull NavigationController navigationController, @NotNull Object... params) {
        super("Gioca", 1200, 900, navigationController);
        $$$setupUI$$$();
        setResizable(true);
        setEnableFullscreen(true);
        //var gameUuid = (UUID) params[0];
        setContentPane(panel);
        playersBoardButton.addActionListener(e -> {
            if (lastViewController != null) lastViewController.close();
            lastViewController = new NavigationController(PlayersBoardsViewController.class, gameBoard.getGame());
        });
        exitButton.addActionListener(e -> {
            var room = new Room("ciao", new User("user1"));
            Collections.nCopies(4, null).parallelStream().map(f -> new User(UUID.randomUUID().toString().substring(0, 7))).collect(Collectors.toList()).forEach(room::addUser);
            var game = GameImpl.Creator.newGame(room);
            game.getPlayers().forEach(f -> {
                f.addDamage(game.getPlayers().get(new SecureRandom().nextInt(game.getPlayers().size())));
                f.addDamage(game.getPlayers().get(new SecureRandom().nextInt(game.getPlayers().size())));
                f.addDamage(game.getPlayers().get(new SecureRandom().nextInt(game.getPlayers().size())));
                f.addDamage(game.getPlayers().get(new SecureRandom().nextInt(game.getPlayers().size())));
                f.addDamage(game.getPlayers().get(new SecureRandom().nextInt(game.getPlayers().size())));
                f.addDamage(game.getPlayers().get(new SecureRandom().nextInt(game.getPlayers().size())));
                f.addDamage(game.getPlayers().get(new SecureRandom().nextInt(game.getPlayers().size())));
                f.addDamage(game.getPlayers().get(new SecureRandom().nextInt(game.getPlayers().size())));
                f.addDamage(game.getPlayers().get(new SecureRandom().nextInt(game.getPlayers().size())));
            });
            try {
                gameBoard.setGame(game);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        /*exitButton.addActionListener(e -> Preferences.getTokenOrJumpBack(getNavigationController()).ifPresent(f -> {
            try {
                Client.API.removeGameListener(f, gameUuid);
            } catch (UserRemoteException ex) {
                ex.printStackTrace();
                Utils.jumpBackToLogin(getNavigationController());
            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
            navigationController.popViewController();
        }));
        Preferences.getTokenOrJumpBack(getNavigationController()).ifPresent(e -> {
            try {
                Client.API.addGameListener(e, gameUuid, f -> {
                    gameBoard.updateGame(f);
                    if (lastViewController != null && lastViewController.getViewController(0) != null)
                        if (lastViewController.getViewController(0) instanceof PlayerBoardViewController)
                            ((PlayerBoardViewController) lastViewController.getViewController(0));
                        else if (lastViewController.getViewController(0) instanceof PlayersBoardsViewController)
                            ((PlayersBoardsViewController) lastViewController.getViewController(0));
                });
            } catch (UserRemoteException ex) {
                ex.printStackTrace();
                Utils.jumpBackToLogin(getNavigationController());
            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });*/
    }

    @Override
    public void doAction(@NotNull Action action) {
        /*Preferences.getTokenOrJumpBack(getNavigationController()).ifPresent(e -> {
            try {
                Client.API.doAction(e, action);
            } catch (UserRemoteException ex) {
                ex.printStackTrace();
                Utils.jumpBackToLogin(getNavigationController());
            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });*/
    }

    private void createUIComponents() {
        var room = new Room("ciao", new User("user1"));
        room.setGameType(Game.Type.SIX_SIX);
        Collections.nCopies(4, null).parallelStream().map(f -> new User(UUID.randomUUID().toString().substring(0, 7))).collect(Collectors.toList()).forEach(room::addUser);
        var game = GameImpl.Creator.newGame(room);
        game.getPlayers().forEach(e -> {
            e.addDamage(game.getPlayers().get(new SecureRandom().nextInt(game.getPlayers().size())));
            e.addDamage(game.getPlayers().get(new SecureRandom().nextInt(game.getPlayers().size())));
            e.addDamage(game.getPlayers().get(new SecureRandom().nextInt(game.getPlayers().size())));
            e.addDamage(game.getPlayers().get(new SecureRandom().nextInt(game.getPlayers().size())));
            e.addDamage(game.getPlayers().get(new SecureRandom().nextInt(game.getPlayers().size())));
            e.addDamage(game.getPlayers().get(new SecureRandom().nextInt(game.getPlayers().size())));
            e.addDamage(game.getPlayers().get(new SecureRandom().nextInt(game.getPlayers().size())));
            e.addDamage(game.getPlayers().get(new SecureRandom().nextInt(game.getPlayers().size())));
            e.addDamage(game.getPlayers().get(new SecureRandom().nextInt(game.getPlayers().size())));
        });
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
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        playersBoardButton = new JButton();
        playersBoardButton.setText("Visualizza plance");
        panel1.add(playersBoardButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exitButton = new JButton();
        exitButton.setText("Esci");
        panel1.add(exitButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}