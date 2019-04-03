package it.polimi.ingsw.controllers;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.controllers.base.BaseViewController;
import it.polimi.ingsw.controllers.base.NavigationController;
import it.polimi.ingsw.models.server.HandyManny;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class GamesListViewController extends BaseViewController {
    private JPanel panel;
    private JTextField cercaTextField;
    private JButton helpButton;
    private JTable table1;

    public GamesListViewController(@NotNull NavigationController navigationController) throws RemoteException, NotBoundException {
        super(800, 600, navigationController);
        setContentPane(panel);
        Client.RMI_REGISTRY.lookup(HandyManny.RMI_NAME);
    }
}
