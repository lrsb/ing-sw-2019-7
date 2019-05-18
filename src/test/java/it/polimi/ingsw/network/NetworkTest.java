package it.polimi.ingsw.network;

import it.polimi.ingsw.Server;
import it.polimi.ingsw.client.network.ClientRmiImpl;
import it.polimi.ingsw.common.models.Action;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.models.User;
import it.polimi.ingsw.common.network.GameListener;
import it.polimi.ingsw.common.network.RoomListener;
import it.polimi.ingsw.server.network.ServerRmiImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NetworkTest {
    private static final @NotNull String HOST = "localhost";
    private static final @NotNull UUID TEST_UUID = UUID.fromString("0940a01c-d78b-4b74-ab34-bff7fe77112c");

    @Test
    void rmiTest() throws RemoteException, NotBoundException {
        var server = new TestServerRmiImpl();
        LocateRegistry.createRegistry(Server.RMI_PORT).rebind(Server.RMI_NAME, server);
        var client = new ClientRmiImpl(LocateRegistry.getRegistry(HOST, Server.RMI_PORT).lookup(Server.RMI_NAME));
        assertEquals(client.authUser("", ""), "ok");
        assertEquals(client.createUser("", ""), "ok");
        assertNull(client.getActiveGame(""));
        assertEquals(client.getRooms("").get(0).getName(), "ok");
        assertEquals(client.joinRoom("", TEST_UUID).getName(), "ok");
        assertEquals(client.createRoom("", "").getName(), "ok");
        assertNull(client.startGame("", TEST_UUID));
        assertTrue(client.doAction("", Action.Builder.create(UUID.randomUUID()).buildMoveAction(new Point(0, 0))));
        client.addGameListener("", e -> fail());
        client.removeGameListener("");
        client.addRoomListener("", e -> fail());
        client.removeRoomListener("");
    }


    private class TestServerRmiImpl extends ServerRmiImpl {
        private TestServerRmiImpl() throws RemoteException {
            super();
        }

        @Override
        public @Nullable String authUser(@NotNull String nickname, @NotNull String password) {
            return "ok";
        }

        @Override
        public @Nullable String createUser(@NotNull String nickname, @NotNull String password) {
            return "ok";
        }

        @Override
        public @Nullable Game getActiveGame(@NotNull String token) {
            return null;
        }

        @Override
        public @Nullable List<Room> getRooms(@NotNull String token) {
            return List.of(new Room("ok", new User("ok")));
        }

        @Override
        public @Nullable Room joinRoom(@NotNull String token, @NotNull UUID roomUuid) {
            return new Room("ok", new User("ok"));
        }

        @Override
        public @Nullable Room createRoom(@NotNull String token, @NotNull String name) {
            return new Room("ok", new User("ok"));
        }

        @Override
        public @Nullable Game startGame(@NotNull String token, @NotNull UUID roomUuid) {
            return null;
        }

        @Override
        public boolean doAction(@NotNull String token, @NotNull Action action) {
            return true;
        }

        @Override
        public void addGameListener(@NotNull String token, @NotNull GameListener listener) {
            assertNotNull(listener);
        }

        @Override
        public void removeGameListener(@NotNull String token) {
            assertNotNull(token);
        }

        @Override
        public void addRoomListener(@NotNull String token, @NotNull RoomListener listener) {
            assertNotNull(listener);
        }

        @Override
        public void removeRoomListener(@NotNull String token) {
            assertNotNull(token);
        }
    }
}