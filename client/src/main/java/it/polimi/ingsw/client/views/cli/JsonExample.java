package it.polimi.ingsw.client.views.cli;

import it.polimi.ingsw.client.others.Utils;

import java.io.FileNotFoundException;

public class JsonExample {
    public static void main(String[] args) throws FileNotFoundException {
        var json = Utils.getStrings("cli", "actions", "move_action").get("select_square").getAsString();
    }
}
