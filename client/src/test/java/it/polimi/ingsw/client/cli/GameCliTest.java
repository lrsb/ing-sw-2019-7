package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.views.cli.GameCli;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.models.User;
import it.polimi.ingsw.server.models.GameImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;

public class GameCliTest {

    @Test
    public void testPrintGameCli() {
        for (Game.Type type : Game.Type.values()) {
            GameImpl game = createGameImpl(type);
            game.getPlayers().get(0).setPosition(new Point(0, 1));
            game.getPlayers().get(1).setPosition(new Point(0, 1));
            game.getPlayers().get(2).setPosition(new Point(2, 1));
            game.getPlayers().get(3).setPosition(new Point(1, 1));
            game.getPlayers().get(4).setPosition(new Point(1, 0));
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
