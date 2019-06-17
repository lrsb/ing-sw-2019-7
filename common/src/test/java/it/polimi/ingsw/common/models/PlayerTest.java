package it.polimi.ingsw.common.models;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.UUID;

import static it.polimi.ingsw.common.models.Game.MAX_X;
import static it.polimi.ingsw.common.models.Game.MAX_Y;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @RepeatedTest(value = 100)
    void testInitializedValuesOfPlayer() {
        Game game = createGame();
        for (Player player : game.getPlayers()) {
            assertNotNull(player.getUuid());
            assertNotNull(player.getNickname());
            assertNull(player.getPosition());
            assertNotNull(player.getBoardType());
            assertNotNull(player.getPowerUps());
            assertNotNull(player.getMarksTaken());
            assertFalse(player.isEasyBoard());
            for (AmmoCard.Color color : AmmoCard.Color.values()) {
                assertEquals(3, player.getColoredCubes(color));
                for (PowerUp.Type type : PowerUp.Type.values()) assertFalse(player.hasPowerUp(new PowerUp(color, type)));
            }
            for (Weapon weapon : Weapon.values()) assertFalse(player.hasWeapon(weapon));
            assertEquals(8, player.getMaximumPoints());
            assertEquals(0, player.getPoints());
            assertEquals(0, player.getPowerUps().size());
            assertEquals(0, player.getDamagesTaken().size());
            assertNotNull(player.getNickname());
            assertEquals(0, player.getWeaponsSize());
            player.setEasyBoard();
            assertTrue(player.isEasyBoard());
            assertEquals(2, player.getMaximumPoints());
        }
    }

    @RepeatedTest(value = 100)
    void testModifierOfItems () {
        Game game = createGame();
        for (Player player : game.getPlayers()) {
            for (AmmoCard.Color color : AmmoCard.Color.values()) {
                assertEquals(3, player.getColoredCubes(color));
                player.removeColoredCubes(color, 10);
                assertEquals(0, player.getColoredCubes(color));
            }
            int numberOfCharging = new SecureRandom().nextInt(4);
            for (int i = 0; i < numberOfCharging; i++) {
                int redCubes = player.getColoredCubes(AmmoCard.Color.RED);
                int yellowCubes = player.getColoredCubes(AmmoCard.Color.YELLOW);
                int blueCubes = player.getColoredCubes(AmmoCard.Color.BLUE);
                ArrayList<PowerUp> powerUps = new ArrayList<>(player.getPowerUps());
                AmmoCard ammoCard = new AmmoCard(AmmoCard.Type.values()[new SecureRandom().nextInt(AmmoCard.Type.values().length)],
                        AmmoCard.Color.values()[new SecureRandom().nextInt(AmmoCard.Color.values().length)],
                        AmmoCard.Color.values()[new SecureRandom().nextInt(AmmoCard.Color.values().length)]);
                PowerUp powerUp = ammoCard.getType().equals(AmmoCard.Type.POWER_UP) ?
                        new PowerUp(AmmoCard.Color.values()[new SecureRandom().nextInt(AmmoCard.Color.values().length)],
                                PowerUp.Type.values()[new SecureRandom().nextInt(PowerUp.Type.values().length)]) : null;
                player.ammoCardRecharging(ammoCard, powerUp);
                int addedRed = 0;
                int addedYellow = 0;
                int addedBlue = 0;
                switch (ammoCard.getType()) {
                    case POWER_UP:
                        assertEquals(powerUps.size() + 1, player.getPowerUps().size());
                        assertNotNull(powerUp);
                        assertTrue(player.hasPowerUp(powerUp));
                        break;
                    case RED:
                        assertNull(powerUp);
                        addedRed++;
                        break;
                    case YELLOW:
                        assertNull(powerUp);
                        addedYellow++;
                        break;
                    case BLUE:
                        assertNull(powerUp);
                        addedBlue++;
                        break;
                }
                switch (ammoCard.getLeft()) {
                    case RED:
                        addedRed++;
                        break;
                    case YELLOW:
                        addedYellow++;
                        break;
                    case BLUE:
                        addedBlue++;
                        break;
                }
                switch (ammoCard.getRight()) {
                    case RED:
                        addedRed++;
                        break;
                    case YELLOW:
                        addedYellow++;
                        break;
                    case BLUE:
                        addedBlue++;
                        break;
                }
                if (blueCubes + addedBlue > 3) assertEquals(3, player.getColoredCubes(AmmoCard.Color.BLUE));
                else assertEquals(blueCubes + addedBlue, player.getColoredCubes(AmmoCard.Color.BLUE));
                if (yellowCubes + addedYellow > 3) assertEquals(3, player.getColoredCubes(AmmoCard.Color.YELLOW));
                else assertEquals(yellowCubes + addedYellow, player.getColoredCubes(AmmoCard.Color.YELLOW));
                if (redCubes + addedRed > 3) assertEquals(3, player.getColoredCubes(AmmoCard.Color.RED));
                else assertEquals(redCubes + addedRed, player.getColoredCubes(AmmoCard.Color.RED));
            }
            int numberOfWeapons = new SecureRandom().nextInt(4);
            while (player.getWeaponsSize() < numberOfWeapons) {
                Weapon weapon = Weapon.values()[new SecureRandom().nextInt(Weapon.values().length)];
                if (game.getPlayers().parallelStream().noneMatch(e -> e.hasWeapon(weapon))) {
                    player.addWeapon(weapon);
                    assertTrue(player.hasWeapon(weapon));
                    assertTrue(player.isALoadedGun(weapon));
                    if (new SecureRandom().nextBoolean()) {
                        player.unloadWeapon(weapon);
                        assertFalse(player.isALoadedGun(weapon));
                    }
                }
            }
        }
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
