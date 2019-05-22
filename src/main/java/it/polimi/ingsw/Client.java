package it.polimi.ingsw;

import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.controllers.startup.ConnTypeViewController;
import it.polimi.ingsw.client.controllers.startup.LoginViewController;
import it.polimi.ingsw.client.network.ClientRestImpl;
import it.polimi.ingsw.common.network.API;

public class Client {
    public static API API;

    public static void main(String[] args) {
        if (args != null) for (var arg : args)
            if (arg.equals("-h")) {
                Client.API = new ClientRestImpl("ing-sw-2019-7.herokuapp.com");
                new NavigationController(LoginViewController.class);
                return;
            }
        new NavigationController(ConnTypeViewController.class);
    }
}