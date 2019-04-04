package it.polimi.ingsw.client.network.socket;

import it.polimi.ingsw.client.network.API;
import it.polimi.ingsw.client.network.GameListener;
import it.polimi.ingsw.client.network.RoomListener;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.network.socket.AdrenalinePacket;
import it.polimi.ingsw.common.network.socket.AdrenalineSocket;
import it.polimi.ingsw.common.network.socket.AdrenalineSocketListener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class APISocketClientImpl implements API, AdrenalineSocketListener {
    private @NotNull AdrenalineSocket adrenalineSocket;
    private @Nullable GameListener gameListener;
    private @Nullable RoomListener roomListener;

    private volatile @Nullable List<Room> getRooms;
    private volatile @Nullable Room joinRoom;
    private volatile @Nullable Room createRoom;
    private volatile @Nullable UUID startGame;


    @Contract(pure = true)
    public APISocketClientImpl(@NotNull String ip) throws IOException {
        this.adrenalineSocket = new AdrenalineSocket(ip, this);
    }

    @Override
    public @Nullable List<Room> getRooms(@NotNull String token) {
        getRooms = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.ROOM_LIST, token, null));
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
    public @Nullable Room createRoom(@NotNull String token, @NotNull String name) {
        createRoom = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.JOIN_ROOM, token, name));
        while (createRoom == null) wait1ms();
        return createRoom;
    }

    @Override
    public @Nullable UUID startGame(@NotNull String token, @NotNull UUID roomUuid) {
        startGame = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.START_GAME, token, roomUuid));
        while (startGame == null) wait1ms();
        return startGame;
    }

    //TODO: impl
    @Override
    public void doMove(@NotNull String token, @NotNull Object move) {

    }

    @Override
    public void addGameListener(@NotNull String token, @NotNull UUID gameUuid, @NotNull GameListener gameListener) {
        this.gameListener = gameListener;
    }

    @Override
    public void addRoomListener(@NotNull String token, @NotNull UUID roomUuid, @NotNull RoomListener roomListener) {
        this.roomListener = roomListener;
    }

    @Override
    public void removeGameListener() {
        gameListener = null;
    }

    @Override
    public void removeRoomListener() {
        roomListener = null;
    }

    @Override
    public void onNewPacket(@NotNull AdrenalineSocket socket, @NotNull AdrenalinePacket packet) {
        switch (packet.getType()) {
            case ROOM_LIST:
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
            case JOIN_GAME:
                break;
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