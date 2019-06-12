package it.polimi.ingsw.server.models;

import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.models.RoomTest;
import org.junit.jupiter.api.Test;

class GameImplTest {
    @Test
    void TestRunGame() {
        Room roomTest = RoomTest.testCreateRoom();
        GameImpl gameImplTest = GameImpl.Creator.newGame(roomTest);

    }
}
