package it.polimi.ingsw.common.models;

import org.junit.jupiter.api.RepeatedTest;

import java.security.SecureRandom;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @RepeatedTest(value = 100)
    void testPlayer() {
        Player playerA = new Player(new User("A"), Player.BoardType.values()[new SecureRandom().nextInt(Player.BoardType.values().length)]);
        Player playerB = new Player(new User("B"), Player.BoardType.values()[new SecureRandom().nextInt(Player.BoardType.values().length)]);
        Player playerC = new Player(new User("C"), Player.BoardType.values()[new SecureRandom().nextInt(Player.BoardType.values().length)]);
        ArrayList<Player> players = new ArrayList<>();
        players.add(playerA);
        players.add(playerB);
        players.add(playerC);
        for (Player player : players) {
            assertNotNull(player.getUuid());
            assertNull(player.getPosition());
            assertNotNull(player.getPowerUps());
            assertNotNull(player.getMarksTaken());
            assertFalse(player.isEasyBoard());
            for (AmmoCard.Color color : AmmoCard.Color.values()) {
                assertEquals(3, player.getColoredCubes(color));
            }
            assertEquals(8, player.getMaximumPoints());
            assertEquals(0, player.getPoints());
            assertEquals(0, player.getPowerUps().size());
            assertEquals(0, player.getDamagesTaken().size());
            assertNotNull(player.getNickname());
            assertEquals(0, player.getWeaponsSize());
        }

    }
}
