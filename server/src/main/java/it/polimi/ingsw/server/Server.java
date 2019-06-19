package it.polimi.ingsw.server;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import it.polimi.ingsw.server.controllers.ServerController;
import it.polimi.ingsw.server.network.AdrenalineServerSocket;
import it.polimi.ingsw.server.network.ServerRestImpl;
import it.polimi.ingsw.server.network.ServerRmiImpl;
import it.polimi.ingsw.server.network.ServerSocketImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;

public class Server {
    public static final int SOCKET_PORT = 0xCAFE;
    private static final int RMI_PORT = 0xBABE;
    private static final @NotNull String RMI_NAME = "adrenaline";

    public static final @NotNull MongoDatabase mongoDatabase = new MongoClient(new MongoClientURI(System.getenv().get("MONGODB_URI"))).getDatabase("heroku_wb845rtj");
    public static final @NotNull ServerController controller = new ServerController();

    public static void main(String[] args) throws IOException {
        if (args != null) for (var arg : args) {
            switch (arg) {
                case "-h":
                    new ServerRestImpl();
                    break;
                case "-r":
                    LocateRegistry.createRegistry(RMI_PORT).rebind(RMI_NAME, new ServerRmiImpl());
                    break;
                case "-s":
                    new AdrenalineServerSocket(new ServerSocketImpl());
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + arg);
            }
        }
    }
}