package it.polimi.ingsw;

import it.polimi.ingsw.server.controllers.ServerController;
import it.polimi.ingsw.server.network.AdrenalineServerSocket;
import it.polimi.ingsw.server.network.ServerRmiImpl;
import it.polimi.ingsw.server.network.ServerSocketImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Server {
    public static final int SOCKET_PORT = 0xCAFE;
    public static final int RMI_PORT = 0xBABE;

    public static final @NotNull String RMI_NAME = "adrenaline";
    public static final @NotNull ServerController controller = new ServerController();

    public static void main(String[] args) throws RemoteException {
        LocateRegistry.createRegistry(RMI_PORT).rebind(RMI_NAME, new ServerRmiImpl());
        try {
            new AdrenalineServerSocket(new ServerSocketImpl());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}