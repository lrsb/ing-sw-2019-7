package it.polimi.ingsw.server.models;

import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.models.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class GameImplTest {
    @Test
    void TestRunGame() {
        Random rand = new Random();
        String gameName = "NomePartita";
        User creator = new User("God");
        ArrayList<User> possibleUserPlayer = new ArrayList<>();
        possibleUserPlayer.add(new User("Luca"));
        possibleUserPlayer.add(new User("Federico"));
        possibleUserPlayer.add(new User("Fabio"));
        possibleUserPlayer.add(new User("Lore"));
        possibleUserPlayer.add(new User("Tia"));
        possibleUserPlayer.add(new User("Cugola"));
        possibleUserPlayer.add(new User("Albertazzi"));
        int nPlayers = rand.nextInt(2) + 3;
        Room room = new Room(gameName, creator);
        room.setGameType(Game.Type.values()[rand.nextInt(Game.Type.values().length)]);
        room.setSkulls(rand.nextInt());
        while (room.getUsers().size() < nPlayers) {
            var userPlayer = possibleUserPlayer.get(rand.nextInt(possibleUserPlayer.size() - 1));
            if (!room.getUsers().contains(userPlayer)) room.addUser(userPlayer);
        }
        room.setGameCreated();
        assertEquals(nPlayers, room.getUsers().size(), "Not the number of players expected");
        assertNotEquals(null, room.getName(), "Lack of game's name");
        assertNotEquals(null, room.getUuid(), "Lack of game's uuid");
        assertNotEquals(null, room.getGameType(), "Lack of map");
        assert (room.getSkulls() >= 5 && room.getSkulls() <= 8) : "Wrong number of skulls";
        assert (room.isGameCreated()) : "Game not created";
        assert (room.getUsers().parallelStream().allMatch(e -> possibleUserPlayer.contains(e) || e.equals(creator)));
        //todo: create game with param 'room'
    }
}
