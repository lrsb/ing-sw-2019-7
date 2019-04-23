package it.polimi.ingsw.common.network;

import it.polimi.ingsw.common.models.Game;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameListener extends Remote {
    void onGameUpdate(Game game) throws RemoteException;
}