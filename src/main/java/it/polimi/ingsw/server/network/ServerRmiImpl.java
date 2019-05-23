package it.polimi.ingsw.server.network;

import it.polimi.ingsw.Server;
import it.polimi.ingsw.common.models.Action;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.network.API;
import it.polimi.ingsw.common.network.GameListener;
import it.polimi.ingsw.common.network.RoomListener;
import org.jetbrains.annotations.NotNull;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.UUID;

public class ServerRmiImpl extends UnicastRemoteObject implements API {
    public ServerRmiImpl() throws RemoteException {
        super();
    }

    @Override
    public @NotNull String authUser(@NotNull String nickname, @NotNull String password) throws RemoteException {
        return Server.controller.authUser(nickname, password);
    }

    @Override
    public @NotNull String createUser(@NotNull String nickname, @NotNull String password) throws RemoteException {
        return Server.controller.createUser(nickname, password);
    }

    @Override
    public @NotNull Game getActiveGame(@NotNull String token) throws RemoteException {
        return Server.controller.getActiveGame(token);
    }

    @Override
    public @NotNull List<Room> getRooms(@NotNull String token) throws RemoteException {
        return Server.controller.getRooms(token);
    }

    @Override
    public @NotNull Room joinRoom(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException {
        return Server.controller.joinRoom(token, roomUuid);
    }

    @Override
    public @NotNull Room createRoom(@NotNull String token, @NotNull String name, int timeout, Game.@NotNull Type gameType) throws RemoteException {
        return Server.controller.createRoom(token, name, timeout, gameType);
    }

    @Override
    public @NotNull Game startGame(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException {
        return Server.controller.startGame(token, roomUuid);
    }

    @Override
    public boolean doAction(@NotNull String token, @NotNull Action action) throws RemoteException {
        return Server.controller.doAction(token, action);
    }

    @Override
    public void addGameListener(@NotNull String token, @NotNull GameListener listener) throws RemoteException {
        Server.controller.addGameListener(token, listener);
    }

    @Override
    public void removeGameListener(@NotNull String token) throws RemoteException {
        Server.controller.removeGameListener(token);
    }

    @Override
    public void addRoomListener(@NotNull String token, @NotNull RoomListener listener) throws RemoteException {
        Server.controller.addRoomListener(token, listener);
    }

    @Override
    public void removeRoomListener(@NotNull String token) throws RemoteException {
        Server.controller.removeRoomListener(token);
    }
}