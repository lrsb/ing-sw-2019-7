package it.polimi.ingsw.client.controllers.game;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import it.polimi.ingsw.client.controllers.base.BaseViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.others.Preferences;
import it.polimi.ingsw.client.views.gui.boards.PlayerBoard;
import it.polimi.ingsw.common.models.Game;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class PlayersBoardsViewController extends BaseViewController {
    private JPanel panel;
    private JTabbedPane tabbedPane;

    public PlayersBoardsViewController(@Nullable NavigationController navigationController, @NotNull Object... params) {
        super("Plance", 850, 270, navigationController);
        setContentPane(panel);
        //noinspection ComparatorMethodParameterNotUsed
        ((Game) params[0]).getPlayers().parallelStream().sorted((e, f) -> e.getUuid().equals(Preferences.getUuid()) ? 1 : 0).forEachOrdered(e -> {
            try {
                tabbedPane.addTab(e.getUuid().equals(Preferences.getUuid()) ? "La tua plancia" : e.getNickname(), new PlayerBoard(((Game) params[0]), e));
            } catch (IOException ex) {
                ex.printStackTrace();
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
        panel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane = new JTabbedPane();
        panel.add(tabbedPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
