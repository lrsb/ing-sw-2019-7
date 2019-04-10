package it.polimi.ingsw.server.controllers;

import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.network.rmi.RmiAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//TODO: impl
public class ServerController implements RmiAPI {
    private final @NotNull List<Room> rooms = new ArrayList<>();
    private final @NotNull List<Game> games = new ArrayList<>();

    @Override
    public @Nullable String authUser(@Nullable String nickname, @Nullable String password) {
        var user = SecureUserController.authUser(nickname, password);
        return user != null ? user.getToken() : null;
    }

    @Override
    public @Nullable String createUser(@Nullable String nickname, @Nullable String password) {
        var user = SecureUserController.createUser(nickname, password);
        return user != null ? user.getToken() : null;
    }

    @Override
    public @Nullable List<Room> getRooms(@Nullable String token) {
        var user = SecureUserController.getUser(token);
        if (user == null) return null;
        return rooms;
    }

    @Override
    public @Nullable Room joinRoom(@Nullable String token, @Nullable UUID roomUuid) {
        var user = SecureUserController.getUser(token);
        if (user == null) return null;
        for (var room : rooms) if (room.getUuid().equals(roomUuid) && room.addUser(user)) return room;
        return null;
    }

    @Override
    public @Nullable Room createRoom(@Nullable String token, @Nullable String name) {
        var user = SecureUserController.getUser(token);
        if (user == null) return null;
        var room = new Room(name);
        rooms.add(room);
        room.addUser(user);
        return room;
    }

    @Override
    public @Nullable UUID startGame(@Nullable String token, @Nullable UUID roomUuid) {
        var user = SecureUserController.getUser(token);
        if (user == null) return null;
        return null;
    }

    @Override
    public boolean doMove(@Nullable String token, @Nullable Object move) {
        var user = SecureUserController.getUser(token);
        return user == null;
    }

    @Override
    public @Nullable Game waitGameUpdate(@Nullable String token, @Nullable UUID gameUuid) {
        var user = SecureUserController.getUser(token);
        if (user == null) return null;
        return null;
    }

    @Override
    public @Nullable Room waitRoomUpdate(@Nullable String token, @Nullable UUID roomUuid) {
        var user = SecureUserController.getUser(token);
        if (user == null) return null;
        return null;
    }
}