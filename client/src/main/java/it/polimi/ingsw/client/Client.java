package it.polimi.ingsw.client;

import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.controllers.startup.ConnTypeViewController;
import it.polimi.ingsw.client.controllers.startup.LoginViewController;
import it.polimi.ingsw.client.network.ClientRestImpl;
import it.polimi.ingsw.client.views.cli.GameCli;
import it.polimi.ingsw.client.views.cli.base.CliMenuManager;
import it.polimi.ingsw.common.network.API;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Client {
    public static final int SOCKET_PORT = 0xCAFE;
    public static final int RMI_PORT = 0xBABE;
    public static final @NotNull String RMI_NAME = "adrenaline";

    public static API API;

    public static void main(String[] args) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InterruptedException {
        if (args != null) for (var arg : args) {
            switch (arg) {
                default:
                case "--help":
                case "-h":
                    System.out.println("Adrenaline board game client");
                    System.out.println();
                    System.out.println("Option Meaning");
                    System.out.println("-w     Run client in GUI mode with web server located at: ing-sw-2019-7.herokuapp.com");
                    System.out.println("-c     Run client with CLI interface");
                    return;
                case "-w":
                    Client.API = new ClientRestImpl("ing-sw-2019-7.herokuapp.com");
                    new NavigationController(LoginViewController.class);
                    return;
                case "-c":
                    //TODO
                    CliMenuManager.startCli(GameCli.class, false);
                    //CliMenuManager.startCli(StartupCli.class, false);
                    return;
            }
        }
        //TODO
        new NavigationController(ConnTypeViewController.class);
        /*Client.API = new API() {
            @Override
            public @NotNull User.Auth authUser(@NotNull String nickname, @NotNull String password) throws RemoteException {
                return null;
            }

            @Override
            public @NotNull User.Auth createUser(@NotNull String nickname, @NotNull String password) throws RemoteException {
                return null;
            }

            @Override
            public @NotNull Game getActiveGame(@NotNull String token) throws RemoteException {
                return null;
            }

            @Override
            public @NotNull java.util.List<Room> getRooms(@NotNull String token) throws RemoteException {
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
        for (Game.Type type : Game.Type.values()) {
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
            var game = GameImpl.Creator.newGame(room);
            game.getPlayers().get(0).setPosition(new Point(0, 1));
            game.getPlayers().get(1).setPosition(new Point(0, 1));
            game.getPlayers().get(2).setPosition(new Point(2, 1));
            game.getPlayers().get(3).setPosition(new Point(1, 1));
            game.getPlayers().get(4).setPosition(new Point(1, 0));

            new NavigationController(GameViewController.class, game);
            break;
        }*/
    }
}