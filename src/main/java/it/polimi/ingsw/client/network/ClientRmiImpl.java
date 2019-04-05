package it.polimi.ingsw.client.network;

import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.network.API;
import it.polimi.ingsw.common.network.GameListener;
import it.polimi.ingsw.common.network.RoomListener;
import it.polimi.ingsw.common.network.rmi.RmiAPI;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientRmiImpl implements API {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private @NotNull RmiAPI rmiAPI;
    private volatile @Nullable GameListener gameListener;
    private volatile @Nullable RoomListener roomListener;

    @Contract(pure = true)
    public ClientRmiImpl(Remote netComm) {
        this.rmiAPI = (RmiAPI) netComm;
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
    public void doMove(@NotNull String token, @NotNull Object move) throws RemoteException {
        rmiAPI.doMove(token, move);
    }

    @Override
    public void addGameListener(@NotNull String token, @NotNull UUID gameUuid, @NotNull GameListener gameListener) {
        this.gameListener = gameListener;
        executorService.submit(() -> {
            while (this.gameListener != null) try {
                var update = rmiAPI.waitGameUpdate(token, gameUuid);
                if (update != null) Optional.ofNullable(this.gameListener).ifPresent(e -> e.onGameUpdated(update));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void addRoomListener(@NotNull String token, @NotNull UUID roomUuid, @NotNull RoomListener roomListener) {
        this.roomListener = roomListener;
        executorService.submit(() -> {
            while (this.roomListener != null) try {
                var update = rmiAPI.waitRoomUpdate(token, roomUuid);
                if (update != null) Optional.ofNullable(this.roomListener).ifPresent(e -> e.onRoomUpdated(update));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void removeGameListener(@NotNull String token, @NotNull UUID gameUuid) {
        this.gameListener = null;
    }

    @Override
    public void removeRoomListener(@NotNull String token, @NotNull UUID roomUuid) {
        this.roomListener = null;
    }
}
