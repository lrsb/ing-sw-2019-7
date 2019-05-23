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

public class ServerController implements API {
    private static final @NotNull MongoCollection<Document> rooms = Server.mongoDatabase.getCollection("rooms");
    private static final @NotNull MongoCollection<Document> games = Server.mongoDatabase.getCollection("games");
    private final @NotNull HashMap<UUID, GameListener> gameListeners = new HashMap<>();
    private final @NotNull HashMap<UUID, RoomListener> roomListeners = new HashMap<>();

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
            var room = new Gson().fromJson(Opt.of(rooms.find(eq("uuid", roomUuid)).first()).e(Document::toJson).get(""), Room.class);
            if (!room.addUser(user)) throw new RemoteException("The room is full, go away!!");
            rooms.replaceOne(eq("uuid", roomUuid), Document.parse(new Gson().toJson(room)));
            informRoomUsers(room);
            return room;
        } catch (Exception ignored) {
            throw new RemoteException("Something went wrong, sometimes it happens!!");
        }
        else throw new RemoteException("The UUID!!");
    }

    @Override
    public @NotNull Room createRoom(@Nullable String token, @Nullable String name, int timeout, @Nullable Game.Type gameType) throws RemoteException {
        var user = SecureUserController.getUser(token);
        if (name != null && gameType != null) try {
            var room = new Room(name, user);
            room.setActionTimeout(timeout);
            room.setGameType(gameType);
            rooms.insertOne(Document.parse(new Gson().toJson(room)));
            return room;
        } catch (Exception ignored) {
            throw new RemoteException("Something went wrong, sometimes it happens!!");
        }
        else throw new RemoteException("The parameters!!");
    }

    @Override
    public @NotNull Game startGame(@Nullable String token, @Nullable UUID roomUuid) throws RemoteException {
        var user = SecureUserController.getUser(token);
        if (roomUuid != null) try {
            var room = new Gson().fromJson(Opt.of(rooms.find(eq("uuid", roomUuid)).first()).e(Document::toJson).get(""), Room.class);
            if (!room.getUsers().get(0).getUuid().equals(user.getUuid()))
                throw new RemoteException("You can't do this!");
            var game = GameImpl.Creator.newGame(room);
            games.insertOne(Document.parse(new Gson().toJson(game)));
            room.setGameCreated();
            informRoomUsers(room);
            rooms.deleteOne(eq("uuid", roomUuid));
            return new Gson().fromJson(new Gson().toJson(game), Game.class);
        } catch (Exception ignored) {
            throw new RemoteException("Something went wrong, sometimes it happens!!");
        }
        else throw new RemoteException("The UUID!!");
    }

    @Override
    public boolean doAction(@Nullable String token, @Nullable Action action) throws RemoteException {
        var user = SecureUserController.getUser(token);
        if (action != null) try {
            var game = new Gson().fromJson(Opt.of(games.find(eq("uuid", action.getGameUuid())).first()).e(Document::toJson).get(""), GameImpl.class);
            if (game.getPlayers().parallelStream().noneMatch(e -> e.getUuid().equals(user.getUuid()))) return false;
            var value = game.doAction(action);
            if (value) games.replaceOne(eq("uuid", action.getGameUuid()), Document.parse(new Gson().toJson(game)));
            informGamePlayers(game);
            return value;
        } catch (Exception ignored) {
            throw new RemoteException("Something went wrong, sometimes it happens!!");
        }
        else throw new RemoteException("The action is not valid!!");
    }

    @Override
    public void addGameListener(@Nullable String token, @Nullable GameListener listener) throws RemoteException {
        var user = SecureUserController.getUser(token);
        if (listener == null) throw new RemoteException("Where is the listener?!?!");
        gameListeners.put(user.getUuid(), listener);
    }

    @Override
    public void removeGameListener(@Nullable String token) throws RemoteException {
        var user = SecureUserController.getUser(token);
        gameListeners.remove(user.getUuid());
    }

    @Override
    public void addRoomListener(@Nullable String token, @Nullable RoomListener listener) throws RemoteException {
        var user = SecureUserController.getUser(token);
        if (listener == null) throw new RemoteException("Where is the listener?!?!");
        roomListeners.put(user.getUuid(), listener);
    }

    @Override
    public void removeRoomListener(@Nullable String token) throws RemoteException {
        var user = SecureUserController.getUser(token);
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
}