package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.views.cli.GameCli;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.models.User;
import it.polimi.ingsw.server.models.GameImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.ArrayList;

public class GameCliTest {

    @Test
    public void testPrintGameCli() {
        for (Game.Type type : Game.Type.values()) {
            GameImpl game = createGameImpl(type);
            GameCli.printGame(game);
        }
    }

    @Test
    public void testPrintPreCli() {
        for (Game.Type type : Game.Type.values()) {
            GameImpl game = createGameImpl(type);
            GameCli.preGame(game);
        }
    }

    @Test
    public void testPrintPostCli() {
            GameCli.postGame();
    }

    private @NotNull GameImpl createGameImpl(@NotNull Game.Type type) {
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

}
