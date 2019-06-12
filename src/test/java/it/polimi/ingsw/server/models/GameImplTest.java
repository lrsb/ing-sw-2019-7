package it.polimi.ingsw.server.models;

import it.polimi.ingsw.client.views.cli.GameCli;
import it.polimi.ingsw.client.views.cli.base.Segue;
import it.polimi.ingsw.common.models.AmmoCard;
import it.polimi.ingsw.common.models.Player;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.models.RoomTest;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static it.polimi.ingsw.client.views.cli.GameCli.game;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GameImplTest {
    @Test
    void TestRunGame() {
        Room roomTest = RoomTest.testCreateRoom();
        GameImpl gameImplTest = GameImpl.Creator.newGame(roomTest);
        assert (gameImplTest.getPlayers().size() >= 3 && gameImplTest.getPlayers().size() <=5) : "Wrong number of players";
        for (Player player : gameImplTest.getPlayers()) {
            for (AmmoCard.Color color : AmmoCard.Color.values()) {
                assertEquals(3, player.getColoredCubes(color));
                assertNull(player.getPosition());
            }
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                var cell = gameImplTest.getCell(new Point(i,j));
                assert cell == null || cell.isSpawnPoint() && cell.getAmmoCard() == null && gameImplTest.getWeapons(cell.getColor()).size() == 3 ||
                        !cell.isSpawnPoint() && cell.getAmmoCard() != null;
            }
        }
        GameCli gameCli = new GameCli();
        Segue segue = game(gameImplTest);
    }
}
