package it.polimi.ingsw.common.network;

import it.polimi.ingsw.common.models.*;
import org.jetbrains.annotations.NotNull;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

public interface API extends Remote {
    @NotNull User.Auth authUser(@NotNull String nickname, @NotNull String password) throws RemoteException;

    @NotNull User.Auth createUser(@NotNull String nickname, @NotNull String password) throws RemoteException;

    @NotNull Game getActiveGame(@NotNull String token) throws RemoteException;

    @NotNull List<Room> getRooms(@NotNull String token) throws RemoteException;

    @NotNull Room joinRoom(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException;

    @NotNull Room createRoom(@NotNull String token, @NotNull Room room) throws RemoteException;

    void quitRoom(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException;

    void startGame(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException;

    void quitGame(@NotNull String token, @NotNull UUID gameUuid) throws RemoteException;

    boolean doAction(@NotNull String token, @NotNull Action action) throws RemoteException;

    void sendMessage(@NotNull String token, @NotNull Message message) throws RemoteException;

    void addListener(@NotNull String token, @NotNull Listener listener) throws RemoteException;

    void removeListener(@NotNull String token) throws RemoteException;
}