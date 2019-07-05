package it.polimi.ingsw.client.network;

import it.polimi.ingsw.common.models.*;
import it.polimi.ingsw.common.network.API;
import it.polimi.ingsw.common.network.Listener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientRmiImpl implements API {
    private static final @NotNull ExecutorService executorService = Executors.newCachedThreadPool();

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
    public void sendMessage(@NotNull String token, @NotNull Message message) throws RemoteException {
        remote.sendMessage(token, message);
    }

    @Override
    public void addListener(@NotNull String token, @NotNull Listener listener) throws RemoteException {
        remote.addListener(token, (Listener) UnicastRemoteObject.exportObject((Listener) o -> executorService.submit(() -> {
            try {
                listener.onUpdate(o);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }), 0));
    }

    @Override
    public void removeListener(@NotNull String token) throws RemoteException {
        remote.removeListener(token);
    }
}