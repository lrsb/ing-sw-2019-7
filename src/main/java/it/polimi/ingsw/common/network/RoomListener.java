package it.polimi.ingsw.common.network;

import it.polimi.ingsw.common.models.Room;
import org.jetbrains.annotations.NotNull;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The interface Room listener.
 */
public interface RoomListener extends Remote {
    /**
     * On room update.
     *
     * @param room the room
     * @throws RemoteException the remote exception
     */
    void onRoomUpdate(@NotNull Room room) throws RemoteException;
}