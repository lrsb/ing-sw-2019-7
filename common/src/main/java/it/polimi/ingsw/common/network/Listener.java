package it.polimi.ingsw.common.network;

import org.jetbrains.annotations.NotNull;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Listener extends Remote {
    /**
     * @param object {@link it.polimi.ingsw.common.models.Game} {@link it.polimi.ingsw.common.models.Room} {@link String}
     * @throws RemoteException
     */
    void onUpdate(@NotNull Object object) throws RemoteException;
}