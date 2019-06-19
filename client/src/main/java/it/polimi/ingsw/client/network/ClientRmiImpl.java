package it.polimi.ingsw.client.network;

import it.polimi.ingsw.common.models.Action;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.models.User;
import it.polimi.ingsw.common.network.API;
import it.polimi.ingsw.common.network.GameListener;
import it.polimi.ingsw.common.network.RoomListener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.UUID;

public class ClientRmiImpl implements API {
    private final @NotNull API remote;

    @Contract(pure = true)
    public ClientRmiImpl(Remote remote) {
        this.remote = (API) remote;
    }

    @Override
    public @NotNull User.Auth authUser(@NotNull String nickname, @NotNull String password) throws RemoteException {
        return remote.authUser(nickname, password);
    }

    @Override
    public @NotNull User.Auth createUser(@NotNull String nickname, @NotNull String password) throws RemoteException {
        return remote.createUser(nickname, password);
    }

    @Override
    public @NotNull Game getActiveGame(@NotNull String token) throws RemoteException {
        return remote.getActiveGame(token);
    }

    @Override
    public @NotNull List<Room> getRooms(@NotNull String token) throws RemoteException {
        return remote.getRooms(token);
    }

    @Override
    public @NotNull Room joinRoom(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException {
        return remote.joinRoom(token, roomUuid);
    }

    @Override
    public @NotNull Room createRoom(@NotNull String token, @NotNull Room room) throws RemoteException {
        return remote.createRoom(token, room);
    }

    @Override
    public void quitRoom(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException {
        remote.quitRoom(token, roomUuid);
    }

    @Override
    public void startGame(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException {
        remote.startGame(token, roomUuid);
    }

    @Override
    public void quitGame(@NotNull String token, @NotNull UUID gameUuid) throws RemoteException {
        remote.quitGame(token, gameUuid);
    }

    @Override
    public boolean doAction(@NotNull String token, @NotNull Action action) throws RemoteException {
        return remote.doAction(token, action);
    }

    @Override
    public void addGameListener(@NotNull String token, @NotNull UUID gameUuid, @NotNull GameListener gameListener) throws RemoteException {
        remote.addGameListener(token, gameUuid, (GameListener) UnicastRemoteObject.exportObject(gameListener, 0));
    }

    @Override
    public void addRoomListener(@NotNull String token, @NotNull UUID roomUuid, @NotNull RoomListener roomListener) throws RemoteException {
        remote.addRoomListener(token, roomUuid, (RoomListener) UnicastRemoteObject.exportObject(roomListener, 0));
    }

    @Override
    public void removeGameListener(@NotNull String token, @NotNull UUID gameUuid) throws RemoteException {
        remote.removeGameListener(token, gameUuid);
    }

    @Override
    public void removeRoomListener(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException {
        remote.removeRoomListener(token, roomUuid);
    }
}