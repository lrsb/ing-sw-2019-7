package it.polimi.ingsw.models.server;

import it.polimi.ingsw.models.interfaces.IHandyManny;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public abstract class HandyManny extends UnicastRemoteObject implements IHandyManny {
    public static final String RMI_NAME = "HandyManny";

    protected HandyManny() throws RemoteException {
        super();
    }
}