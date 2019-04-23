package it.polimi.ingsw.common.network;

import it.polimi.ingsw.common.models.Game;
import org.jetbrains.annotations.NotNull;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameListener extends Remote {
    void onGameUpdate(@NotNull Game game) throws RemoteException;
}