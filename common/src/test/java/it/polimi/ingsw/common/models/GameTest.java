package it.polimi.ingsw.common.models;

import it.polimi.ingsw.common.models.exceptions.SelfResponseException;
import org.junit.jupiter.api.RepeatedTest;

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
                else if (!(point.getX() == 3 && point.getY() == 0) && !(point.getX() == 0 && point.getY() == 2)) {
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
}
