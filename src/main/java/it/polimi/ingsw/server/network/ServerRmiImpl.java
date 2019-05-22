package it.polimi.ingsw.server.network;

import it.polimi.ingsw.Server;
import it.polimi.ingsw.common.models.Action;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.network.API;
import it.polimi.ingsw.common.network.GameListener;
import it.polimi.ingsw.common.network.RoomListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.UUID;

public class ServerRmiImpl extends UnicastRemoteObject implements API {
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
    public @Nullable Game getActiveGame(@NotNull String token) {
        return Server.controller.getActiveGame(token);
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
    public @Nullable Room createRoom(@NotNull String token, @NotNull String name, int timeout, Game.@NotNull Type gameType) {
        return Server.controller.createRoom(token, name, timeout, gameType);
    }

    @Override
    public @Nullable Game startGame(@NotNull String token, @NotNull UUID roomUuid) {
        return Server.controller.startGame(token, roomUuid);
    }

    @Override
    public boolean doAction(@NotNull String token, @NotNull Action action) {
        return Server.controller.doAction(token, action);
    }

    @Override
    public void addGameListener(@NotNull String token, @NotNull GameListener listener) {
        Server.controller.addGameListener(token, listener);
    }

    @Override
    public void removeGameListener(@NotNull String token) {
        Server.controller.removeGameListener(token);
    }

    @Override
    public void addRoomListener(@NotNull String token, @NotNull RoomListener listener) {
        Server.controller.addRoomListener(token, listener);
    }

    @Override
    public void removeRoomListener(@NotNull String token) {
        Server.controller.removeRoomListener(token);
    }
}