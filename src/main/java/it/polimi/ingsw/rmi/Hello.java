package it.polimi.ingsw.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Hello extends Remote {
    String hello() throws RemoteException;
}
