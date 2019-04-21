package it.polimi.ingsw;

import it.polimi.ingsw.client.network.ClientRestImpl;
import it.polimi.ingsw.client.network.ClientRmiImpl;
import it.polimi.ingsw.client.network.ClientSocketImpl;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.network.RoomListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.security.SecureRandom;

public class Client {
    //WARN: per provare il server fare commit e usare ClientRestImpl
    public static void main(String[] args) throws IOException, NotBoundException {
        //new NavigationController(MainViewController.class);
        var local = "localhost";
        var comm = new SecureRandom().nextBoolean() ? new ClientSocketImpl(local) : new ClientRmiImpl(LocateRegistry.getRegistry(local, Server.RMI_PORT).lookup(Server.RMI_NAME));
        var comm1 = new ClientRestImpl("ing-sw-2019-7.herokuapp.com");
        var token = comm1.authUser("lorenzo", "lorenzo");
        comm1.addRoomListener(token, new RoomListener() {
            @Override
            public void onRoomUpdate(@NotNull Room room) {
                System.out.println(room.getName());
            }

            @Override
            public void disconnected() {
                System.out.println("okss");
            }
        });
        comm1.removeRoomListener(token);
        //var room = comm.createRoom(token, "room");
        //System.out.println(room);
    }
}