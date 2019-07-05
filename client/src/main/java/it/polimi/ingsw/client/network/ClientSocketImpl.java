package it.polimi.ingsw.client.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.common.models.*;
import it.polimi.ingsw.common.network.API;
import it.polimi.ingsw.common.network.Listener;
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

    private volatile @Nullable User.Auth authUser;
    private volatile @Nullable User.Auth createUser;
    private volatile @Nullable Game activeGame;
    private volatile @Nullable List<Room> getRooms;
    private volatile @Nullable Room joinRoom;
    private volatile @Nullable Room createRoom;
    private volatile boolean quitRoom;
    private volatile boolean startGame;
    private volatile boolean quitGame;
    private volatile @Nullable Boolean doAction;
    private volatile boolean sendMessage;
    private volatile boolean updateRemoved;
    private volatile @Nullable RemoteException remoteException;

    private @Nullable Listener listener;

    @Contract(pure = true)
    public ClientSocketImpl(@NotNull String ip) throws IOException {
        this.adrenalineSocket = new AdrenalineSocket(ip, Client.SOCKET_PORT, this);
    }

    @Override
    public @NotNull User.Auth authUser(@NotNull String nickname, @NotNull String password) throws RemoteException {
        authUser = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.AUTH_USER, null, Arrays.asList(nickname, password)));
        while (authUser == null) wait1ms();
        return authUser;
    }

    @Override
    public @NotNull User.Auth createUser(@NotNull String nickname, @NotNull String password) throws RemoteException {
        createUser = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.CREATE_USER, null, Arrays.asList(nickname, password)));
        while (createUser == null) wait1ms();
        return createUser;
    }

    @Override
    public @NotNull Game getActiveGame(@NotNull String token) throws RemoteException {
        activeGame = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.GET_ACTIVE_GAME, token, null));
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
    public @NotNull Room createRoom(@NotNull String token, @NotNull Room room) throws RemoteException {
        createRoom = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.CREATE_ROOM, token, room));
        while (createRoom == null) wait1ms();
        return createRoom;
    }

    @Override
    public void quitRoom(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException {
        quitRoom = false;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.QUIT_ROOM, token, roomUuid));
        while (!quitRoom) wait1ms();
    }

    @Override
    public void startGame(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException {
        startGame = false;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.START_GAME, token, roomUuid));
        while (!startGame) wait1ms();
    }

    @Override
    public void quitGame(@NotNull String token, @NotNull UUID gameUuid) throws RemoteException {
        quitGame = false;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.QUIT_GAME, token, gameUuid));
        while (!quitGame) wait1ms();
    }

    @Override
    public boolean doAction(@NotNull String token, @NotNull Action action) throws RemoteException {
        doAction = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.DO_ACTION, token, action));
        while (doAction == null) wait1ms();
        return Optional.ofNullable(doAction).orElse(false);
    }

    @Override
    public void sendMessage(@NotNull String token, @NotNull Message message) throws RemoteException {
        sendMessage = false;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.SEND_MESSAGE, token, message));
        while (!sendMessage) wait1ms();
    }

    @Override
    public void addListener(@NotNull String token, @NotNull Listener listener) throws RemoteException {
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.UPDATE, token, null));
        wait1ms();
        this.listener = listener;
    }

    @Override
    public void removeListener(@NotNull String token) throws RemoteException {
        updateRemoved = false;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.REMOVE_UPDATE, token, null));
        if (!updateRemoved) wait1ms();
        listener = null;
    }

    @Override
    public void onNewPacket(@NotNull AdrenalineSocket socket, @NotNull AdrenalinePacket packet) {
        try {
            if (packet.getType() != null) switch (packet.getType()) {
                case AUTH_USER:
                    authUser = packet.getAssociatedObject(User.Auth.class);
                    break;
                case CREATE_USER:
                    createUser = packet.getAssociatedObject(User.Auth.class);
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
                case QUIT_ROOM:
                    quitRoom = true;
                    break;
                case START_GAME:
                    startGame = true;
                    break;
                case QUIT_GAME:
                    quitGame = true;
                    break;
                case DO_ACTION:
                    doAction = packet.getAssociatedObject(boolean.class);
                    break;
                case SEND_MESSAGE:
                    sendMessage = true;
                    break;
                case UPDATE:
                    List<String> data = packet.getAssociatedObject(new TypeToken<List<String>>() {
                    });
                    if (data != null) Optional.ofNullable(listener).ifPresent(f -> {
                        new Thread(() -> {
                            try {
                                f.onUpdate(new Gson().fromJson(data.get(1), TypeToken.get(Class.forName(data.get(0))).getType()));
                            } catch (RemoteException | ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    });
                    break;
                case REMOVE_UPDATE:
                    updateRemoved = true;
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