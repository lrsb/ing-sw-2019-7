package it.polimi.ingsw.server.network;

import it.polimi.ingsw.Server;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.network.GameListener;
import it.polimi.ingsw.common.network.RoomListener;
import it.polimi.ingsw.common.network.socket.AdrenalinePacket;
import it.polimi.ingsw.common.network.socket.AdrenalineSocket;
import it.polimi.ingsw.common.network.socket.AdrenalineSocketListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerSocketImpl implements AdrenalineServerSocketListener, AdrenalineSocketListener {

    @Override
    public void onNewPacket(@NotNull AdrenalineSocket socket, @NotNull AdrenalinePacket packet) {
        Logger.getLogger("socket").log(Level.INFO, "received: {0}", packet.getType());
        try {
            var token = packet.getToken();
            ArrayList<String> userInfo;
            switch (packet.getType()) {
                case AUTH_USER:
                    userInfo = packet.getAssociatedObject(ArrayList.class);
                    socket.send(new AdrenalinePacket(AdrenalinePacket.Type.AUTH_USER, null, Server.controller.authUser(userInfo.get(0), userInfo.get(1))));
                    break;
                case CREATE_USER:
                    userInfo = packet.getAssociatedObject(ArrayList.class);
                    socket.send(new AdrenalinePacket(AdrenalinePacket.Type.CREATE_USER, null, Server.controller.createUser(userInfo.get(0), userInfo.get(1))));
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
                case DO_MOVE:
                    Server.controller.doMove(token, packet.getAssociatedObject(Game.class));
                    break;
                case GAME_UPDATE:
                    Server.controller.addGameListener(token, new GameListener() {
                        @Override
                        public void onGameUpdate(Game game) {
                            socket.send(new AdrenalinePacket(AdrenalinePacket.Type.GAME_UPDATE, null, game));
                        }

                        @Override
                        public void disconnected() {
                        }
                    });
                    break;
                case ROOM_UPDATE:
                    Server.controller.addRoomListener(token, new RoomListener() {
                        @Override
                        public void onRoomUpdate(@NotNull Room room) {
                            socket.send(new AdrenalinePacket(AdrenalinePacket.Type.ROOM_UPDATE, null, room));
                        }

                        @Override
                        public void disconnected() {
                        }
                    });
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
        Logger.getLogger("socket").log(Level.INFO, "s_close: {0}", socket.getInetAddress());
    }

    @Override
    public void onNewSocket(@NotNull AdrenalineSocket socket) {
        Logger.getLogger("socket").log(Level.INFO, "s_open: {0}", socket.getInetAddress());
        socket.setAdrenalineSocketListener(this);
    }
}