package it.polimi.ingsw;

import it.polimi.ingsw.models.client.GameRmiImpl;
import it.polimi.ingsw.models.interfaces.IGame;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static Registry RMI_REGISTRY;

    static {
        try {
            RMI_REGISTRY = LocateRegistry.getRegistry("localhost", 10000);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //new NavigationController(MainViewController.class);
        rmi();
    }

    public static void rmi() {
        try {
            var registry = LocateRegistry.getRegistry("localhost", 10000);
            IGame obj = new GameRmiImpl(registry.lookup("games/uuid"));
            obj.addGameListener(System.out::println);
            System.out.println(obj.makeMove());
        } catch (Exception e) {
            System.out.println("HelloClient exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}