package it.polimi.ingsw.common.network;

import it.polimi.ingsw.common.models.Room;
import org.jetbrains.annotations.NotNull;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RoomListener extends Remote {
    void onRoomUpdate(@NotNull Room room) throws RemoteException;
}