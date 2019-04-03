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
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.ROOM_LIST, null));
        while (rooms == null) wait1ms();
        var rooms1 = rooms;
        rooms = null;
        return rooms1;
    }

    //TODO: impl
    @Override
    public boolean joinRoom(User user, @NotNull Room room) {
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.JOIN_ROOM, Arrays.asList(user, room.getUuid())));
        while (joined == null) wait1ms();
        var joined1 = joined;
        joined = null;
        //noinspection ConstantConditions
        return joined1;
    }

    //TODO: impl
    @Override
    public @NotNull Room createRoom(@NotNull String name) {
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.CREATE_ROOM, name));
        while (room == null) wait1ms();
        var room1 = room;
        room = null;
        return room1;
    }

    //TODO: impl
    @Override
    public void onNewObject(@NotNull AdrenalinePacket object) {
        switch (object.getType()) {
            case ROOM_LIST:
                //noinspection unchecked
                rooms = (ArrayList<Room>) object.getAssociatedObject(ArrayList.class);
                break;
            case JOIN_ROOM:
                joined = object.getAssociatedObject(boolean.class);
                break;
            case CREATE_ROOM:
                room = object.getAssociatedObject(Room.class);
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
