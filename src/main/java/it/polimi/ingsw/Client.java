package it.polimi.ingsw;

import com.google.gson.Gson;
import it.polimi.ingsw.controllers.MainViewController;
import it.polimi.ingsw.controllers.base.NavigationController;
import it.polimi.ingsw.models.weapons.Weapon;
import it.polimi.ingsw.socket.AdrenalinePacket;
import it.polimi.ingsw.socket.AdrenalineSocket;
import it.polimi.ingsw.socket.AdrenalineSocketListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        new NavigationController(new MainViewController());
        var socket = new AdrenalineSocket("localhost", new AdrenalineSocketListener() {
            @Override
            public void onNewObject(@NotNull AdrenalinePacket object) {
                System.out.println(object);
            }

            @Override
            public void onClose(@NotNull AdrenalineSocket socket) {
                System.out.println("close: " + socket.getInetAddress().toString());
            }
        });
        var scanner = new Scanner(System.in);
        while (true) if (scanner.hasNext()) {
            var line = scanner.nextLine();
            if (line.equals("close")) break;
            var string = new Gson().toJson(new Weapon(Weapon.Name.FURNACE, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
            socket.send(new AdrenalinePacket(AdrenalinePacket.Type.JOIN_GAME, string));
        }
        socket.close();
    }
}