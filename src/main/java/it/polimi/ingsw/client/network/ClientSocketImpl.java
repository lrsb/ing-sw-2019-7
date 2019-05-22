package it.polimi.ingsw.client.network;

import com.google.gson.Gson;
import it.polimi.ingsw.common.models.Action;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.network.API;
import it.polimi.ingsw.common.network.GameListener;
import it.polimi.ingsw.common.network.RoomListener;
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

public class ClientSocketImpl implements API, AdrenalineSocketListener {
    private final @NotNull AdrenalineSocket adrenalineSocket;

    private volatile @Nullable String authUser;
    private volatile @Nullable String createUser;
    private volatile @Nullable Game activeGame;
    private volatile @Nullable List<Room> getRooms;
    private volatile @Nullable Room joinRoom;
    private volatile @Nullable Room createRoom;
    private volatile @Nullable Game startGame;
    private volatile @Nullable Boolean doMove;
    private @Nullable GameListener gameListener;
    private @Nullable RoomListener roomListener;

    @Contract(pure = true)
    public ClientSocketImpl(@NotNull String ip) throws IOException {
        this.adrenalineSocket = new AdrenalineSocket(ip, this);
    }

    @Override
    public @Nullable String authUser(@NotNull String nickname, @NotNull String password) {
        authUser = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.AUTH_USER, null, Arrays.asList(nickname, password)));
        while (authUser == null) wait1ms();
        return authUser;
    }

    @Override
    public @Nullable String createUser(@NotNull String nickname, @NotNull String password) {
        createUser = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.CREATE_USER, null, Arrays.asList(nickname, password)));
        while (createUser == null) wait1ms();
        return createUser;
    }

    @Override
    public @Nullable Game getActiveGame(@NotNull String token) {
        activeGame = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.GET_ACTIVE_GAME, null, null));
        while (activeGame == null) wait1ms();
        return activeGame;
    }

    @Override
    public @Nullable List<Room> getRooms(@NotNull String token) {
        getRooms = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.GET_ROOMS, token, null));
        while (getRooms == null) wait1ms();
        return getRooms;
    }

    @Override
    public @Nullable Room joinRoom(@NotNull String token, @NotNull UUID roomUuid) {
        joinRoom = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.JOIN_ROOM, token, roomUuid));
        while (joinRoom == null) wait1ms();
        return joinRoom;
    }

    @Override
    public @Nullable Room createRoom(@NotNull String token, @NotNull String name, int timeout, @NotNull Game.Type gameType) {
        createRoom = null;
        var gson = new Gson();
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.CREATE_ROOM, token,
                Arrays.asList(gson.toJson(name), gson.toJson(timeout), gson.toJson(gameType))));
        while (createRoom == null) wait1ms();
        return createRoom;
    }

    @Override
    public @Nullable Game startGame(@NotNull String token, @NotNull UUID roomUuid) {
        startGame = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.START_GAME, token, roomUuid));
        while (startGame == null) wait1ms();
        return startGame;
    }

    @Override
    public boolean doAction(@NotNull String token, @NotNull Action action) {
        doMove = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.DO_ACTION, token, action));
        while (doMove == null) wait1ms();
        return Optional.ofNullable(doMove).orElse(false);
    }

    @Override
    public void addGameListener(@NotNull String token, @NotNull GameListener gameListener) {
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.GAME_UPDATE, token, null));
        this.gameListener = gameListener;
    }

    @Override
    public void addRoomListener(@NotNull String token, @NotNull RoomListener roomListener) {
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.ROOM_UPDATE, token, null));
        this.roomListener = roomListener;
    }

    @Override
    public void removeGameListener(@NotNull String token) {
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.REMOVE_GAME_UPDATES, token, null));
        gameListener = null;
    }

    @Override
    public void removeRoomListener(@NotNull String token) {
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.REMOVE_ROOM_UPDATES, token, null));
        roomListener = null;
    }

    @Override
    public void onNewPacket(@NotNull AdrenalineSocket socket, @NotNull AdrenalinePacket packet) {
        try {
            if (packet.getType() != null) switch (packet.getType()) {
                case AUTH_USER:
                    authUser = packet.getAssociatedObject();
                    break;
                case CREATE_USER:
                    createUser = packet.getAssociatedObject();
                    break;
                case GET_ACTIVE_GAME:
                    activeGame = packet.getAssociatedObject();
                    break;
                case GET_ROOMS:
                    //TODO:test
                    getRooms = packet.getAssociatedObject();
                    break;
                case JOIN_ROOM:
                    joinRoom = packet.getAssociatedObject();
                    break;
                case CREATE_ROOM:
                    createRoom = packet.getAssociatedObject();
                    break;
                case START_GAME:
                    startGame = packet.getAssociatedObject();
                    break;
                case DO_ACTION:
                    doMove = packet.getAssociatedObject();
                    break;
                case GAME_UPDATE:
                    Game game = packet.getAssociatedObject();
                    if (game != null) Optional.ofNullable(gameListener).ifPresent(f -> {
                        try {
                            f.onGameUpdate(game);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    });
                    break;
                case ROOM_UPDATE:
                    Room room = packet.getAssociatedObject();
                    if (room != null) Optional.ofNullable(roomListener).ifPresent(f -> {
                        try {
                            f.onRoomUpdate(room);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(@NotNull AdrenalineSocket socket) {
    }

    private void wait1ms() {
        try {
            Thread.onSpinWait();
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}