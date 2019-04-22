package it.polimi.ingsw.client.network;

import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.network.API;
import it.polimi.ingsw.common.network.GameListener;
import it.polimi.ingsw.common.network.RoomListener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public @Nullable String authUser(@NotNull String nickname, @NotNull String password) throws RemoteException {
        return remote.authUser(nickname, password);
    }

    @Override
    public @Nullable String createUser(@NotNull String nickname, @NotNull String password) throws RemoteException {
        return remote.createUser(nickname, password);
    }

    @Override
    public @Nullable UUID getActiveGame(@NotNull String token) throws RemoteException {
        return remote.getActiveGame(token);
    }

    @Override
    public @Nullable List<Room> getRooms(@NotNull String token) throws RemoteException {
        return remote.getRooms(token);
    }

    @Override
    public @Nullable Room joinRoom(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException {
        return remote.joinRoom(token, roomUuid);
    }

    @Override
    public @Nullable Room createRoom(@NotNull String token, @NotNull String name) throws RemoteException {
        return remote.createRoom(token, name);
    }

    @Override
    public @Nullable UUID startGame(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException {
        return remote.startGame(token, roomUuid);
    }

    @Override
    public boolean doMove(@NotNull String token, @NotNull Object move) throws RemoteException {
        return remote.doMove(token, move);
    }

    @Override
    public void addGameListener(@NotNull String token, @NotNull GameListener gameListener) throws RemoteException {
        remote.addGameListener(token, (GameListener) UnicastRemoteObject.exportObject(gameListener, 0));
    }

    @Override
    public void addRoomListener(@NotNull String token, @NotNull RoomListener roomListener) throws RemoteException {
        remote.addRoomListener(token, (RoomListener) UnicastRemoteObject.exportObject(roomListener, 0));
    }

    @Override
    public void removeGameListener(@NotNull String token) throws RemoteException {
        remote.removeGameListener(token);
    }

    @Override
    public void removeRoomListener(@NotNull String token) throws RemoteException {
        remote.removeRoomListener(token);
    }
}
