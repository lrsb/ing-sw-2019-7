package it.polimi.ingsw;

import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.controllers.game.GameViewController;
import it.polimi.ingsw.client.controllers.startup.ConnTypeViewController;
import it.polimi.ingsw.client.controllers.startup.LoginViewController;
import it.polimi.ingsw.client.network.ClientRestImpl;
import it.polimi.ingsw.client.views.cli.StartupCli;
import it.polimi.ingsw.client.views.cli.base.CliMenuManager;
import it.polimi.ingsw.common.models.Action;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.models.User;
import it.polimi.ingsw.common.network.API;
import it.polimi.ingsw.common.network.GameListener;
import it.polimi.ingsw.common.network.RoomListener;
import it.polimi.ingsw.server.models.GameImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

public class Client {
    public static API API;

    public static void main(String[] args) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InterruptedException {
        if (args != null) for (var arg : args)
            switch (arg) {
                case "-h":
                    Client.API = new ClientRestImpl("ing-sw-2019-7.herokuapp.com");
                    new NavigationController(LoginViewController.class);
                    return;
                case "-c":
                    CliMenuManager.startCli(StartupCli.class, false); // TODO da sistemare a true
                    return;
            }
        //test();
        new NavigationController(ConnTypeViewController.class);
    }

    private static void test() {
        var room = new Room("ciao", new User("ciao"));
        room.setGameType(Game.Type.SIX_FIVE);
        var game = GameImpl.Creator.newGame(room);
        API = new API() {
            @Override
            public @NotNull String authUser(@NotNull String nickname, @NotNull String password) throws RemoteException {
                return null;
            }

            @Override
            public @NotNull String createUser(@NotNull String nickname, @NotNull String password) throws RemoteException {
                return null;
            }

            @Override
            public @NotNull Game getActiveGame(@NotNull String token) throws RemoteException {
                return null;
            }

            @Override
            public @NotNull List<Room> getRooms(@NotNull String token) throws RemoteException {
                return null;
            }

            @Override
            public @NotNull Room joinRoom(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException {
                return null;
            }

            @Override
            public @NotNull Room createRoom(@NotNull String token, @NotNull Room room) throws RemoteException {
                return null;
            }

            @Override
            public void quitRoom(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException {

            }

            @Override
            public void startGame(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException {

            }

            @Override
            public void quitGame(@NotNull String token, @NotNull UUID gameUuid) throws RemoteException {

            }

            @Override
            public boolean doAction(@NotNull String token, @NotNull Action action) throws RemoteException {
                return false;
            }

            @Override
            public void addGameListener(@NotNull String token, @NotNull UUID gameUuid, @NotNull GameListener gameListener) throws RemoteException {

            }

            @Override
            public void addRoomListener(@NotNull String token, @NotNull UUID roomUuid, @NotNull RoomListener roomListener) throws RemoteException {

            }

            @Override
            public void removeGameListener(@NotNull String token, @NotNull UUID gameUuid) throws RemoteException {

            }

            @Override
            public void removeRoomListener(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException {

            }
        };
        new NavigationController(GameViewController.class, game);
    }
}