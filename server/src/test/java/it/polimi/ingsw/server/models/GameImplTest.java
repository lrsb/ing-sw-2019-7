package it.polimi.ingsw.server.models;

import it.polimi.ingsw.common.models.*;
import org.jetbrains.annotations.NotNull;
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
