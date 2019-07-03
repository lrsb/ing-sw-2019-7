package it.polimi.ingsw.client.controllers.pregame;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.controllers.base.BaseViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.controllers.game.GameViewController;
import it.polimi.ingsw.client.others.Preferences;
import it.polimi.ingsw.client.views.gui.sprite.Sprite;
import it.polimi.ingsw.client.views.gui.sprite.SpriteBoard;
import it.polimi.ingsw.common.others.Utils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.rmi.RemoteException;

import static it.polimi.ingsw.client.others.Utils.jumpBackToLogin;

public class MainViewController extends BaseViewController {
    private JPanel panel;
    private SpriteBoard logo;
    private JButton elencoPartiteButton;
    private JButton nuovaPartitaButton;
    private JButton esciButton;

    public MainViewController(@NotNull NavigationController navigationController) throws IOException {
        super("Benvenuto", 400, 300, navigationController);
        $$$setupUI$$$();
        setContentPane(panel);
        logo.addSprite(new Sprite(10, 40, 370, 70, Utils.readPngImage(getClass(), "logo")));

        nuovaPartitaButton.addActionListener(e -> {
            if (getNavigationController() != null)
                getNavigationController().presentViewController(NewRoomViewController.class);
        });
        elencoPartiteButton.addActionListener(e -> {
            if (getNavigationController() != null)
                getNavigationController().presentViewController(RoomsListViewController.class);
        });
        esciButton.addActionListener(e -> jumpBackToLogin(getNavigationController()));
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) Preferences.getTokenOrJumpBack(getNavigationController()).ifPresent(e -> {
            try {
                var game = Client.API.getActiveGame(e);
                if (getNavigationController() != null)
                    getNavigationController().presentViewController(GameViewController.class, game);
            } catch (RemoteException ignored) {
            }
        });
    }

    private void createUIComponents() {
        logo = new SpriteBoard(null);
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
        panel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1, false, true));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        Font logoFont = this.$$$getFont$$$("Arial Unicode MS", Font.PLAIN, 36, logo.getFont());
        if (logoFont != null) logo.setFont(logoFont);
        panel1.add(logo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(4, 5, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        elencoPartiteButton = new JButton();
        elencoPartiteButton.setText("Elenco partite");
        panel2.add(elencoPartiteButton, new GridConstraints(2, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nuovaPartitaButton = new JButton();
        nuovaPartitaButton.setText("Nuova partita");
        panel2.add(nuovaPartitaButton, new GridConstraints(1, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel2.add(spacer2, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel2.add(spacer3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel2.add(spacer4, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel2.add(spacer5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        esciButton = new JButton();
        esciButton.setText("Esci");
        panel2.add(esciButton, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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