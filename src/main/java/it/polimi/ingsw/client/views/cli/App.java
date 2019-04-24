package it.polimi.ingsw.client.views.cli;

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
