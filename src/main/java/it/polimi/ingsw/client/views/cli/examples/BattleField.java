package it.polimi.ingsw.client.views.cli.examples;

import it.polimi.ingsw.client.views.cli.base.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleField {

    private static final int MAX_VERT_TILES = 4 * 5; //rows.
    private static final int MAX_HORIZ_TILES = 3 * 5; //cols.


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

            if (System.getProperty("os.name").contains("Windows"))
                Runtime.getRuntime().exec("cls");
            else Runtime.getRuntime().exec("clear");
        } catch (final Exception e) {
            //  Handle any exceptions.
        }
    }

    private void fillEmpy() {

        tiles[0][0] = "╔";
        for (int c = 1; c < MAX_HORIZ_TILES - 1; c++) {
            if (c % 5 == 0) {
                tiles[0][c] = "╦";
            } else {
                tiles[0][c] = "═";
            }
        }

        tiles[0][MAX_HORIZ_TILES - 1] = "╗";

        for (int r = 1; r < MAX_VERT_TILES - 1; r++) {
            if (r % 5 == 0) {
                tiles[r][0] = "╠";
            } else {
                tiles[r][0] = "║";
            }

            for (int c = 1; c < MAX_HORIZ_TILES - 1; c++) {
                if (c % 5 == 0) {
                    if (r % 5 == 0) {
                        tiles[r][c] = "╬";
                    } else {
                        tiles[r][c] = "║";
                    }
                } else {
                    if (r % 5 == 0) {
                        tiles[r][c] = "═";
                    }
                    tiles[r][c] = " ";
                }

            }
            tiles[r][MAX_HORIZ_TILES - 1] = "║";
        }

        tiles[MAX_VERT_TILES - 1][0] = "╚";
        for (int c = 1; c < MAX_HORIZ_TILES - 1; c++) {
            if (c % 5 == 0) {
                tiles[MAX_VERT_TILES - 1][c] = "╩";
            } else {
                tiles[MAX_VERT_TILES - 1][c] = "═";
            }
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
        clearConsole();
        System.out.print(Color.ANSI_YELLOW.escape());
        for (int r = 0; r < MAX_VERT_TILES; r++) {
            System.out.println();
            for (int c = 0; c < MAX_HORIZ_TILES; c++) {
                System.out.print(tiles[r][c]);
            }
        }
    }
}
