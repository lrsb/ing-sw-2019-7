package it.polimi.ingsw;

import it.polimi.ingsw.models.common.Room;
import it.polimi.ingsw.models.common.User;
import it.polimi.ingsw.models.server.Game;
import it.polimi.ingsw.models.server.HandyManny;
import it.polimi.ingsw.socket.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class Server implements AdrenalineServerSocketListener {
    private static Registry registry;
    private static HandyManny handyManny;
    private static ArrayList<Room> rooms = new ArrayList<>();
    private static ArrayList<Game> games = new ArrayList<>();

    static {
        try {
            registry = LocateRegistry.createRegistry(10000);
            handyManny = new HandyManny() {
                @Contract(pure = true)
                @Override
                public @NotNull List<Room> getRooms() {
                    return rooms;
                }

                @Override
                public boolean joinRoom(User user, @NotNull Room room) {
                    return Server.joinRoom(user, room);
                }

                @Override
                public @NotNull Room createRoom(@NotNull String name) {
                    var room = new Room(name);
                    rooms.add(room);
                    return room;
                }
            };
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws RemoteException {
        //registry.rebind(HandyManny.RMI_NAME, handyManny);
        registry.rebind("games/uuid", Game.Creator.newGame());
        try (var server = new AdrenalineServerSocket(new Server())) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean joinRoom(@NotNull User user, @NotNull Room jRoom) {
        for (var room : rooms) if (room.getUuid().equals(jRoom.getUuid())) return room.addUser(user);
        return false;
    }

    @Override
    public void onNewSocket(@NotNull AdrenalineSocket adrenalineSocket) {
        System.out.println("open: " + adrenalineSocket.getInetAddress());
        adrenalineSocket.setAdrenalineSocketListener(new AdrenalineSocketListener() {
            @Override
            public void onNewObject(@NotNull AdrenalinePacket object) {
                switch (object.getType()) {
                    case ROOM_LIST:
                        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.ROOM_LIST, rooms));
                        break;
                    case JOIN_ROOM:
                        try {
                            var associatedObject = object.getAssociatedObject(ArrayList.class);
                            //noinspection ConstantConditions
                            var user = (User) associatedObject.get(0);
                            var room = (Room) associatedObject.get(1);
                            adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.JOIN_ROOM, joinRoom(user, room)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case CREATE_ROOM:
                        var room = new Room(object.getAssociatedObject(String.class));
                        rooms.add(room);
                        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.CREATE_ROOM, room));
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