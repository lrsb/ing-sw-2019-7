package it.polimi.ingsw.client.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.common.models.Action;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.network.API;
import it.polimi.ingsw.common.network.GameListener;
import it.polimi.ingsw.common.network.RoomListener;
import it.polimi.ingsw.common.network.exceptions.UserRemoteException;
import it.polimi.ingsw.common.network.socket.AdrenalinePacket;
import it.polimi.ingsw.common.network.socket.AdrenalineSocket;
import it.polimi.ingsw.common.network.socket.AdrenalineSocketListener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("ConstantConditions")
public class ClientSocketImpl implements API, AdrenalineSocketListener {
    private final @NotNull AdrenalineSocket adrenalineSocket;

    private volatile @Nullable String authUser;
    private volatile @Nullable String createUser;
    private volatile @Nullable Game activeGame;
    private volatile @Nullable List<Room> getRooms;
    private volatile @Nullable Room joinRoom;
    private volatile @Nullable Room createRoom;
    private volatile @Nullable Game startGame;
    private volatile @Nullable Boolean doAction;
    private volatile boolean gameUpdateRemoved;
    private volatile boolean roomUpdateRemoved;
    private volatile @Nullable RemoteException remoteException;

    private @Nullable GameListener gameListener;
    private @Nullable RoomListener roomListener;

    @Contract(pure = true)
    public ClientSocketImpl(@NotNull String ip) throws IOException {
        this.adrenalineSocket = new AdrenalineSocket(ip, this);
    }

    @Override
    public @NotNull String authUser(@NotNull String nickname, @NotNull String password) throws RemoteException {
        authUser = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.AUTH_USER, null, Arrays.asList(nickname, password)));
        while (authUser == null) wait1ms();
        return authUser;
    }

    @Override
    public @NotNull String createUser(@NotNull String nickname, @NotNull String password) throws RemoteException {
        createUser = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.CREATE_USER, null, Arrays.asList(nickname, password)));
        while (createUser == null) wait1ms();
        return createUser;
    }

    @Override
    public @NotNull Game getActiveGame(@NotNull String token) throws RemoteException {
        activeGame = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.GET_ACTIVE_GAME, null, null));
        while (activeGame == null) wait1ms();
        return activeGame;
    }

    @Override
    public @NotNull List<Room> getRooms(@NotNull String token) throws RemoteException {
        getRooms = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.GET_ROOMS, token, null));
        while (getRooms == null) wait1ms();
        return getRooms;
    }

    @Override
    public @NotNull Room joinRoom(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException {
        joinRoom = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.JOIN_ROOM, token, roomUuid));
        while (joinRoom == null) wait1ms();
        return joinRoom;
    }

    @Override
    public @NotNull Room createRoom(@NotNull String token, @NotNull String name, int timeout, @NotNull Game.Type gameType) throws RemoteException {
        createRoom = null;
        var gson = new Gson();
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.CREATE_ROOM, token,
                Arrays.asList(gson.toJson(name), gson.toJson(timeout), gson.toJson(gameType))));
        while (createRoom == null) wait1ms();
        return createRoom;
    }

    @Override
    public void quitRoom(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException {
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.JOIN_ROOM, token, roomUuid));//TODO
    }

    @Override
    public @NotNull Game startGame(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException {
        startGame = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.START_GAME, token, roomUuid));
        while (startGame == null) wait1ms();
        return startGame;
    }

    @Override
    public boolean doAction(@NotNull String token, @NotNull Action action) throws RemoteException {
        doAction = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.DO_ACTION, token, action));
        while (doAction == null) wait1ms();
        return Optional.ofNullable(doAction).orElse(false);
    }

    @Override
    public void addGameListener(@NotNull String token, @NotNull UUID gameUuid, @NotNull GameListener gameListener) throws RemoteException {
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.GAME_UPDATE, token, gameUuid));
        wait1ms();
        this.gameListener = gameListener;
    }

    @Override
    public void addRoomListener(@NotNull String token, @NotNull UUID roomUuid, @NotNull RoomListener roomListener) throws RemoteException {
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.ROOM_UPDATE, token, roomUuid));
        wait1ms();
        this.roomListener = roomListener;
    }

    @Override
    public void removeGameListener(@NotNull String token, @NotNull UUID gameUuid) throws RemoteException {
        gameUpdateRemoved = false;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.REMOVE_GAME_UPDATES, token, gameUuid));
        if (!gameUpdateRemoved) wait1ms();
        gameListener = null;
    }

    @Override
    public void removeRoomListener(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException {
        roomUpdateRemoved = false;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.REMOVE_ROOM_UPDATES, token, roomUuid));
        if (!roomUpdateRemoved) wait1ms();
        roomListener = null;
    }

    @Override
    public void onNewPacket(@NotNull AdrenalineSocket socket, @NotNull AdrenalinePacket packet) {
        try {
            if (packet.getType() != null) switch (packet.getType()) {
                case AUTH_USER:
                    authUser = packet.getAssociatedObject(String.class);
                    break;
                case CREATE_USER:
                    createUser = packet.getAssociatedObject(String.class);
                    break;
                case GET_ACTIVE_GAME:
                    activeGame = packet.getAssociatedObject(Game.class);
                    break;
                case GET_ROOMS:
                    getRooms = packet.getAssociatedObject(new TypeToken<List<Room>>() {
                    });
                    break;
                case JOIN_ROOM:
                    joinRoom = packet.getAssociatedObject(Room.class);
                    break;
                case CREATE_ROOM:
                    createRoom = packet.getAssociatedObject(Room.class);
                    break;
                case START_GAME:
                    startGame = packet.getAssociatedObject(Game.class);
                    break;
                case DO_ACTION:
                    doAction = packet.getAssociatedObject(boolean.class);
                    break;
                case GAME_UPDATE:
                    Game game = packet.getAssociatedObject(Game.class);
                    if (game != null) Optional.ofNullable(gameListener).ifPresent(f -> {
                        try {
                            f.onGameUpdate(game);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    });
                    break;
                case ROOM_UPDATE:
                    Room room = packet.getAssociatedObject(Room.class);
                    if (room != null) Optional.ofNullable(roomListener).ifPresent(f -> {
                        try {
                            f.onRoomUpdate(room);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    });
                case REMOVE_GAME_UPDATES:
                    gameUpdateRemoved = true;
                    break;
                case REMOVE_ROOM_UPDATES:
                    roomUpdateRemoved = true;
                    break;
                case USER_REMOTE_EXCEPTION:
                    remoteException = packet.getAssociatedObject(UserRemoteException.class);
                    break;
                case REMOTE_EXCEPTION:
                    remoteException = packet.getAssociatedObject(RemoteException.class);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            remoteException = new RemoteException(e.getMessage());
        }
    }

    @Override
    public void onClose(@NotNull AdrenalineSocket socket) {
    }

    private void wait1ms() throws RemoteException {
        if (remoteException != null) try {
            throw remoteException;
        } catch (@SuppressWarnings("CaughtExceptionImmediatelyRethrown") RemoteException e) {
            throw e;
        } finally {
            remoteException = null;
        }
        else try {
            Thread.onSpinWait();
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}