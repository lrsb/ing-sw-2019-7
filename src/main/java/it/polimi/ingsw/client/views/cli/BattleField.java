package it.polimi.ingsw.client.views.cli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleField {

    private static final int MAX_VERT_TILES = 5; //rows.
    private static final int MAX_HORIZ_TILES = 25; //cols.

    String[][] tiles = new String[MAX_VERT_TILES][MAX_HORIZ_TILES];

    List<String> weapons = new ArrayList<>();


    BattleField() {
        fillEmpy();
        loadWeapons();
        dissiminateWeapons();
    }

    public final static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (final Exception e) {
            //  Handle any exceptions.
        }
    }

    private void fillEmpy() {

        tiles[0][0] = "╔";
        for (int c = 1; c < MAX_HORIZ_TILES - 1; c++) {
            tiles[0][c] = "═";
        }

        tiles[0][MAX_HORIZ_TILES - 1] = "╗";

        for (int r = 1; r < MAX_VERT_TILES - 1; r++) {
            tiles[r][0] = "║";
            for (int c = 1; c < MAX_HORIZ_TILES - 1; c++) {
                tiles[r][c] = " ";
            }
            tiles[r][MAX_HORIZ_TILES - 1] = "║";
        }

        tiles[MAX_VERT_TILES - 1][0] = "╚";
        for (int c = 1; c < MAX_HORIZ_TILES - 1; c++) {
            tiles[MAX_VERT_TILES - 1][c] = "═";
        }

        tiles[MAX_VERT_TILES - 1][MAX_HORIZ_TILES - 1] = "╝";

    }

    private void loadWeapons() {

        String greenEscape = Color.ANSI_YELLOW.escape();

        weapons.add(Color.ANSI_RED.escape() + "@" + greenEscape);
        weapons.add(Color.ANSI_BLUE.escape() + "p" + greenEscape);
        weapons.add(Color.ANSI_YELLOW.escape() + "x" + greenEscape);
    }

    private void dissiminateWeapons() {

        for (; ; ) {
            int count = weapons.size();
            if (count <= 0)
                break;

            // get random index:
            Random rand = new Random();
            int index = rand.nextInt(count);

            // get random coords: (inside field)
            int r = rand.nextInt(MAX_VERT_TILES - 2) + 1;

            // get random index:
            int c = rand.nextInt(MAX_HORIZ_TILES - 2) + 1;

            tiles[r][c] = weapons.get(index);
            weapons.remove(index);


        }
    }

    final void plot() {
        //clearConsole();
        try {
            Runtime.getRuntime().exec("clear");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print(Color.ANSI_YELLOW.escape());
        for (int r = 0; r < MAX_VERT_TILES; r++) {
            System.out.println();
            for (int c = 0; c < MAX_HORIZ_TILES; c++) {
                System.out.print(tiles[r][c]);
            }
        }
    }
}
