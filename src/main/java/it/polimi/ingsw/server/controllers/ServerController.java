package it.polimi.ingsw.server.controllers;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import it.polimi.ingsw.Server;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Player;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.models.User;
import it.polimi.ingsw.common.network.API;
import it.polimi.ingsw.common.network.GameListener;
import it.polimi.ingsw.common.network.RoomListener;
import it.polimi.ingsw.server.models.GameImpl;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.eq;

//TODO: impl
public class ServerController implements API {
    private static final @NotNull MongoCollection<Document> rooms = Server.mongoDatabase.getCollection("rooms");
    private static final @NotNull MongoCollection<Document> games = Server.mongoDatabase.getCollection("games");
    private final @NotNull HashMap<UUID, GameListener> gameListeners = new HashMap<>();
    private final @NotNull HashMap<UUID, RoomListener> roomListeners = new HashMap<>();

    @Override
    public @Nullable String authUser(@Nullable String nickname, @Nullable String password) {
        return SecureUserController.authUser(nickname, password);
    }

    @Override
    public @Nullable String createUser(@Nullable String nickname, @Nullable String password) {
        return SecureUserController.createUser(nickname, password);
    }

    @Override
    public @Nullable UUID getActiveGame(@Nullable String token) {
        var user = SecureUserController.getUser(token);
        if (user == null) return null;
        return null;
    }

    @Override
    public @Nullable List<Room> getRooms(@Nullable String token) {
        var user = SecureUserController.getUser(token);
        if (user == null) return null;
        return convertCollection(rooms, Room.class);
    }

    @Override
    public @Nullable Room joinRoom(@Nullable String token, @Nullable UUID roomUuid) {
        try {
            var user = SecureUserController.getUser(token);
            if (user == null) return null;
            var room = new Gson().fromJson(rooms.find(eq("uuid", roomUuid)).first().toJson(), Room.class);
            room.addUser(user);
            rooms.replaceOne(eq("uuid", roomUuid), Document.parse(new Gson().toJson(room)));
            informRoomUsers(room);
            return room;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public @Nullable Room createRoom(@Nullable String token, @Nullable String name) {
        try {
            var user = SecureUserController.getUser(token);
            if (user == null) return null;
            var room = new Room(name);
            room.addUser(user);
            rooms.insertOne(Document.parse(new Gson().toJson(room)));
            return room;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public @Nullable UUID startGame(@Nullable String token, @Nullable UUID roomUuid) {
        try {
            var user = SecureUserController.getUser(token);
            if (user == null) return null;
            var room = new Gson().fromJson(rooms.find(eq("uuid", roomUuid)).first().toJson(), Room.class);
            if (room.getUsers().size() < Game.MIN_PLAYERS || room.getUsers().size() > Game.MAX_PLAYERS) return null;
            var game = GameImpl.Creator.newGame(room.getUuid(), room.getUsers());
            games.insertOne(Document.parse(new Gson().toJson(game)));
            room.setGameCreated();
            informRoomUsers(room);
            rooms.replaceOne(eq("uuid", roomUuid), Document.parse(new Gson().toJson(room)));
            return game.getUuid();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean doMove(@Nullable String token, @Nullable Object move) {
        var user = SecureUserController.getUser(token);
        return user == null;
        /*if (move.isLegal()) {
            Game game;
            game.getPlayers().parallelStream().map(Player::getUuid).map(gameListeners::get).filter(Objects::nonNull).forEach(e -> {
                try {
                    e.onGameUpdate(game);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
        }*/
    }

    @Override
    public void addGameListener(@Nullable String token, @Nullable GameListener listener) {
        var user = SecureUserController.getUser(token);
        if (user == null) return;
        gameListeners.put(user.getUuid(), listener);
    }

    @Override
    public void removeGameListener(@Nullable String token) {
        var user = SecureUserController.getUser(token);
        if (user == null) return;
        gameListeners.remove(user.getUuid());
    }

    @Override
    public void addRoomListener(@Nullable String token, @Nullable RoomListener listener) {
        var user = SecureUserController.getUser(token);
        if (user == null) return;
        roomListeners.put(user.getUuid(), listener);
    }

    @Override
    public void removeRoomListener(@Nullable String token) {
        var user = SecureUserController.getUser(token);
        if (user == null) return;
        roomListeners.remove(user.getUuid());
    }

    private void informGamePlayers(@NotNull Game game) {
        game.getPlayers().parallelStream().map(Player::getUuid).map(gameListeners::get).filter(Objects::nonNull).forEach(e -> {
            try {
                e.onGameUpdate(game);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void informRoomUsers(@NotNull Room room) {
        room.getUsers().parallelStream().map(User::getUuid).map(roomListeners::get).filter(Objects::nonNull).forEach(e -> {
            try {
                e.onRoomUpdate(room);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        });
    }

    private @NotNull <T> List<T> convertCollection(@NotNull MongoCollection<Document> collection, @NotNull Class<T> type) {
        var gson = new Gson();
        return StreamSupport.stream(collection.find().spliterator(), false).map(e -> gson.fromJson(e.toJson(), type)).collect(Collectors.toList());
    }
}