package it.polimi.ingsw.models.client;

import it.polimi.ingsw.models.common.Room;
import it.polimi.ingsw.models.common.User;
import it.polimi.ingsw.models.interfaces.IHandyManny;
import it.polimi.ingsw.socket.AdrenalinePacket;
import it.polimi.ingsw.socket.AdrenalineSocket;
import it.polimi.ingsw.socket.AdrenalineSocketListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HandyMannySocketImpl implements IHandyManny, AdrenalineSocketListener {
    private AdrenalineSocket adrenalineSocket;
    private volatile @Nullable ArrayList<Room> rooms;
    private volatile @Nullable Boolean joined;
    private volatile @Nullable Room room;

    public HandyMannySocketImpl(String ip) throws IOException {
        adrenalineSocket = new AdrenalineSocket(ip, this);
    }

    //TODO: impl
    @Override
    public @NotNull List<Room> getRooms() {
        rooms = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.ROOM_LIST, null));
        while (rooms == null) wait1ms();
        return rooms;
    }

    //TODO: impl
    @Override
    public boolean joinRoom(@NotNull User user, @NotNull Room room) {
        joined = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.JOIN_ROOM, Arrays.asList(user, room.getUuid())));
        while (joined == null) wait1ms();
        //noinspection ConstantConditions
        return joined;
    }

    //TODO: impl
    @Override
    public @NotNull Room createRoom(@NotNull String name) {
        room = null;
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.CREATE_ROOM, name));
        while (room == null) wait1ms();
        //noinspection ConstantConditions
        return room;
    }

    //TODO: impl
    @Override
    public void onNewPacket(@NotNull AdrenalinePacket packet) {
        switch (packet.getType()) {
            case ROOM_LIST:
                //noinspection unchecked
                rooms = (ArrayList<Room>) packet.getAssociatedObject(ArrayList.class);
                break;
            case JOIN_ROOM:
                joined = packet.getAssociatedObject(boolean.class);
                break;
            case CREATE_ROOM:
                room = packet.getAssociatedObject(Room.class);
                break;
        }
    }

    //TODO: impl
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
