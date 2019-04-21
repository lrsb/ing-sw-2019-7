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
    private final @NotNull API rmiAPI;

    @Contract(pure = true)
    public ClientRmiImpl(Remote netComm) {
        this.rmiAPI = (API) netComm;
    }

    @Override
    public @Nullable String authUser(@NotNull String nickname, @NotNull String password) throws RemoteException {
        return rmiAPI.authUser(nickname, password);
    }

    @Override
    public @Nullable String createUser(@NotNull String nickname, @NotNull String password) throws RemoteException {
        return rmiAPI.createUser(nickname, password);
    }

    @Override
    public @Nullable List<Room> getRooms(@NotNull String token) throws RemoteException {
        return rmiAPI.getRooms(token);
    }

    @Override
    public @Nullable Room joinRoom(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException {
        return rmiAPI.joinRoom(token, roomUuid);
    }

    @Override
    public @Nullable Room createRoom(@NotNull String token, @NotNull String name) throws RemoteException {
        return rmiAPI.createRoom(token, name);
    }

    @Override
    public @Nullable UUID startGame(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException {
        return rmiAPI.startGame(token, roomUuid);
    }

    @Override
    public boolean doMove(@NotNull String token, @NotNull Object move) throws RemoteException {
        return rmiAPI.doMove(token, move);
    }

    @Override
    public void addGameListener(@NotNull String token, @NotNull GameListener gameListener) throws RemoteException {
        rmiAPI.addGameListener(token, (GameListener) UnicastRemoteObject.exportObject(gameListener, 0));
    }

    @Override
    public void addRoomListener(@NotNull String token, @NotNull RoomListener roomListener) throws RemoteException {
        rmiAPI.addGameListener(token, (GameListener) UnicastRemoteObject.exportObject(roomListener, 0));
    }

    @Override
    public void removeGameListener(@NotNull String token) throws RemoteException {
        rmiAPI.removeGameListener(token);
    }

    @Override
    public void removeRoomListener(@NotNull String token) throws RemoteException {
        rmiAPI.removeRoomListener(token);
    }
}
