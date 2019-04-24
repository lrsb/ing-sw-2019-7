package it.polimi.ingsw.server.network;

import it.polimi.ingsw.Server;
import it.polimi.ingsw.common.models.Action;
import it.polimi.ingsw.common.network.socket.AdrenalinePacket;
import it.polimi.ingsw.common.network.socket.AdrenalineSocket;
import it.polimi.ingsw.common.network.socket.AdrenalineSocketListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerSocketImpl implements AdrenalineServerSocketListener, AdrenalineSocketListener {
    private static final @NotNull Logger logger = Logger.getLogger("ServerSocketImpl");

    @Override
    public void onNewPacket(@NotNull AdrenalineSocket socket, @NotNull AdrenalinePacket packet) {
        try {
            var token = packet.getToken();
            ArrayList<String> userInfo;
            switch (packet.getType()) {
                case AUTH_USER:
                    //noinspection unchecked
                    userInfo = packet.getAssociatedObject(ArrayList.class);
                    //noinspection ConstantConditions
                    socket.send(new AdrenalinePacket(AdrenalinePacket.Type.AUTH_USER, null, Server.controller.authUser(userInfo.get(0), userInfo.get(1))));
                    break;
                case CREATE_USER:
                    //noinspection unchecked
                    userInfo = packet.getAssociatedObject(ArrayList.class);
                    //noinspection ConstantConditions
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
                    socket.send(new AdrenalinePacket(AdrenalinePacket.Type.CREATE_ROOM, null, Server.controller.createRoom(token, packet.getAssociatedObject(String.class))));
                    break;
                case START_GAME:
                    socket.send(new AdrenalinePacket(AdrenalinePacket.Type.START_GAME, null, Server.controller.startGame(token, packet.getAssociatedObject(UUID.class))));
                    break;
                case DO_ACTION:
                    Server.controller.doAction(token, packet.getAssociatedObject(Action.class));
                    break;
                case GAME_UPDATE:
                    Server.controller.addGameListener(token, game -> socket.send(new AdrenalinePacket(AdrenalinePacket.Type.GAME_UPDATE, null, game)));
                    break;
                case ROOM_UPDATE:
                    Server.controller.addRoomListener(token, room -> socket.send(new AdrenalinePacket(AdrenalinePacket.Type.ROOM_UPDATE, null, room)));
                    break;
                case REMOVE_GAME_UPDATES:
                    Server.controller.removeGameListener(token);
                    break;
                case REMOVE_ROOM_UPDATES:
                    Server.controller.removeRoomListener(token);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + packet.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
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