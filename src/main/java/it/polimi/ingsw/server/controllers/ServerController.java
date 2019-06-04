package it.polimi.ingsw.server.controllers;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import it.polimi.ingsw.Server;
import it.polimi.ingsw.common.models.*;
import it.polimi.ingsw.common.models.wrappers.Opt;
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

@SuppressWarnings("Duplicates")
public class ServerController implements API {
    private static final @NotNull MongoCollection<Document> rooms = Server.mongoDatabase.getCollection("rooms");
    private static final @NotNull MongoCollection<Document> games = Server.mongoDatabase.getCollection("games");
    private final @NotNull HashMap<UUID, HashMap<UUID, GameListener>> gameListeners = new HashMap<>();
    private final @NotNull HashMap<UUID, HashMap<UUID, RoomListener>> roomListeners = new HashMap<>();

    @Override
    public @NotNull String authUser(@Nullable String nickname, @Nullable String password) throws RemoteException {
        return SecureUserController.authUser(nickname, password);
    }

    @Override
    public @NotNull String createUser(@Nullable String nickname, @Nullable String password) throws RemoteException {
        return SecureUserController.createUser(nickname, password);
    }

    //TODO: impl
    @Override
    public @NotNull Game getActiveGame(@Nullable String token) throws RemoteException {
        var user = SecureUserController.getUser(token);
        if (user == null) return null;
        return null;
    }

    @Override
    public @NotNull List<Room> getRooms(@Nullable String token) throws RemoteException {
        SecureUserController.getUser(token);
        var gson = new Gson();
        return StreamSupport.stream(rooms.find().filter(eq("gameCreated", false)).spliterator(), true)
                .map(e -> gson.fromJson(e.toJson(), Room.class)).collect(Collectors.toList());
    }

    @Override
    public @NotNull Room joinRoom(@Nullable String token, @Nullable UUID roomUuid) throws RemoteException {
        var user = SecureUserController.getUser(token);
        if (roomUuid != null) try {
            var room = new Gson().fromJson(Opt.of(rooms.find(eq("uuid", roomUuid.toString())).first()).e(Document::toJson).get(""), Room.class);
            if (!room.addUser(user)) throw new RemoteException("The room is full, go away!!");
            rooms.replaceOne(eq("uuid", roomUuid.toString()), Document.parse(new Gson().toJson(room)));
            informRoomUsers(room);
            return room;
        } catch (Exception ignored) {
            throw new RemoteException("Something went wrong, sometimes it happens!!");
        }
        else throw new RemoteException("The UUID!!");
    }

    @Override
    public @NotNull Room createRoom(@Nullable String token, @Nullable Room newRoom) throws RemoteException {
        var user = SecureUserController.getUser(token);
        if (newRoom != null) try {
            var room = new Room(newRoom.getName(), user);
            room.setActionTimeout(newRoom.getActionTimeout());
            room.setGameType(newRoom.getGameType());
            rooms.insertOne(Document.parse(new Gson().toJson(room)));
            return room;
        } catch (Exception ignored) {
            throw new RemoteException("Something went wrong, sometimes it happens!!");
        }
        else throw new RemoteException("The parameters!!");
    }

    @Override
    public void quitRoom(@Nullable String token, @Nullable UUID roomUuid) throws RemoteException {
        var user = SecureUserController.getUser(token);
        if (roomUuid != null) try {
            var room = new Gson().fromJson(Opt.of(rooms.find(eq("uuid", roomUuid.toString())).first()).e(Document::toJson).get(""), Room.class);
            room.removeUser(user);
            if (room.getUsers().isEmpty()) rooms.deleteOne(eq("uuid", roomUuid.toString()));
            else rooms.replaceOne(eq("uuid", roomUuid.toString()), Document.parse(new Gson().toJson(room)));
            informRoomUsers(room);
        } catch (Exception ignored) {
            throw new RemoteException("Something went wrong, sometimes it happens!!");
        }
        else throw new RemoteException("The UUID!!");
    }

