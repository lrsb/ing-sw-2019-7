package it.polimi.ingsw.client.controllers.pregame;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.controllers.base.BaseViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.controllers.game.ChatViewController;
import it.polimi.ingsw.client.controllers.game.GameViewController;
import it.polimi.ingsw.client.others.Preferences;
import it.polimi.ingsw.common.models.Message;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.models.User;
import it.polimi.ingsw.common.network.exceptions.UserRemoteException;
import it.polimi.ingsw.common.others.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.others.Utils.jumpBackToLogin;

public class RoomViewController extends BaseViewController {
    private JPanel panel;
    private JList<String> usersList;
    private JLabel roomNameLabel;
    private JLabel skullsLabel;
    private JLabel gameTypeLabel;
    private JLabel startLabel;
    private JLabel timeoutLabel;
    private JButton startButton;
    private JButton exitButton;
    private JButton chatButton;

    private @NotNull UUID roomUuid;
    private @Nullable Clip clip;
    private Timer timer;
    private boolean game = true;

    private @Nullable ChatViewController chatViewController;

    {
        try {
            clip = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public RoomViewController(@NotNull NavigationController navigationController, @NotNull Object... params) {
        super("", 600, 400, navigationController);
        setContentPane(panel);
        startLabel.setText("");
        var room = (Room) params[0];
        roomUuid = room.getUuid();

        connect();

        chatButton.addActionListener(e -> {
            chatButton.setText("Chat");
            if (chatViewController != null) chatViewController.dispose();
            chatViewController = new ChatViewController(null, room.getUuid());
            chatViewController.setVisible(true);
        });

        startButton.addActionListener(e -> Preferences.getTokenOrJumpBack(getNavigationController()).ifPresent(f -> {
            try {
                Client.API.startGame(f, roomUuid);
            } catch (UserRemoteException ex) {
                ex.printStackTrace();
                jumpBackToLogin(getNavigationController());
            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }));

        exitButton.addActionListener(e -> Preferences.getTokenOrJumpBack(getNavigationController()).ifPresent(f -> {
            if (JOptionPane.showConfirmDialog(null, "Vuoi uscire dalla stanza?", "Esci", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION && getNavigationController() != null)
                getNavigationController().popViewController();
        }));

        if (clip != null) try {
            var audioInputStream = AudioSystem.getAudioInputStream(Utils.getUrl(getClass(), "follettina", "wav"));
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        update(room);
    }

    private void connect() {
        Preferences.getTokenOrJumpBack(getNavigationController()).ifPresent(e -> {
            try {
                Client.API.addListener(e, f -> {
                    if (f instanceof Room && ((Room) f).getUuid().equals(roomUuid)) {
                        var updatedRoom = (Room) f;
                        update(updatedRoom);
                        if (updatedRoom.isGameCreated() && getNavigationController() != null) {
                            game = false;
                            getNavigationController().presentViewController(true, GameViewController.class, Client.API.getActiveGame(e));
                        }
                    } else if (f instanceof Message) {
                        if (!((Message) f).getFrom().getUuid().equals(Preferences.getUuid()))
                            chatButton.setText("Chat (*)");
                        ChatViewController.messages.add((Message) f);
                    }
                });
            } catch (UserRemoteException ex) {
                ex.printStackTrace();
                jumpBackToLogin(getNavigationController());
            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });
    }

    private void update(@NotNull Room room) {
        if (timer != null) timer.stop();
        timer = new Timer(1000, e -> {
            if (room.getStartTime() - System.currentTimeMillis() <= 0) {
                startLabel.setText("");
            } else
                startLabel.setText("Partenza tra: " + (room.getStartTime() - System.currentTimeMillis()) / 1000 + " sec");
        });
        timer.start();
        roomNameLabel.setText(room.getName());
        skullsLabel.setText("Teschi: " + room.getSkulls());
        gameTypeLabel.setText("Tipo di gioco: " + room.getGameType());
        timeoutLabel.setText("Timeout: " + room.getActionTimeout());
        var listModel = new DefaultListModel<String>();
        listModel.addAll(room.getUsers().parallelStream().map(User::getNickname).collect(Collectors.toList()));
        usersList.setModel(listModel);
    }

    @Override
    public void dispose() {
        super.dispose();
        ChatViewController.messages.clear();
        Optional.ofNullable(chatViewController).ifPresent(ChatViewController::dispose);
        if (clip != null) clip.stop();
        Preferences.getTokenOrJumpBack(getNavigationController()).ifPresent(e -> {
            try {
                //Client.API.removeListener(e);
                if (game) Client.API.quitRoom(e, roomUuid);
            } catch (UserRemoteException ex) {
                ex.printStackTrace();
                jumpBackToLogin(getNavigationController());
            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        roomNameLabel = new JLabel();
        Font roomNameLabelFont = this.$$$getFont$$$(null, -1, 26, roomNameLabel.getFont());
        if (roomNameLabelFont != null) roomNameLabel.setFont(roomNameLabelFont);
        roomNameLabel.setText("Label");
        panel1.add(roomNameLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        skullsLabel = new JLabel();
        skullsLabel.setText("Label");
        panel2.add(skullsLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        gameTypeLabel = new JLabel();
        gameTypeLabel.setText("Label");
        panel2.add(gameTypeLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        timeoutLabel = new JLabel();
        timeoutLabel.setText("Label");
        panel2.add(timeoutLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        usersList = new JList();
        panel.add(usersList, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        startLabel = new JLabel();
        Font startLabelFont = this.$$$getFont$$$(null, -1, 20, startLabel.getFont());
        if (startLabelFont != null) startLabel.setFont(startLabelFont);
        startLabel.setHorizontalAlignment(0);
        startLabel.setText("Label");
        panel3.add(startLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        startButton = new JButton();
        startButton.setText("Inizia");
        panel3.add(startButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exitButton = new JButton();
        exitButton.setText("Esci");
        panel3.add(exitButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chatButton = new JButton();
        chatButton.setText("Chat");
        panel3.add(chatButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
