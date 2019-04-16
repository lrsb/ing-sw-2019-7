package it.polimi.ingsw;

import it.polimi.ingsw.client.controllers.MainViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.network.ClientRestImpl;
import it.polimi.ingsw.client.network.ClientRmiImpl;
import it.polimi.ingsw.client.network.ClientSocketImpl;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.security.SecureRandom;

public class Client {
    //WARN: per provare il server fare commit e usare ClientRestImpl
    public static void main(String[] args) throws IOException, NotBoundException {
        new NavigationController(MainViewController.class);
        var local = "localhost";
        var comm = new SecureRandom().nextBoolean() ? new ClientSocketImpl(local) : new ClientRmiImpl(LocateRegistry.getRegistry(local, Server.RMI_PORT).lookup(Server.RMI_NAME));
        var comm1 = new ClientRestImpl("ing-sw-2019-7.herokuapp.com");
        var token = comm.authUser("lorenzo", "lorenzo");
        var room = comm.createRoom(token, "room");
        System.out.println(room);
    }
}