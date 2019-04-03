package it.polimi.ingsw.models.interfaces;

import it.polimi.ingsw.models.common.Room;
import it.polimi.ingsw.models.common.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IHandyManny {
    @NotNull List<Room> getRooms();

    boolean joinRoom(User user, @NotNull Room room);

    @NotNull Room createRoom(@NotNull String name);
}
