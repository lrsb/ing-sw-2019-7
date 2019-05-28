package it.polimi.ingsw.common.network.exceptions;

import java.rmi.RemoteException;

public class UserRemoteException extends RemoteException {
    public UserRemoteException(String s) {
        super(s);
    }
}