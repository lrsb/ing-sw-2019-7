package it.polimi.ingsw.common.network;

import it.polimi.ingsw.common.models.Room;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

public interface API extends Remote {
    @Nullable String authUser(@NotNull String nickname, @NotNull String password) throws RemoteException;

    @Nullable String createUser(@NotNull String nickname, @NotNull String password) throws RemoteException;

    @Nullable UUID getActiveGame(@NotNull String token) throws RemoteException;

    @Nullable List<Room> getRooms(@NotNull String token) throws RemoteException;

    @Nullable Room joinRoom(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException;

    @Nullable Room createRoom(@NotNull String token, @NotNull String name) throws RemoteException;

    @Nullable UUID startGame(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException;

    boolean doMove(@NotNull String token, @NotNull Object move) throws RemoteException;

    void addGameListener(@NotNull String token, @NotNull GameListener gameListener) throws RemoteException;

    void addRoomListener(@NotNull String token, @NotNull RoomListener roomListener) throws RemoteException;

    void removeGameListener(@NotNull String token) throws RemoteException;

    void removeRoomListener(@NotNull String token) throws RemoteException;
}