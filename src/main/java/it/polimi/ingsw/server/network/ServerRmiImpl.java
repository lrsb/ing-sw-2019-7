package it.polimi.ingsw.server.network;

import it.polimi.ingsw.Server;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.network.rmi.RmiAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.UUID;

public class ServerRmiImpl extends UnicastRemoteObject implements RmiAPI {
    public ServerRmiImpl() throws RemoteException {
        super();
    }

    @Override
    public @Nullable String authUser(@NotNull String nickname, @NotNull String password) {
        return Server.controller.authUser(nickname, password);
    }

    @Override
    public @Nullable String createUser(@NotNull String nickname, @NotNull String password) {
        return Server.controller.createUser(nickname, password);
    }

    @Override
    public @Nullable List<Room> getRooms(@NotNull String token) {
        return Server.controller.getRooms(token);
    }

    @Override
    public @Nullable Room joinRoom(@NotNull String token, @NotNull UUID roomUuid) {
        return Server.controller.joinRoom(token, roomUuid);
    }

    @Override
    public @Nullable Room createRoom(@NotNull String token, @NotNull String name) {
        return Server.controller.createRoom(token, name);
    }

    @Override
    public @Nullable UUID startGame(@NotNull String token, @NotNull UUID roomUuid) {
        return Server.controller.startGame(token, roomUuid);
    }

    @Override
    public boolean doMove(@NotNull String token, @NotNull Object move) {
        return Server.controller.doMove(token, move);
    }

    @Override
    public @Nullable Game waitGameUpdate(@NotNull String token, @NotNull UUID gameUuid) {
        return Server.controller.waitGameUpdate(token, gameUuid);
    }

    @Override
    public @Nullable Room waitRoomUpdate(@NotNull String token, @NotNull UUID roomUuid) {
        return Server.controller.waitRoomUpdate(token, roomUuid);
    }
}