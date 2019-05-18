package it.polimi.ingsw.common.network;

import it.polimi.ingsw.common.models.Game;
import org.jetbrains.annotations.NotNull;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The interface Game listener.
 */
public interface GameListener extends Remote {
    /**
     * On game update.
     *
     * @param game the game
     * @throws RemoteException the remote exception
     */
    void onGameUpdate(@NotNull Game game) throws RemoteException;
}