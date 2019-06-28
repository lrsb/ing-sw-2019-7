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

    public static MongoDatabase mongoDatabase;
    public static ServerController controller;

    public static void main(String[] args) throws IOException {
        if (args != null) for (var arg : args) {
            switch (arg) {
                default:
                case "-h":
                case "--help":
                    System.out.println("Adrenaline board game server");
                    System.out.println("You have to specify all interfaces you want to use.\n");
                    System.out.println("Option        Meaning");
                    System.out.println("-w            Web HTTP/WS interface");
                    System.out.println("-r            RMI interface");
                    System.out.println("-s            SOCKET interface");
                    System.out.println();
                    System.out.println("ENV vars:");
                    System.out.println("Name          Meaning");
                    System.out.println("MONGODB_URI   URI of MongoDB (mandatory)");
                    System.out.println("MONGODB_NAME  Name of DB to use (mandatory)");
                    System.out.println("HTTP_PORT     Port where HTTP server have to listen (optional, default: 80)");
                    System.out.println("ROOM_TIMEOUT  Room timeout in seconds (optional, default: 30s)");
                    System.out.println("GAME_TIMEOUT  Game timeout in seconds (optional, default: 30s)");
                    return;
                case "-w":
                    new ServerRestImpl(Integer.parseInt(System.getenv().get("HTTP_PORT") == null ? "80" : System.getenv().get("HTTP_PORT")));
                    break;
                case "-r":
                    LocateRegistry.createRegistry(RMI_PORT).rebind(RMI_NAME, new ServerRmiImpl());
                    break;
                case "-s":
                    new AdrenalineServerSocket(new ServerSocketImpl());
                    break;
            }
        }
        else System.out.println("Use --help");
        try {
            mongoDatabase = new MongoClient(new MongoClientURI(System.getenv().get("MONGODB_URI"))).getDatabase(System.getenv().get("MONGODB_NAME"));
        } catch (Exception ignored) {
            System.out.println("Use --help");
            return;
        }
        controller = new ServerController();
    }
}