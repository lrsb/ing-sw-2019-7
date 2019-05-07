package it.polimi.ingsw;

import it.polimi.ingsw.client.controllers.MainViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.network.ClientRestImpl;
import it.polimi.ingsw.client.others.Preferences;
import it.polimi.ingsw.common.network.API;

import java.io.IOException;
import java.rmi.NotBoundException;

public class Client {
    public static API API;

    //WARN: per provare il server fare commit e usare ClientRestImp
    public static void main(String[] args) throws IOException, NotBoundException {
        var local = "localhost";
        //var comm = new SecureRandom().nextBoolean() ? new ClientSocketImpl(local) : new ClientRmiImpl(LocateRegistry.getRegistry(local, Server.RMI_PORT).lookup(Server.RMI_NAME));
        API = new ClientRestImpl("ing-sw-2019-7.herokuapp.com");
        var token = Preferences.getToken() != null ? Preferences.getToken() : API.authUser("lorenzo", "lorenzo");
        if (token != null) {
            Preferences.setToken(token);
        }
        //comm1.removeRoomListener(token);
        //var room = comm.createRoom(token, "room");
        //System.out.println(room);
        new NavigationController(MainViewController.class);
    }
}