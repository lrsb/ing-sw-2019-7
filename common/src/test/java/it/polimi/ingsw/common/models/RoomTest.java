package it.polimi.ingsw.common.models;

import org.junit.jupiter.api.RepeatedTest;

import java.security.SecureRandom;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    @RepeatedTest(value = 100)
    void testCreateRoom() {
        SecureRandom rand = new SecureRandom();
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
            var userPlayer = possibleUserPlayer.get(rand.nextInt(possibleUserPlayer.size()));
            if (room.addUser(userPlayer) && rand.nextBoolean())
                room.removeUser(possibleUserPlayer.get(rand.nextInt(possibleUserPlayer.size())));
        }
        User fake = new User("");
        boolean bool = room.addUser(fake);
        assertTrue(room.getUsers().size() == 5 && !bool || room.getUsers().size() <= 5 && bool);
        room.removeUser(fake);
        var time = rand.nextInt(100) - 25;
        room.setStartTime(time);
        assertEquals(room.getStartTime(), time);
        room.setActionTimeout(rand.nextInt(200));
        assertTrue(room.getActionTimeout() >= 60 && room.getActionTimeout() <= 120 || room.getActionTimeout() == 30);
        room.setGameCreated();
        assertEquals(nPlayers, room.getUsers().size(), "Not the number of players expected");
        assertNotEquals(null, room.getName(), "Lack of game's name");
        assertNotEquals(null, room.getUuid(), "Lack of game's uuid");
        assertNotEquals(null, room.getGameType(), "Lack of map");
        assert (room.getSkulls() >= 5 && room.getSkulls() <= 8) : "Wrong number of skulls";
        assert (room.isGameCreated()) : "Game not created";
        assert (room.getUsers().parallelStream().allMatch(e -> possibleUserPlayer.contains(e) || e.equals(creator)));
    }
}
