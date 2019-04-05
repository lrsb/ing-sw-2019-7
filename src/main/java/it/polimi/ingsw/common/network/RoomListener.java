package it.polimi.ingsw.common.network;

import it.polimi.ingsw.common.models.Room;
import org.jetbrains.annotations.NotNull;

public interface RoomListener {
    void onRoomUpdated(@NotNull Room room);
}