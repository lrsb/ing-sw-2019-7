package it.polimi.ingsw.client.views.cli.examples;

import it.polimi.ingsw.client.views.cli.base.Color;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        Color c = Color.ANSI_BLUE;
        Dice d = new Dice(c);
        d.roll();
        d.dump();


        BattleField battleField = new BattleField();
        battleField.plot();
        battleField.plot();

    }
}
