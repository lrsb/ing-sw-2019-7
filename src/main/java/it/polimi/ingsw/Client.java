package it.polimi.ingsw;

import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.controllers.game.GameViewController;
import it.polimi.ingsw.client.controllers.startup.LoginViewController;
import it.polimi.ingsw.client.network.ClientRestImpl;
import it.polimi.ingsw.client.views.cli.StartupCli;
import it.polimi.ingsw.client.views.cli.base.CliMenuManager;
import it.polimi.ingsw.common.network.API;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

public class Client {
    public static API API;

    public static void main(String[] args) throws RemoteException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (args != null) for (var arg : args)
            switch (arg) {
                case "-h":
                    Client.API = new ClientRestImpl("ing-sw-2019-7.herokuapp.com");
                    new NavigationController(LoginViewController.class);
                    return;
                case "-c":
                    CliMenuManager.startCli(StartupCli.class, false); // TODO da sistemare a true
                    return;
            }
        //Client.API = new ClientRestImpl("ing-sw-2019-7.herokuapp.com");
        new NavigationController(GameViewController.class, new Object());
    }
}