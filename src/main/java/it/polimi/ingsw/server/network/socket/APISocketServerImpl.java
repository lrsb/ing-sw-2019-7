package it.polimi.ingsw.server.network.socket;

import it.polimi.ingsw.common.network.socket.AdrenalinePacket;
import it.polimi.ingsw.common.network.socket.AdrenalineSocket;
import it.polimi.ingsw.common.network.socket.AdrenalineSocketListener;
import it.polimi.ingsw.server.controllers.SecureUserController;
import it.polimi.ingsw.server.controllers.ServerController;
import org.jetbrains.annotations.NotNull;

public class APISocketServerImpl implements AdrenalineServerSocketListener, AdrenalineSocketListener {
    @Override
    public void onNewPacket(@NotNull AdrenalineSocket socket, @NotNull AdrenalinePacket packet) {
        var user = SecureUserController.getUser(packet.getToken());
        if (user == null) return;
        try {
            var token = packet.getToken();
            switch (packet.getType()) {
                case ROOM_LIST:
                    socket.send(new AdrenalinePacket(AdrenalinePacket.Type.ROOM_LIST, null, ServerController.getRooms(token)));
                    break;
                case JOIN_ROOM:
                    socket.send(new AdrenalinePacket(AdrenalinePacket.Type.JOIN_ROOM, null, ServerController.joinRoom(token, packet.getAssociatedObject())));
                    break;
                case CREATE_ROOM:
                    socket.send(new AdrenalinePacket(AdrenalinePacket.Type.CREATE_ROOM, null, ServerController.createRoom(token, packet.getAssociatedObject())));
                    break;
                case JOIN_GAME:
                    socket.send(new AdrenalinePacket(AdrenalinePacket.Type.JOIN_GAME, null, ServerController.createRoom(token, packet.getAssociatedObject())));
                    break;
                case START_GAME:
                    socket.send(new AdrenalinePacket(AdrenalinePacket.Type.START_GAME, null, ServerController.startGame(token, packet.getAssociatedObject())));
                    break;
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