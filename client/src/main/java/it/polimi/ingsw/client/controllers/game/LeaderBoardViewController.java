package it.polimi.ingsw.client.controllers.game;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import it.polimi.ingsw.client.controllers.base.BaseViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Optional;
import java.util.stream.Collectors;

public class LeaderBoardViewController extends BaseViewController {
    private JPanel panel;
    private JTable leaderboardTable;
    private JButton okButton;

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    public LeaderBoardViewController(@Nullable NavigationController navigationController, @NotNull Object... args) {
        super("Leaderboard", 800, 500, navigationController);
        setContentPane(panel);
        var game = (Game) args[0];
        okButton.addActionListener(e -> Optional.ofNullable(getNavigationController()).ifPresent(NavigationController::popViewController));
        var tableModel = new DefaultTableModel() {
            @Contract(pure = true)
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.addColumn("Posizione");
        tableModel.addColumn("Player");
        var position = 1;
        if (game.getFinalRanking() != null) game.getFinalRanking().stream().map(e -> new Object[]{
                Integer.toString(position),
                e.stream().map(f -> game.getPlayers().parallelStream().filter(g -> g.getUuid().equals(f)).findAny().map(Player::getNickname).get())
                        .collect(Collectors.joining(", "))}).forEachOrdered(tableModel::addRow);
        leaderboardTable.setModel(tableModel);
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
        panel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        leaderboardTable = new JTable();
        scrollPane1.setViewportView(leaderboardTable);
        okButton = new JButton();
        okButton.setText("OK");
        panel.add(okButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
