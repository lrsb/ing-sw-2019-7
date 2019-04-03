package it.polimi.ingsw.models.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRmiGame extends Remote {
    String makeMove() throws RemoteException;

    String waitBoardUpdate() throws RemoteException;
}
