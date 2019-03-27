package it.polimi.ingsw;

import it.polimi.ingsw.views.Main;
import it.polimi.ingsw.views.Settings;

public class Client {
    public static void main(String[] args) {
        var main = new Main(new Settings());
        main.setVisible(true);
    }
}
