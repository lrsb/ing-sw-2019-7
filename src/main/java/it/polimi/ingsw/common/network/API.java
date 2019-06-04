package it.polimi.ingsw.common.network;

import it.polimi.ingsw.common.models.Action;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import org.jetbrains.annotations.NotNull;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

public interface API extends Remote {
    @NotNull String authUser(@NotNull String nickname, @NotNull String password) throws RemoteException;

    @NotNull String createUser(@NotNull String nickname, @NotNull String password) throws RemoteException;

    @NotNull Game getActiveGame(@NotNull String token) throws RemoteException;

    @NotNull List<Room> getRooms(@NotNull String token) throws RemoteException;

    @NotNull Room joinRoom(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException;


    @NotNull Room createRoom(@NotNull String token, @NotNull Room room) throws RemoteException;

    void quitRoom(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException;

    void startGame(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException;

    void quitGame(@NotNull String token, @NotNull UUID gameUuid) throws RemoteException;

    boolean doAction(@NotNull String token, @NotNull Action action) throws RemoteException;

    void addGameListener(@NotNull String token, @NotNull UUID gameUuid, @NotNull GameListener gameListener) throws RemoteException;

    void addRoomListener(@NotNull String token, @NotNull UUID roomUuid, @NotNull RoomListener roomListener) throws RemoteException;

    void removeGameListener(@NotNull String token, @NotNull UUID gameUuid) throws RemoteException;

    void removeRoomListener(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException;

}