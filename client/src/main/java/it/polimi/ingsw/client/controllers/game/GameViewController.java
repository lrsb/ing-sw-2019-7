package it.polimi.ingsw.client.controllers.game;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.controllers.base.BaseViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.others.Preferences;
import it.polimi.ingsw.client.others.Utils;
import it.polimi.ingsw.client.views.gui.boards.GameBoard;
import it.polimi.ingsw.client.views.gui.boards.GameBoardListener;
import it.polimi.ingsw.common.models.Action;
import it.polimi.ingsw.common.models.*;
import it.polimi.ingsw.common.network.exceptions.UserRemoteException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.Objects;

import static javax.swing.JOptionPane.*;

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
    private JButton moveButton;
    private JButton shootButton;
    private JButton grabButton;
    private JLabel actualPlayerLabel;
    private JLabel moveLabel;
    private JPanel actionPanel;
    private JButton cancelButton;
    private JLabel actionDescriptionLabel;
    private JPanel cancelPanel;
    private JButton spawnButton;
    private JButton skipButton;
    private JButton powerupButton;
    private JButton reloadButton;
    private JButton rulesButton;

    private @Nullable PlayersBoardsViewController playersBoardsViewController;

    private Game game;
    private boolean yourTurn;
    private @Nullable Action.Type type;

    public GameViewController(@NotNull NavigationController navigationController, @NotNull Object... params) throws IOException {
        super("Gioca", 1100, 700, navigationController);
        game = (Game) params[0];
        $$$setupUI$$$();
        setContentPane(panel);
        setResizable(true);
        setEnableFullscreen(true);

        updateBoards(game);

        panel.setBackground(BACKGROUND_COLOR);
        actionPanel.setBackground(BACKGROUND_COLOR);
        gameBoard.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBackground(BACKGROUND_COLOR);
        playersBoardButton.setBackground(BACKGROUND_COLOR);
        playersBoardButton.setForeground(BACKGROUND_COLOR);
        exitButton.setBackground(BACKGROUND_COLOR);
        exitButton.setForeground(BACKGROUND_COLOR);
        moveLabel.setForeground(WHITE_ACCENT);
        cancelPanel.setBackground(BACKGROUND_COLOR);
        actionDescriptionLabel.setForeground(WHITE_ACCENT);
        actualPlayerLabel.setForeground(WHITE_ACCENT);

        Preferences.getTokenOrJumpBack(getNavigationController()).ifPresent(e -> {
            try {
                Client.API.addGameListener(e, game.getUuid(), (f, message) -> {
                    if (message != null) JOptionPane.showMessageDialog(null, message);
                    try {
                        updateBoards(f);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if (f.isCompleted()) navigationController.popViewController();
                });
            } catch (UserRemoteException ex) {
                ex.printStackTrace();
                Utils.jumpBackToLogin(getNavigationController());
            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });

        playersBoardButton.addActionListener(e -> {
            try {
                Utils.swingOpenRules();
            } catch (URISyntaxException | IOException ex) {
                ex.printStackTrace();
            }
            if (playersBoardsViewController != null) playersBoardsViewController.dispose();
            playersBoardsViewController = new PlayersBoardsViewController(gameBoard.getGame());
            playersBoardsViewController.setVisible(true);
        });

        exitButton.addActionListener(e -> Preferences.getTokenOrJumpBack(getNavigationController()).ifPresent(f -> {
            if (JOptionPane.showConfirmDialog(null, "Vuoi uscire dal gioco?", "Esci", YES_NO_OPTION) == YES_OPTION) {
                try {
                    Client.API.quitGame(f, game.getUuid());
                    navigationController.popViewController();
                } catch (UserRemoteException ex) {
                    ex.printStackTrace();
                    Utils.jumpBackToLogin(getNavigationController());
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        }));

        moveButton.addActionListener(e -> {
            if (yourTurn) {
                type = Action.Type.MOVE;
                reloadActionPanel();
            }
        });
        shootButton.addActionListener(e -> {
            if (yourTurn) {
                type = Action.Type.FIRE;
                reloadActionPanel();
            }
        });
        grabButton.addActionListener(e -> {
            if (yourTurn) {
                type = game.getCell(game.getActualPlayer().getPosition()).isSpawnPoint() ? Action.Type.GRAB_WEAPON : Action.Type.GRAB_AMMOCARD;
                reloadActionPanel();
            }
        });
        spawnButton.addActionListener(e -> {
            try {
                if (JOptionPane.showConfirmDialog(null,
                        Utils.getStrings("cli", "actions").get("borning_action").getAsString(),
                        game.getActualPlayer().getPosition() == null ? "Nasci" : "Rinasci", OK_CANCEL_OPTION) == OK_OPTION) {
                    new PowerUpSelectorViewController(game.getActualPlayer().getPowerUps(), (PowerUpSelectorViewController.PowerCallback) powerUps -> {
                        if (powerUps.size() == 1)
                            doAction(Action.Builder.create(game.getUuid()).buildReborn(powerUps.get(0).getType(), powerUps.get(0).getAmmoColor()));
                        else if (powerUps.size() > 1) JOptionPane.showMessageDialog(null, "Scegli solo un powerup!");
                        else JOptionPane.showMessageDialog(null, "scegli un powerup!");
                    }).setVisible(true);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        cancelButton.addActionListener(e -> {
            type = null;
            reloadActionPanel();
        });
    }

    private void updateBoards(@NotNull Game game) throws IOException {
        this.game = game;
        if (gameBoard != null) gameBoard.setGame(game);
        if (playersBoardsViewController != null) {
            playersBoardsViewController.dispose();
            playersBoardsViewController = null;
        }
        yourTurn = Preferences.getUuid().equals(game.getActualPlayer().getUuid());
        reloadActionPanel();

        actualPlayerLabel.setText(yourTurn ? "TE" : game.getActualPlayer().getNickname());
    }

    private void reloadActionPanel() {
        if (yourTurn) {
            if (type != null) {
                switch (type) {
                    case MOVE:
                        actionDescriptionLabel.setText("Muovi il tuo giocatore");
                        break;
                    case GRAB_WEAPON:
                        actionDescriptionLabel.setText("Scegli l'arma da raccogliere");
                        break;
                    case GRAB_AMMOCARD:
                        actionDescriptionLabel.setText("Scegli la tessera delle munizioni da raccogliere");
                        break;
                    case FIRE:
                        break;
                    case USE_POWER_UP:
                        break;
                    case RELOAD:
                        break;
                    case NEXT_TURN:
                        break;
                    case REBORN:
                        break;
                }
                actionPanel.setVisible(false);
                cancelPanel.setVisible(true);
            } else {
                actionPanel.setVisible(true);
                moveButton.setVisible(false);
                shootButton.setVisible(false);
                grabButton.setVisible(false);
                spawnButton.setVisible(false);
                skipButton.setVisible(false);
                powerupButton.setVisible(false);
                reloadButton.setVisible(false);
                rulesButton.setVisible(true);

                if (game.getActualPlayer().getPosition() == null) { // non è mai spawnato
                    spawnButton.setText("Spawna");
                    spawnButton.setVisible(true);

                } else if (game.isAReborn()) {
                    spawnButton.setText("Respawna");
                    spawnButton.setVisible(true);

                } else if (game.isATagbackResponse()) {
                    powerupButton.setVisible(true);

                } else if (game.getRemainedActions() == 0) {
                    reloadButton.setVisible(true);
                    powerupButton.setVisible(true);

                } else if (!game.isCompleted()) {//azione standard
                    moveButton.setVisible(true);
                    shootButton.setVisible(true);
                    grabButton.setVisible(true);
                    powerupButton.setVisible(true);
                }

                grabButton.setVisible(game.getCell(game.getActualPlayer().getPosition()) != null);
                cancelPanel.setVisible(false);
            }
        } else {
            actionPanel.setVisible(false);
            cancelPanel.setVisible(false);
        }
    }

    @Override
    public void spriteSelected(@Nullable Object data, @Nullable Point point) {
        @Nullable Action todo = null;

        if (data instanceof Weapon) {
            if (type == Action.Type.GRAB_WEAPON) {
                //todo = Action.Builder.create(game.getUuid()).buildWeaponGrabAction()
            } else new ExpoViewController(null, data).setVisible(true);
        }
        if (data instanceof AmmoCard && type == Action.Type.GRAB_AMMOCARD) {
            todo = Action.Builder.create(game.getUuid()).buildAmmoCardGrabAction(point);
        }

        doAction(todo);
    }

    @Override
    public boolean spriteMoved(@Nullable Object data, @Nullable Point point) {
        if (data == null) return false;
        @Nullable Action todo = null;

        if (data instanceof Player && type == Action.Type.MOVE)
            if (Preferences.getUuid().equals(((Player) data).getUuid())) {
            if (point != null && game.canMove(game.getActualPlayer().getPosition(), point, 2))
                todo = Action.Builder.create(game.getUuid()).buildMoveAction(point);
            else JOptionPane.showMessageDialog(null, "Non ti puoi muovere lì");
        } else JOptionPane.showMessageDialog(null, "Muovi il tuo giocatore");

        return doAction(todo);
    }

    private boolean doAction(@Nullable Action action) {
        var token = Preferences.getTokenOrJumpBack(getNavigationController());
        if (action != null && token.isPresent()) try {
            return Client.API.doAction(token.get(), action);
        } catch (UserRemoteException ex) {
            ex.printStackTrace();
            Utils.jumpBackToLogin(getNavigationController());
        } catch (RemoteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return false;
    }

    @Override
    protected void controllerPopped() {
        Preferences.getTokenOrJumpBack(getNavigationController()).ifPresent(e -> {
            try {
                Client.API.removeGameListener(e, game.getUuid());
            } catch (UserRemoteException ex) {
                ex.printStackTrace();
                Utils.jumpBackToLogin(getNavigationController());
            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });
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
        panel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(gameBoard, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayoutManager(9, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(buttonPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        actualPlayerLabel = new JLabel();
        Font actualPlayerLabelFont = this.$$$getFont$$$(null, -1, 28, actualPlayerLabel.getFont());
        if (actualPlayerLabelFont != null) actualPlayerLabel.setFont(actualPlayerLabelFont);
        actualPlayerLabel.setText("Label");
        buttonPanel.add(actualPlayerLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        playersBoardButton = new JButton();
        playersBoardButton.setText("Visualizza plance");
        buttonPanel.add(playersBoardButton, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exitButton = new JButton();
        exitButton.setText("Esci");
        buttonPanel.add(exitButton, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        buttonPanel.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        moveLabel = new JLabel();
        Font moveLabelFont = this.$$$getFont$$$(null, -1, 24, moveLabel.getFont());
        if (moveLabelFont != null) moveLabel.setFont(moveLabelFont);
        moveLabel.setText("Tocca a:");
        buttonPanel.add(moveLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        actionPanel = new JPanel();
        actionPanel.setLayout(new GridLayoutManager(8, 1, new Insets(0, 0, 0, 0), -1, -1));
        buttonPanel.add(actionPanel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        moveButton = new JButton();
        moveButton.setText("Muovi");
        actionPanel.add(moveButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        shootButton = new JButton();
        shootButton.setText("Spara");
        actionPanel.add(shootButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        grabButton = new JButton();
        grabButton.setText("Raccogli");
        actionPanel.add(grabButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spawnButton = new JButton();
        spawnButton.setText("Nasci");
        actionPanel.add(spawnButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        skipButton = new JButton();
        skipButton.setText("Salta");
        actionPanel.add(skipButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        powerupButton = new JButton();
        powerupButton.setText("Usa powerup");
        actionPanel.add(powerupButton, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        reloadButton = new JButton();
        reloadButton.setText("Ricarica");
        actionPanel.add(reloadButton, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cancelPanel = new JPanel();
        cancelPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        buttonPanel.add(cancelPanel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setText("Annulla mossa");
        cancelPanel.add(cancelButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        actionDescriptionLabel = new JLabel();
        Font actionDescriptionLabelFont = this.$$$getFont$$$(null, -1, 20, actionDescriptionLabel.getFont());
        if (actionDescriptionLabelFont != null) actionDescriptionLabel.setFont(actionDescriptionLabelFont);
        actionDescriptionLabel.setText("Label");
        cancelPanel.add(actionDescriptionLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rulesButton = new JButton();
        rulesButton.setText("Regole");
        buttonPanel.add(rulesButton, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}