package it.polimi.ingsw.server.models;

import it.polimi.ingsw.common.models.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class GameImplTest {
    @Test
    void testRunGame() {
        var room = new Room("", new User(""));
        Collections.nCopies(5, null).parallelStream().map(f -> new User(UUID.randomUUID().toString())).collect(Collectors.toList()).forEach(room::addUser);
        GameImpl gameImplTest = GameImpl.Creator.newGame(room);
        assert (gameImplTest.getPlayers().size() >= 3 && gameImplTest.getPlayers().size() <= 5) : "Wrong number of players";
        for (Player player : gameImplTest.getPlayers()) {
            for (AmmoCard.Color color : AmmoCard.Color.values()) {
                assertEquals(3, player.getColoredCubes(color));
                assertNull(player.getPosition());
                assertEquals(0, player.getPoints());
                assertEquals(0, player.getDamagesTaken().size());
                assertEquals(0, player.getMarksTaken().size());
                assertEquals(player.equals(gameImplTest.getActualPlayer()) ? 2 : 0, player.getPowerUps().size());
                assertEquals(8, player.getMaximumPoints());
            }
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                var cell = gameImplTest.getCell(new Point(i, j));
                assertTrue(cell == null || cell.isSpawnPoint() && cell.getAmmoCard() == null && gameImplTest.getWeapons(cell.getColor()).size() == 3 ||
                        !cell.isSpawnPoint() && cell.getAmmoCard() != null);
            }
        }
    }

    @Test
    void testStartCondition() {
        GameImpl gameImpl = createGameImpl(Game.Type.FIVE_FIVE);
        for (Player player : gameImpl.getPlayers()) {
            assertNull(player.getPosition());
            for (AmmoCard.Color color : AmmoCard.Color.values()) {
                assertEquals(3, player.getColoredCubes(color));
            }
            assertEquals(0, player.getWeaponsSize());
            assertEquals(player.equals(gameImpl.getActualPlayer()) ? 2 : 0, player.getPowerUps().size());
        }
        for (int i = 0; i < gameImpl.getPlayers().size(); i++) {
            assertEquals(gameImpl.getPlayers().get(i), gameImpl.getActualPlayer());
            assertEquals(2, gameImpl.getActualPlayer().getPowerUps().size());
            AmmoCard.Color expectedColor = gameImpl.getActualPlayer().getPowerUps().get(0).getAmmoColor();
            assertTrue(gameImpl.doAction(reborningAction(gameImpl.getActualPlayer().getPowerUps().get(0), gameImpl)));
            assertEquals(1, gameImpl.getActualPlayer().getPowerUps().size());
            assertTrue(gameImpl.getCell(gameImpl.getActualPlayer().getPosition()).isSpawnPoint());
            assertEquals(expectedColor.toString(), gameImpl.getCell(gameImpl.getActualPlayer().getPosition()).getColor().toString());
            assertTrue(gameImpl.doAction(Action.Builder.create(gameImpl.getUuid()).buildNextTurn()));
        }
        //Set posizioni giocatori
        gameImpl.getPlayers().get(0).setPosition(new Point(0, 0));
        gameImpl.getPlayers().get(1).setPosition(new Point(1, 2));
        gameImpl.getPlayers().get(2).setPosition(new Point(2, 2));
        gameImpl.getPlayers().get(3).setPosition(new Point(2, 2));
        gameImpl.getPlayers().get(4).setPosition(new Point(2, 3));
        //tolgo powerUps
        for (Player player : gameImpl.getPlayers()) {
            PowerUp powerUp = player.getPowerUps().get(0);
            player.removePowerUp(powerUp);
            assertEquals(0, player.getPowerUps().size());
        }
        //Turno 1, giocatore 1
        assertEquals(2, gameImpl.getRemainedActions());
        gameImpl.getActualPlayer().addWeapon(Weapon.LOCK_RIFLE);
        ArrayList<UUID> targets = new ArrayList<>();
        targets.add(gameImpl.getPlayers().get(1).getUuid());
        assertTrue(gameImpl.doAction(Action.Builder.create(gameImpl.getUuid()).buildFireAction(Weapon.LOCK_RIFLE, new Point(0, 0),
                null, false, 0, targets, null, new ArrayList<>(),
                null, new ArrayList<>(), null)));
        assertEquals(1, gameImpl.getRemainedActions());
        assertEquals(2, gameImpl.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(1, gameImpl.getPlayers().get(1).getMarksTaken().size());
        assertTrue(gameImpl.getPlayers().get(1).getDamagesTaken().contains(gameImpl.getActualPlayer().getUuid()));
        assertFalse(gameImpl.getActualPlayer().isALoadedGun(Weapon.LOCK_RIFLE));
        assertFalse(gameImpl.doAction(Action.Builder.create(gameImpl.getUuid()).buildReload(Weapon.LOCK_RIFLE, null)));
        assertFalse(gameImpl.doAction(Action.Builder.create(gameImpl.getUuid()).buildAmmoCardGrabAction(new Point(1, 0))));
        assertEquals(1, gameImpl.getRemainedActions());
        assertTrue(gameImpl.doAction(Action.Builder.create(gameImpl.getUuid()).buildMoveAction(new Point(2, 1))));
        assertEquals(new Point(2, 1), gameImpl.getActualPlayer().getPosition());
        assertTrue(gameImpl.doAction(Action.Builder.create(gameImpl.getUuid()).buildReload(Weapon.LOCK_RIFLE, null)));
        assertTrue(gameImpl.getActualPlayer().isALoadedGun(Weapon.LOCK_RIFLE));
        assertEquals(1, gameImpl.getActualPlayer().getColoredCubes(AmmoCard.Color.BLUE));
        assertTrue(gameImpl.doAction(Action.Builder.create(gameImpl.getUuid()).buildNextTurn()));
        //Turno 1, giocatore 2
        assertEquals(gameImpl.getPlayers().get(1), gameImpl.getActualPlayer());
        gameImpl.getActualPlayer().addWeapon(Weapon.ELECTROSCYTHE);
        assertTrue(gameImpl.doAction(Action.Builder.create(gameImpl.getUuid()).buildMoveAction(new Point(2, 2))));
        targets.clear();
        assertTrue(gameImpl.doAction(Action.Builder.create(gameImpl.getUuid()).buildFireAction(Weapon.ELECTROSCYTHE, new Point(2, 2),
                null, true, 0, targets, null, new ArrayList<>(),
                null, new ArrayList<>(), null)));
        assertEquals(2, gameImpl.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(2, gameImpl.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(2, gameImpl.getPlayers().get(3).getDamagesTaken().size());
        assertTrue(gameImpl.getPlayers().get(2).getDamagesTaken().contains(gameImpl.getActualPlayer().getUuid()));
        assertTrue(gameImpl.getPlayers().get(3).getDamagesTaken().contains(gameImpl.getActualPlayer().getUuid()));
        assertTrue(gameImpl.doAction(Action.Builder.create(gameImpl.getUuid()).buildReload(Weapon.ELECTROSCYTHE, null)));
        assertEquals(1, gameImpl.getActualPlayer().getColoredCubes(AmmoCard.Color.BLUE));
        assertEquals(2, gameImpl.getActualPlayer().getColoredCubes(AmmoCard.Color.RED));
    }

    @RepeatedTest(value = 100)
    void testGrabItems() {
        GameImpl game = createGameImpl(Game.Type.FIVE_FIVE);
        game.getActualPlayer().setPosition(new Point(0, 0));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildWeaponGrabAction(new Point(1, 0), game.getWeapons(Cell.Color.RED).get(0), null, null)));
        assertEquals(2, game.getWeapons(Cell.Color.RED).size());
        assertEquals(1, game.getActualPlayer().getWeaponsSize());
        for (AmmoCard.Color color : AmmoCard.Color.values())
            assertEquals(3 - game.getActualPlayer().getWeapons().get(0).getGrabCost(color), game.getActualPlayer().getColoredCubes(color));
        assertEquals(new Point(1, 0), game.getActualPlayer().getPosition());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildWeaponGrabAction(new Point(1, 0), game.getWeapons(Cell.Color.BLUE).get(0), null, null)));
        assertEquals(2, game.getWeapons(Cell.Color.RED).size());
        assertEquals(3, game.getWeapons(Cell.Color.BLUE).size());
        assertEquals(1, game.getActualPlayer().getWeaponsSize());
        assertEquals(new Point(1, 0), game.getActualPlayer().getPosition());
        for (AmmoCard.Color color : AmmoCard.Color.values())
            assertEquals(3 - game.getActualPlayer().getWeapons().get(0).getGrabCost(color), game.getActualPlayer().getColoredCubes(color));
        game.getPlayers().get(1).setPosition(new Point(0, 1));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        assertEquals(3, game.getWeapons(Cell.Color.RED).size());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildAmmoCardGrabAction(new Point(0, 2))));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildWeaponGrabAction(new Point(0, 2), game.getWeapons(Cell.Color.BLUE).get(0), null, null)));
        for (AmmoCard.Color color: AmmoCard.Color.values())
            game.getActualPlayer().removeColoredCubes(color, game.getActualPlayer().getColoredCubes(color));
        while (!game.getActualPlayer().getPowerUps().isEmpty()) game.getActualPlayer().removePowerUp(game.getActualPlayer().getPowerUps().get(0));
        AmmoCard ammoCard = game.getCell(new Point(0, 1)).getAmmoCard();
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildAmmoCardGrabAction(new Point(0, 1))));
        assertEquals(ammoCard.getType().equals(AmmoCard.Type.POWER_UP) ? 1 : 0, game.getActualPlayer().getPowerUps().size());
        switch (ammoCard.getType()) {
            case POWER_UP:
                assertTrue(game.getActualPlayer().getColoredCubes(ammoCard.getLeft()) == 1 &&
                        game.getActualPlayer().getColoredCubes(ammoCard.getRight()) == 1 ||
                        game.getActualPlayer().getColoredCubes(ammoCard.getLeft()) == 2 &&
                                game.getActualPlayer().getColoredCubes(ammoCard.getRight()) == 2);
                break;
            case RED:
                assertEquals(1, game.getActualPlayer().getColoredCubes(AmmoCard.Color.RED));
                assertEquals(2, game.getActualPlayer().getColoredCubes(ammoCard.getLeft()));
                assertEquals(2, game.getActualPlayer().getColoredCubes(ammoCard.getRight()));
                break;
            case YELLOW:
                assertEquals(1, game.getActualPlayer().getColoredCubes(AmmoCard.Color.YELLOW));
                assertEquals(2, game.getActualPlayer().getColoredCubes(ammoCard.getLeft()));
                assertEquals(2, game.getActualPlayer().getColoredCubes(ammoCard.getRight()));
                break;
            case BLUE:
                assertEquals(1, game.getActualPlayer().getColoredCubes(AmmoCard.Color.BLUE));
                assertEquals(2, game.getActualPlayer().getColoredCubes(ammoCard.getLeft()));
                assertEquals(2, game.getActualPlayer().getColoredCubes(ammoCard.getRight()));
                break;
            default:
                break;
        }
        assertEquals(3, game.getActualPlayer().getPowerUps().size() +
                game.getActualPlayer().getColoredCubes(AmmoCard.Color.RED) +
                game.getActualPlayer().getColoredCubes(AmmoCard.Color.YELLOW) +
                game.getActualPlayer().getColoredCubes(AmmoCard.Color.BLUE));
    }

    @Test
    GameImpl createGameImpl(@NotNull Game.Type type) {
        String gameName = "NomePartita";
        User creator = new User("God");
        ArrayList<User> possibleUserPlayer = new ArrayList<>();
        possibleUserPlayer.add(new User("Luca"));
        possibleUserPlayer.add(new User("Federico"));
        possibleUserPlayer.add(new User("Lore"));
        possibleUserPlayer.add(new User("Tia"));
        Room room = new Room(gameName, creator);
        room.setGameType(type);
        room.setSkulls(5);
        while (room.getUsers().size() < 5) room.addUser(possibleUserPlayer.get(room.getUsers().size() - 1));
        return GameImpl.Creator.newGame(room);
    }

    Action reborningAction(@NotNull PowerUp toThrow, @NotNull GameImpl game) {
        return Action.Builder.create(game.getUuid()).buildReborn(toThrow.getType(), toThrow.getAmmoColor());
    }
}
