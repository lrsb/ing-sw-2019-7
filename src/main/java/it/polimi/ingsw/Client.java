package it.polimi.ingsw;

import it.polimi.ingsw.controllers.MainViewController;
import it.polimi.ingsw.library.NavigationController;

public class Client {
    public static void main(String[] args) {
        var navigationController = new NavigationController(new MainViewController());
    }
}
