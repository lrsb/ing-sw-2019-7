package it.polimi.ingsw.common.models;

import it.polimi.ingsw.common.models.exceptions.SelfResponseException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

import static it.polimi.ingsw.common.models.Game.MAX_X;
import static it.polimi.ingsw.common.models.Game.MAX_Y;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @RepeatedTest(value = 100)
    void testGame() {
        Game.Type type = Game.Type.values()[new SecureRandom().nextInt(Game.Type.values().length)];
        var cells = new Cell[MAX_Y][MAX_X];
        switch (type.getLeft()) {
            case "L5":
                cells[0][0] = Cell.Creator.withBounds("_ |_").color(Cell.Color.BLUE).create();
                cells[1][0] = Cell.Creator.withBounds("| __").color(Cell.Color.RED).spawnPoint().create();
                cells[2][0] = null;
                cells[0][1] = Cell.Creator.withBounds("_ _ ").color(Cell.Color.BLUE).create();
                switch (type.getRight()) {
                    case "R5":
                        cells[1][1] = Cell.Creator.withBounds("_ _ ").color(Cell.Color.RED).create();
                        cells[2][1] = Cell.Creator.withBounds("| __").color(Cell.Color.WHITE).create();
                        break;
                    case "R6":
                        cells[1][1] = Cell.Creator.withBounds("__| ").color(Cell.Color.RED).create();
                        cells[2][1] = Cell.Creator.withBounds("||__").color(Cell.Color.WHITE).create();
                        break;
                }
                break;
            case "L6":
                cells[0][0] = Cell.Creator.withBounds("_| _").color(Cell.Color.RED).create();
                cells[1][0] = Cell.Creator.withBounds(" _|_").color(Cell.Color.RED).spawnPoint().create();
                cells[2][0] = Cell.Creator.withBounds("| __").color(Cell.Color.WHITE).create();
                cells[0][1] = Cell.Creator.withBounds("_ ||").color(Cell.Color.BLUE).create();
                switch (type.getRight()) {
                    case "R5":
                        cells[1][1] = Cell.Creator.withBounds("| |_").color(Cell.Color.PURPLE).create();
                        cells[2][1] = Cell.Creator.withBounds("| _ ").color(Cell.Color.WHITE).create();
                        break;
                    case "R6":
                        cells[1][1] = Cell.Creator.withBounds("|_|_").color(Cell.Color.PURPLE).create();
                        cells[2][1] = Cell.Creator.withBounds("||_ ").color(Cell.Color.WHITE).create();
                        break;
                }
                break;
        }
        switch (type.getRight()) {
            case "R5":
                cells[0][2] = Cell.Creator.withBounds("__| ").color(Cell.Color.BLUE).spawnPoint().create();
                cells[1][2] = Cell.Creator.withBounds("||_ ").color(type.getLeft().equals("L5") ? Cell.Color.RED : Cell.Color.PURPLE).create();
                cells[2][2] = Cell.Creator.withBounds("_|_ ").color(Cell.Color.WHITE).create();
                cells[0][3] = null;
                cells[1][3] = Cell.Creator.withBounds("__ |").color(Cell.Color.YELLOW).create();
                cells[2][3] = Cell.Creator.withBounds(" __|").color(Cell.Color.YELLOW).spawnPoint().create();
                break;
            case "R6":
                cells[0][2] = Cell.Creator.withBounds("_|| ").color(Cell.Color.BLUE).spawnPoint().create();
                cells[1][2] = Cell.Creator.withBounds("|  _").color(Cell.Color.YELLOW).create();
                cells[2][2] = Cell.Creator.withBounds("  _|").color(Cell.Color.YELLOW).create();
                cells[0][3] = Cell.Creator.withBounds("__||").color(Cell.Color.GREEN).create();
                cells[1][3] = Cell.Creator.withBounds("|_  ").color(Cell.Color.YELLOW).create();
                cells[2][3] = Cell.Creator.withBounds(" __ ").color(Cell.Color.YELLOW).spawnPoint().create();
                break;
        }
        ArrayList<User> users = Collections.nCopies(5, null).parallelStream().map(f -> new User(UUID.randomUUID().toString())).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Player> players = new ArrayList<>();
        users.forEach(e -> players.add(new Player(e, Player.BoardType.values()[new SecureRandom().nextInt(Player.BoardType.values().length)])));
        Game gameTest = new Game();
        assertNotNull(gameTest.getCells());
        assertNotNull(gameTest.getUuid());
        assertNotNull(gameTest.getType());
        assertFalse(gameTest.isCompleted());
        gameTest = new Game(UUID.randomUUID(), type, cells, players, new SecureRandom().nextInt(4) + 5);
        assertTrue(gameTest.getSkulls() >= 5 && gameTest.getSkulls() <= 8);
        assertTrue(gameTest.getTagbackPlayers().isEmpty());
        assertThrows(SelfResponseException.class , gameTest::getTagbackedPlayer);
        assertFalse(gameTest.isAReborn());
        assertFalse(gameTest.isATagbackResponse());
        assertNull(gameTest.getCell(null));
        for (int i = 0; i < 100; i++) {
            Point point = new Point(new SecureRandom().nextInt(6), new SecureRandom().nextInt(6));
            if (point.getX() >= cells.length) assertNull(gameTest.getCell(point));
            else {
                if (point.getY() >= cells[(int) point.getX()].length) assertNull(gameTest.getCell(point));
                else if (!(point.getX() == 0 && point.getY() == 3) && !(point.getX() == 2 && point.getY() == 0)) {
                    assertNotNull(gameTest.getCell(point));
                    assertTrue(gameTest.getPlayersAtPosition(point).isEmpty());
                }
            }
        }
        assertTrue(gameTest.players.containsAll(players));
        assertEquals(gameTest.getActualPlayer(), gameTest.players.get(0));
        gameTest.addToLastsDamaged(gameTest.players.get(2));
        assertEquals(1, gameTest.getLastsDamaged().size());
        assertTrue(gameTest.getLastsDamaged().contains(gameTest.players.get(2).getUuid()));
        gameTest.getPlayers().get(2).addPowerUp(new PowerUp(AmmoCard.Color.BLUE, PowerUp.Type.TAGBACK_GRENADE));
        assertEquals(1, gameTest.getTagbackPlayers().size());
        for (Cell.Color color : Cell.Color.values()) {
            assertTrue(gameTest.getWeapons(color).isEmpty());
        }
    }

    @Test
    void testMovementsFIVE_FIVE() {
        Game game = createGame(Game.Type.FIVE_FIVE);
        while (game.seqPlay < game.getPlayers().size()) {
            game.getActualPlayer().setPosition(new Point(1, 0));
            game.seqPlay++;
        }
        assertTrue(game.getPlayers().parallelStream().allMatch(e -> e.getPosition().equals(new Point(1, 0))));
        assertTrue(game.canMove(game.getActualPlayer().getPosition(), new Point(0, 1), 2));
        assertFalse(game.canMove(game.getPlayers().get(1).getPosition(), new Point(2, 2), 2));
        assertFalse(game.canMove(null, new Point(2, 3), 3));
        assertFalse(game.canMove(new Point(2, 3), null, 3));
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
    Game createGame(@NotNull Game.Type type) {
        var cells = new Cell[MAX_Y][MAX_X];
        switch (type.getLeft()) {
            case "L5":
                cells[0][0] = Cell.Creator.withBounds("_ |_").color(Cell.Color.BLUE).create();
                cells[1][0] = Cell.Creator.withBounds("| __").color(Cell.Color.RED).spawnPoint().create();
                cells[2][0] = null;
                cells[0][1] = Cell.Creator.withBounds("_ _ ").color(Cell.Color.BLUE).create();
                switch (type.getRight()) {
                    case "R5":
                        cells[1][1] = Cell.Creator.withBounds("_ _ ").color(Cell.Color.RED).create();
                        cells[2][1] = Cell.Creator.withBounds("| __").color(Cell.Color.WHITE).create();
                        break;
                    case "R6":
                        cells[1][1] = Cell.Creator.withBounds("__| ").color(Cell.Color.RED).create();
                        cells[2][1] = Cell.Creator.withBounds("||__").color(Cell.Color.WHITE).create();
                        break;
                }
                break;
            case "L6":
                cells[0][0] = Cell.Creator.withBounds("_| _").color(Cell.Color.RED).create();
                cells[1][0] = Cell.Creator.withBounds(" _|_").color(Cell.Color.RED).spawnPoint().create();
                cells[2][0] = Cell.Creator.withBounds("| __").color(Cell.Color.WHITE).create();
                cells[0][1] = Cell.Creator.withBounds("_ ||").color(Cell.Color.BLUE).create();
                switch (type.getRight()) {
                    case "R5":
                        cells[1][1] = Cell.Creator.withBounds("| |_").color(Cell.Color.PURPLE).create();
                        cells[2][1] = Cell.Creator.withBounds("| _ ").color(Cell.Color.WHITE).create();
                        break;
                    case "R6":
                        cells[1][1] = Cell.Creator.withBounds("|_|_").color(Cell.Color.PURPLE).create();
                        cells[2][1] = Cell.Creator.withBounds("||_ ").color(Cell.Color.WHITE).create();
                        break;
                }
                break;
        }
        switch (type.getRight()) {
            case "R5":
                cells[0][2] = Cell.Creator.withBounds("__| ").color(Cell.Color.BLUE).spawnPoint().create();
                cells[1][2] = Cell.Creator.withBounds("||_ ").color(type.getLeft().equals("L5") ? Cell.Color.RED : Cell.Color.PURPLE).create();
                cells[2][2] = Cell.Creator.withBounds("_|_ ").color(Cell.Color.WHITE).create();
                cells[0][3] = null;
                cells[1][3] = Cell.Creator.withBounds("__ |").color(Cell.Color.YELLOW).create();
                cells[2][3] = Cell.Creator.withBounds(" __|").color(Cell.Color.YELLOW).spawnPoint().create();
                break;
            case "R6":
                cells[0][2] = Cell.Creator.withBounds("_|| ").color(Cell.Color.BLUE).spawnPoint().create();
                cells[1][2] = Cell.Creator.withBounds("|  _").color(Cell.Color.YELLOW).create();
                cells[2][2] = Cell.Creator.withBounds("  _|").color(Cell.Color.YELLOW).create();
                cells[0][3] = Cell.Creator.withBounds("__||").color(Cell.Color.GREEN).create();
                cells[1][3] = Cell.Creator.withBounds("|_  ").color(Cell.Color.YELLOW).create();
                cells[2][3] = Cell.Creator.withBounds(" __ ").color(Cell.Color.YELLOW).spawnPoint().create();
                break;
        }
        return new Game(UUID.randomUUID(), type, cells, createPlayers(), new SecureRandom().nextInt(4) + 5);
    }
}
