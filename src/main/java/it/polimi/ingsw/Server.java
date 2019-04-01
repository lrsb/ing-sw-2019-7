package it.polimi.ingsw;

import it.polimi.ingsw.models.weapons.Weapon;
import it.polimi.ingsw.socket.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Scanner;

public class Server implements AdrenalineServerSocketListener {
    public static void main(String[] args) {
        AdrenalineServerSocket server = null;
        try {
            server = new AdrenalineServerSocket(new Server());
            new Scanner(System.in).nextLine();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNewSocket(@NotNull AdrenalineSocket adrenalineSocket) {
        System.out.println("open: " + adrenalineSocket.getInetAddress());
        adrenalineSocket.setAdrenalineSocketListener(new AdrenalineSocketListener() {
            @Override
            public void onNewObject(@NotNull AdrenalinePacket object) {
                switch (object.getType()) {
                    case REQUEST_GAMES_LIST:
                        System.out.println(object.getAssociatedObject(Weapon.class));
                        break;
                    case JOIN_GAME:
                        System.out.println(object.getAssociatedObject(Weapon.class));
                        break;
                }
            }

            @Override
            public void onClose(@NotNull AdrenalineSocket socket) {
                System.out.println("close: " + socket.getInetAddress().toString());
            }
        });
    }
}