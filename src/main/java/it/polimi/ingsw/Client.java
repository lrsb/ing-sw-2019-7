package it.polimi.ingsw;

import it.polimi.ingsw.client.controllers.MainViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.network.ClientRmiImpl;
import it.polimi.ingsw.client.network.ClientSocketImpl;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.security.SecureRandom;

import static it.polimi.ingsw.Server.RMI_NAME;
import static it.polimi.ingsw.Server.RMI_PORT;

public class Client {
    public static void main(String[] args) throws IOException, NotBoundException {
        new NavigationController(MainViewController.class);
        var hostname = "localhost";
        var socket = new SecureRandom().nextBoolean();
        var comm = socket ? new ClientSocketImpl(hostname) : new ClientRmiImpl(LocateRegistry.getRegistry(hostname, RMI_PORT).lookup(RMI_NAME));
        var token = comm.createUser("ciao", "password");
        var room = comm.createRoom(token, "room");

    }
}