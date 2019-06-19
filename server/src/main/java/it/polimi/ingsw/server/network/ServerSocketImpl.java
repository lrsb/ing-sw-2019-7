package it.polimi.ingsw.server.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.common.models.Action;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.network.exceptions.UserRemoteException;
import it.polimi.ingsw.common.network.socket.AdrenalinePacket;
import it.polimi.ingsw.common.network.socket.AdrenalineSocket;
import it.polimi.ingsw.common.network.socket.AdrenalineSocketListener;
import it.polimi.ingsw.server.Server;
import org.jetbrains.annotations.NotNull;

import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerSocketImpl implements AdrenalineServerSocketListener, AdrenalineSocketListener {
    private static final @NotNull Logger logger = Logger.getLogger("ServerSocketImpl");

    @Override
    public void onNewPacket(@NotNull AdrenalineSocket socket, @NotNull AdrenalinePacket packet) {
        try {
            var token = packet.getToken();
            List<String> userInfo;
            if (packet.getType() != null) switch (packet.getType()) {
                case AUTH_USER:
                    userInfo = packet.getAssociatedObject(new TypeToken<List<String>>() {
                    });
                    if (userInfo != null)
                        socket.send(new AdrenalinePacket(AdrenalinePacket.Type.AUTH_USER, null, Server.controller.authUser(userInfo.get(0), userInfo.get(1))));
                    break;
                case CREATE_USER:
                    userInfo = packet.getAssociatedObject(new TypeToken<List<String>>() {
                    });
                    if (userInfo != null)
                        socket.send(new AdrenalinePacket(AdrenalinePacket.Type.CREATE_USER, null, Server.controller.createUser(userInfo.get(0), userInfo.get(1))));
                    break;
                case GET_ACTIVE_GAME:
                    socket.send(new AdrenalinePacket(AdrenalinePacket.Type.GET_ACTIVE_GAME, null, Server.controller.getActiveGame(token)));
                    break;
                case GET_ROOMS:
                    socket.send(new AdrenalinePacket(AdrenalinePacket.Type.GET_ROOMS, null, Server.controller.getRooms(token)));
                    break;
                case JOIN_ROOM:
                    socket.send(new AdrenalinePacket(AdrenalinePacket.Type.JOIN_ROOM, null, Server.controller.joinRoom(token, packet.getAssociatedObject(UUID.class))));
                    break;
                case CREATE_ROOM:
                    socket.send(new AdrenalinePacket(AdrenalinePacket.Type.CREATE_ROOM, null, Server.controller.createRoom(token, packet.getAssociatedObject(Room.class))));
                    break;
                case QUIT_ROOM:
                    Server.controller.quitRoom(token, packet.getAssociatedObject(UUID.class));
                    socket.send(new AdrenalinePacket(AdrenalinePacket.Type.QUIT_ROOM, null, null));
                    break;
                case START_GAME:
                    Server.controller.startGame(token, packet.getAssociatedObject(UUID.class));
                    socket.send(new AdrenalinePacket(AdrenalinePacket.Type.START_GAME, null, null));
                    break;
                case QUIT_GAME:
                    Server.controller.quitGame(token, packet.getAssociatedObject(UUID.class));
                    socket.send(new AdrenalinePacket(AdrenalinePacket.Type.QUIT_GAME, null, null));
                    break;
                case DO_ACTION:
                    Server.controller.doAction(token, packet.getAssociatedObject(Action.class));
                    break;
                case GAME_UPDATE:
                    Server.controller.addGameListener(token, packet.getAssociatedObject(UUID.class), (e, message) ->
                            socket.send(new AdrenalinePacket(AdrenalinePacket.Type.GAME_UPDATE, null, List.of(new Gson().toJson(e), new Gson().toJson(message)))));
                    break;
                case ROOM_UPDATE:
                    Server.controller.addRoomListener(token, packet.getAssociatedObject(UUID.class), e -> socket.send(new AdrenalinePacket(AdrenalinePacket.Type.ROOM_UPDATE, null, e)));
                    break;
                case REMOVE_GAME_UPDATES:
                    Server.controller.removeGameListener(token, packet.getAssociatedObject(UUID.class));
                    break;
                case REMOVE_ROOM_UPDATES:
                    Server.controller.removeRoomListener(token, packet.getAssociatedObject(UUID.class));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + packet.getType());
            }
        } catch (UserRemoteException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            socket.send(new AdrenalinePacket(AdrenalinePacket.Type.USER_REMOTE_EXCEPTION, null, e));
        } catch (RemoteException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            socket.send(new AdrenalinePacket(AdrenalinePacket.Type.REMOTE_EXCEPTION, null, e));
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            socket.send(new AdrenalinePacket(AdrenalinePacket.Type.REMOTE_EXCEPTION, null, new RemoteException(e.getMessage())));
        }
    }

    @Override
    public void onClose(@NotNull AdrenalineSocket socket) {
        logger.log(Level.INFO, "s_close: {0}", socket.getInetAddress());
    }

    @Override
    public void onNewSocket(@NotNull AdrenalineSocket socket) {
        logger.log(Level.INFO, "s_open: {0}", socket.getInetAddress());
        socket.setAdrenalineSocketListener(this);
    }
}