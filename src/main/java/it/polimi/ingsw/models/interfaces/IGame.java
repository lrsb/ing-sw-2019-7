package it.polimi.ingsw.models.interfaces;

import java.rmi.RemoteException;

public interface IGame {
    String makeMove() throws RemoteException;

    void addGameListener(GameListener gameListener);
}