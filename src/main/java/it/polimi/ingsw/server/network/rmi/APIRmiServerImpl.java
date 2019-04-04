package it.polimi.ingsw.server.network.rmi;

import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.network.rmi.IRmiAPI;
import it.polimi.ingsw.server.controllers.SecureUserController;
import it.polimi.ingsw.server.controllers.ServerController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.UUID;

//TODO: impl
public class APIRmiServerImpl extends UnicastRemoteObject implements IRmiAPI {
    public APIRmiServerImpl() throws RemoteException {
        super();
    }

    @Override
    public @Nullable List<Room> getRooms(@NotNull String token) {
        return ServerController.getRooms(token);
    }

    @Override
    public @Nullable Room joinRoom(@NotNull String token, @NotNull UUID roomUuid) {
        return ServerController.joinRoom(token, roomUuid);
    }

    @Override
    public @Nullable Room createRoom(@NotNull String token, @NotNull String name) {
        return ServerController.createRoom(token, name);
    }

    @Override
    public @Nullable UUID startGame(@NotNull String token, @NotNull UUID roomUuid) {
        return ServerController.startGame(token, roomUuid);
    }

    @Override
    public void doMove(@NotNull String token, @NotNull Object move) {
        ServerController.doMove(token, move);
    }

    @Override
    public @Nullable Game waitGameUpdate(@NotNull String token, @NotNull UUID gameUuid) {
        var user = SecureUserController.getUser(token);
        if (user == null) return null;
        return null;
    }

    @Override
    public @Nullable Room waitRoomUpdate(@NotNull String token, @NotNull UUID roomUuid) {
        var user = SecureUserController.getUser(token);
        if (user == null) return null;
        return null;
    }
}
