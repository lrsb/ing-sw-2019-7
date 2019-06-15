package it.polimi.ingsw.server.models;

import it.polimi.ingsw.common.models.AmmoCard;
import it.polimi.ingsw.common.models.Player;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.models.User;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class GameImplTest {
    @Test
    void TestRunGame() {
        var room = new Room("", new User(""));
        Collections.nCopies(5, null).parallelStream().map(f -> new User(UUID.randomUUID().toString())).collect(Collectors.toList()).forEach(room::addUser);
        GameImpl gameImplTest = GameImpl.Creator.newGame(room);
        assert (gameImplTest.getPlayers().size() >= 3 && gameImplTest.getPlayers().size() <=5) : "Wrong number of players";
        for (Player player : gameImplTest.getPlayers()) {
            for (AmmoCard.Color color : AmmoCard.Color.values()) {
                assertEquals(3, player.getColoredCubes(color));
                assertNull(player.getPosition());
                assertEquals(0, player.getPoints());
                assertEquals(0, player.getDamagesTaken().size());
                assertEquals(0, player.getMarksTaken().size());
                assertEquals(0, player.getPowerUps().size());
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
}
