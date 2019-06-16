package it.polimi.ingsw.common.models;

import it.polimi.ingsw.common.models.exceptions.PlayerNotFoundException;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.UUID;

import static it.polimi.ingsw.common.models.Game.MAX_X;
import static it.polimi.ingsw.common.models.Game.MAX_Y;
import static org.junit.jupiter.api.Assertions.*;

class PowerUpTest {

    @RepeatedTest(value = 100)
    void testTargetingScope() {
        Game game = createGame();
        PowerUp ts = new PowerUp(AmmoCard.Color.values()[new SecureRandom().nextInt(AmmoCard.Color.values().length)],
                PowerUp.Type.TARGETING_SCOPE);
        game.getPlayers().get(0).addPowerUp(ts);
        assertThrows(PlayerNotFoundException.class, () -> ts.use(game));
        ts.setTarget(game.getPlayers().get(1));
        assertFalse(ts.use(game));
        game.addToLastsDamaged(game.getPlayers().get(1));
        assertTrue(ts.use(game));
        assertEquals(1, game.getPlayers().get(1).getDamagesTaken().size());
        assertTrue(game.getPlayers().get(1).getDamagesTaken().contains(game.getPlayers().get(0).getUuid()));
        for (int i = 0; i < 100; i++) assertTrue(ts.use(game));
        assertEquals(12, game.getPlayers().get(1).getDamagesTaken().size());
    }

    @Test
    ArrayList<Player> createPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        ArrayList<String> nickNames = new ArrayList<>();
        int nPlayers = new SecureRandom().nextInt(3) + 3;
        nickNames.add("Fede");
        nickNames.add("Lore");
        nickNames.add("Tia");
        nickNames.add("SuperMario");
        nickNames.add("Snake");
        nickNames.add("Liquid");
        nickNames.add("Ocelot");
        int board = 0;
        while (players.size() < nPlayers) {
            int pos = new SecureRandom().nextInt(nickNames.size());
            players.add(new Player(new User(nickNames.get(pos)), Player.BoardType.values()[board]));
            nickNames.remove(pos);
            board++;
        }
        assertTrue(players.size() > 2 && players.size() < 6);
        for (int i = 0; i < players.size() - 1; i++)
            for (int j = i + 1; j < players.size(); j++) {
                assertNotEquals(players.get(i), players.get(j));
            }
        return players;
    }

    @Test
    Game createGame() {
        Game.Type type = Game.Type.values()[new SecureRandom().nextInt(Game.Type.values().length)];
        var cells = new Cell[MAX_X][MAX_Y];
        switch (type.getLeft()) {
            case "L5":
                cells[0][0] = Cell.Creator.withBounds("_ |_").color(Cell.Color.BLUE).create();
                cells[0][1] = Cell.Creator.withBounds("| __").color(Cell.Color.RED).spawnPoint().create();
                cells[0][2] = null;
                cells[1][0] = Cell.Creator.withBounds("_ _ ").color(Cell.Color.BLUE).create();
                switch (type.getRight()) {
                    case "R5":
                        cells[1][1] = Cell.Creator.withBounds("_ _ ").color(Cell.Color.RED).create();
                        cells[1][2] = Cell.Creator.withBounds("| __").color(Cell.Color.WHITE).create();
                        break;
                    case "R6":
                        cells[1][1] = Cell.Creator.withBounds("___ ").color(Cell.Color.RED).create();
                        break;
                }
                break;
            case "L6":
                cells[0][0] = Cell.Creator.withBounds("_| _").color(Cell.Color.BLUE).create();
                cells[0][1] = Cell.Creator.withBounds(" _|_").color(Cell.Color.RED).spawnPoint().create();
                cells[0][2] = Cell.Creator.withBounds("| __").color(Cell.Color.WHITE).create();
                cells[1][0] = Cell.Creator.withBounds("_ ||").color(Cell.Color.BLUE).create();
                switch (type.getRight()) {
                    case "R5":
                        cells[1][1] = Cell.Creator.withBounds("| |_").color(Cell.Color.PURPLE).create();
                        cells[1][2] = Cell.Creator.withBounds("| _ ").color(Cell.Color.WHITE).create();
                        break;
                    case "R6":
                        cells[1][1] = Cell.Creator.withBounds("|_|_").color(Cell.Color.PURPLE).create();
                        break;
                }
                break;
        }
        switch (type.getRight()) {
            case "R5":
                cells[2][0] = Cell.Creator.withBounds("__| ").color(Cell.Color.BLUE).spawnPoint().create();
                cells[2][1] = Cell.Creator.withBounds("||_ ").color(Cell.Color.PURPLE).create();
                cells[2][2] = Cell.Creator.withBounds("_|_ ").color(Cell.Color.WHITE).create();
                cells[3][0] = null;
                cells[3][1] = Cell.Creator.withBounds("__ |").color(Cell.Color.YELLOW).create();
                cells[3][2] = Cell.Creator.withBounds(" __|").color(Cell.Color.YELLOW).spawnPoint().create();
                break;
            case "R6":
                cells[1][2] = Cell.Creator.withBounds("||__").color(Cell.Color.WHITE).create();
                cells[2][0] = Cell.Creator.withBounds("_|| ").color(Cell.Color.BLUE).spawnPoint().create();
                cells[2][1] = Cell.Creator.withBounds("|  _").color(Cell.Color.YELLOW).create();
                cells[2][2] = Cell.Creator.withBounds("  _|").color(Cell.Color.YELLOW).create();
                cells[3][0] = Cell.Creator.withBounds("__||").color(Cell.Color.GREEN).create();
                cells[3][1] = Cell.Creator.withBounds("|_  ").color(Cell.Color.YELLOW).create();
                cells[3][2] = Cell.Creator.withBounds(" __ ").color(Cell.Color.YELLOW).spawnPoint().create();
                break;
        }
        return new Game(UUID.randomUUID(), type, cells, createPlayers(), new SecureRandom().nextInt(4) + 5);
    }
}
