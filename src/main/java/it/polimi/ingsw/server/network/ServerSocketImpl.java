package it.polimi.ingsw.server.network;

import it.polimi.ingsw.Server;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.network.socket.AdrenalinePacket;
import it.polimi.ingsw.common.network.socket.AdrenalineSocket;
import it.polimi.ingsw.common.network.socket.AdrenalineSocketListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerSocketImpl implements AdrenalineServerSocketListener, AdrenalineSocketListener {
    private static final @NotNull ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public void onNewPacket(@NotNull AdrenalineSocket socket, @NotNull AdrenalinePacket packet) {
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
                    executorService.submit(() -> socket.send(new AdrenalinePacket(AdrenalinePacket.Type.GAME_UPDATE, null, Server.controller.waitGameUpdate(packet.getToken(), packet.getAssociatedObject(UUID.class)))));
                    break;
                case ROOM_UPDATE:
                    executorService.submit(() -> socket.send(new AdrenalinePacket(AdrenalinePacket.Type.ROOM_UPDATE, null, Server.controller.waitRoomUpdate(packet.getToken(), packet.getAssociatedObject(UUID.class)))));
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
        System.out.println("close: " + socket.getInetAddress().toString());
    }

    @Override
    public void onNewSocket(@NotNull AdrenalineSocket socket) {
        System.out.println("open: " + socket.getInetAddress().toString());
        socket.setAdrenalineSocketListener(this);
    }
}