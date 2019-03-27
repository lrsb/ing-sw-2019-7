package it.polimi.ingsw;

import it.polimi.ingsw.views.MainFrame;
import it.polimi.ingsw.views.SettingsFrame;

public class Client {
    public static void main(String[] args) {
        var main = new MainFrame(new SettingsFrame());
        main.setVisible(true);
    }
}
