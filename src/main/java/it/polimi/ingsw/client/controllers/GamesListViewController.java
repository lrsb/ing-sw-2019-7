package it.polimi.ingsw.client.controllers;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.client.controllers.base.BaseViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.others.Preferences;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GamesListViewController extends BaseViewController {
    private JPanel panel;
    private JTextField cercaTextField;
    private JButton helpButton;
    private JTable table;
    private JButton joinButton;

    public GamesListViewController(@NotNull NavigationController navigationController) {
        super("Elenco partite", 800, 600, navigationController);
        setContentPane(panel);
        try {
            update();
        } catch (Exception e) {
            e.printStackTrace();
        }
        table.getSelectionModel().addListSelectionListener(event -> {
            var index = table.getSelectedRow();
        });
    }

    private void update() throws Exception {
        var roomList = Client.API.getRooms(Preferences.getToken());
        var tableModel = new DefaultTableModel();
        tableModel.addColumn(new Object[]{"Nome", "N. giocatori", " modalità"});
        roomList.stream().map(e -> new Object[]{e.getName(), e.getUsers().size() + "/5"}).forEach(tableModel::addRow);
        table.setModel(tableModel);
    }
}