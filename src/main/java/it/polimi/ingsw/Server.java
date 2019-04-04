package it.polimi.ingsw;

import it.polimi.ingsw.server.network.rmi.APIRmiServerImpl;
import it.polimi.ingsw.server.network.socket.APISocketServerImpl;
import it.polimi.ingsw.server.network.socket.AdrenalineServerSocket;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Server {
    public static final int SOCKET_PORT = 0xCAFE;
    public static final int RMI_PORT = 0xBABE;
    public static final @NotNull String RMI_NAME = "adrenaline";

    public static void main(String[] args) throws RemoteException {
        LocateRegistry.createRegistry(RMI_PORT).rebind(RMI_NAME, new APIRmiServerImpl());
        try {
            new AdrenalineServerSocket(new APISocketServerImpl());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}