    @Override
    public @NotNull Game startGame(@Nullable String token, @Nullable UUID roomUuid) throws RemoteException {
        var user = SecureUserController.getUser(token);
        if (roomUuid != null) try {
            var room = new Gson().fromJson(Opt.of(rooms.find(eq("uuid", roomUuid.toString())).first()).e(Document::toJson).get(""), Room.class);
            if (!room.getUsers().get(0).getUuid().equals(user.getUuid()))
                throw new RemoteException("You can't do this!");
            var game = GameImpl.Creator.newGame(room);
            games.insertOne(Document.parse(new Gson().toJson(game)));
            room.setGameCreated();
            informRoomUsers(room);
            rooms.deleteOne(eq("uuid", roomUuid.toString()));
            return new Gson().fromJson(new Gson().toJson(game), Game.class);
        } catch (Exception ignored) {
            throw new RemoteException("Something went wrong, sometimes it happens!!");
        }
        else throw new RemoteException("The UUID!!");
    }

    @Override
    public boolean doAction(@Nullable String token, @Nullable Action action) throws RemoteException {
        var user = SecureUserController.getUser(token);
        if (action != null && action.getGameUuid() != null) try {
            var game = new Gson().fromJson(Opt.of(games.find(eq("uuid", action.getGameUuid().toString())).first()).e(Document::toJson).get(""), GameImpl.class);
            if (game.getPlayers().parallelStream().noneMatch(e -> e.getUuid().equals(user.getUuid()))) return false;
            var value = game.doAction(action);
            if (value)
                games.replaceOne(eq("uuid", action.getGameUuid().toString()), Document.parse(new Gson().toJson(game)));
            informGamePlayers(game);
            return value;
        } catch (Exception ignored) {
            throw new RemoteException("Something went wrong, sometimes it happens!!");
        }
        else throw new RemoteException("The action is not valid!!");
    }

    @Override
    public void addGameListener(@Nullable String token, @Nullable UUID gameUuid, @Nullable GameListener listener) throws RemoteException {
        var user = SecureUserController.getUser(token);
        if (gameUuid == null || listener == null) throw new RemoteException("What the heck?!?!");
        var hashMap = gameListeners.getOrDefault(user.getUuid(), new HashMap<>());
        hashMap.put(gameUuid, listener);
        gameListeners.put(user.getUuid(), hashMap);
    }

    @Override
    public void removeGameListener(@Nullable String token, @Nullable UUID gameUuid) throws RemoteException {
        var user = SecureUserController.getUser(token);
        if (gameUuid == null) throw new RemoteException("What the heck?!?!");
        var hashMap = gameListeners.getOrDefault(user.getUuid(), new HashMap<>());
        hashMap.remove(gameUuid);
        gameListeners.put(user.getUuid(), hashMap);
    }

    @Override
    public void addRoomListener(@Nullable String token, @Nullable UUID roomUuid, @Nullable RoomListener listener) throws RemoteException {
        var user = SecureUserController.getUser(token);
        if (roomUuid == null || listener == null) throw new RemoteException("What the heck?!?!");
        var hashMap = roomListeners.getOrDefault(user.getUuid(), new HashMap<>());
        hashMap.put(roomUuid, listener);
        roomListeners.put(user.getUuid(), hashMap);
    }

    @Override
    public void removeRoomListener(@Nullable String token, @Nullable UUID roomUuid) throws RemoteException {
        var user = SecureUserController.getUser(token);
        if (roomUuid == null) throw new RemoteException("What the heck?!?!");
        var hashMap = roomListeners.getOrDefault(user.getUuid(), new HashMap<>());
        hashMap.remove(roomUuid);
        roomListeners.put(user.getUuid(), hashMap);
    }

    private void informGamePlayers(@NotNull Game game) {
        game.getPlayers().parallelStream().map(Player::getUuid).map(gameListeners::get).filter(Objects::nonNull).forEach(e -> {
            try {
                e.getOrDefault(game.getUuid(), f -> {
                }).onGameUpdate(game);
            } catch (RemoteException ex) {
                ex.printStackTrace();
                e.remove(game.getUuid());
            }
        });
    }

    private void informRoomUsers(@NotNull Room room) {
        room.getUsers().parallelStream().map(User::getUuid).map(roomListeners::get).filter(Objects::nonNull).forEach(e -> {
            try {
                e.getOrDefault(room.getUuid(), f -> {
                }).onRoomUpdate(room);
            } catch (RemoteException ex) {
                ex.printStackTrace();
                e.remove(room.getUuid());
            }
        });
    }
}