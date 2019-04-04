package it.polimi.ingsw.server.controllers;

import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//TODO: impl
public class ServerController {
    private static final @NotNull List<Room> rooms = new ArrayList<>();
    private static final @NotNull List<Game> games = new ArrayList<>();

    @Contract(pure = true)
    private ServerController() {
    }

    @Contract(pure = true)
    public static @Nullable List<Room> getRooms(@Nullable String token) {
        var user = SecureUserController.getUser(token);
        if (user == null) return null;
        return rooms;
    }

    public static @Nullable Room joinRoom(@Nullable String token, @Nullable UUID roomUuid) {
        var user = SecureUserController.getUser(token);
        if (user == null) return null;
        for (var room : rooms) if (room.getUuid().equals(roomUuid) && room.addUser(user)) return room;
        return null;
    }

    public static @Nullable Room createRoom(@Nullable String token, @Nullable String name) {
        var user = SecureUserController.getUser(token);
        if (user == null) return null;
        var room = new Room(name);
        rooms.add(room);
        return room;
    }

    public static @Nullable UUID startGame(@Nullable String token, @Nullable UUID roomUuid) {
        var user = SecureUserController.getUser(token);
        if (user == null) return null;
        return null;
    }

    public static void doMove(@Nullable String token, @Nullable Object move) {
        var user = SecureUserController.getUser(token);
        if (user == null) return;
    }
}