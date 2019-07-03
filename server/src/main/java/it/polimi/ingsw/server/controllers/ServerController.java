package it.polimi.ingsw.server.controllers;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import it.polimi.ingsw.common.models.*;
import it.polimi.ingsw.common.models.wrappers.Opt;
import it.polimi.ingsw.common.network.API;
import it.polimi.ingsw.common.network.Listener;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.models.GameImpl;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.eq;

public class ServerController implements API {
    private static final @NotNull MongoCollection<Document> rooms = Server.mongoDatabase.getCollection("rooms");
    private static final @NotNull MongoCollection<Document> games = Server.mongoDatabase.getCollection("games");
    private final @NotNull HashMap<UUID, Listener> listeners = new HashMap<>();
    private final @NotNull HashMap<UUID, Timer> roomTimers = new HashMap<>();
    private final @NotNull HashMap<UUID, Timer> gameTimers = new HashMap<>();

    @Override
    public @NotNull User.Auth authUser(@Nullable String nickname, @Nullable String password) throws RemoteException {
        return SecureUserController.authUser(nickname, password);
    }

    @Override
    public @NotNull User.Auth createUser(@Nullable String nickname, @Nullable String password) throws RemoteException {
        return SecureUserController.createUser(nickname, password);
    }

    @Override
    public @NotNull Game getActiveGame(@Nullable String token) throws RemoteException {
        var user = SecureUserController.getUser(token);
        try {
            var game = findGame(user.getUuid());
            if (game == null) throw new RemoteException("No active game!!");
            return game;
        } catch (Exception ignored) {
            throw new RemoteException("No active game!!");
        }
    }

    private @Nullable Game findGame(@NotNull UUID userUuid) {
        return new Gson().fromJson(Opt.of(games.find(eq("players.uuid", userUuid.toString())).first()).e(Document::toJson).get(""), Game.class);
    }

    @Override
    public @NotNull List<Room> getRooms(@Nullable String token) throws RemoteException {
        SecureUserController.getUser(token);
        var gson = new Gson();
        return StreamSupport.stream(rooms.find().spliterator(), true)
                .map(e -> gson.fromJson(e.toJson(), Room.class)).collect(Collectors.toList());
    }

    @Override
    public @NotNull Room joinRoom(@Nullable String token, @Nullable UUID roomUuid) throws RemoteException {
        var user = SecureUserController.getUser(token);
        if (roomUuid != null) try {
            if (findGame(user.getUuid()) != null) throw new RemoteException("Exit your active game first!!");
            var room = new Gson().fromJson(Opt.of(rooms.find(eq("uuid", roomUuid.toString())).first()).e(Document::toJson).get(""), Room.class);
            if (!room.addUser(user)) throw new RemoteException("The room is full, go away!!");
            if (room.getUsers().size() >= 3) {
                var timeout = Integer.parseInt(System.getProperty("ROOM_TIMEOUT", "30"));
                room.setStartTime(System.currentTimeMillis() + timeout * 1000);
                var timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            startGame(room.getUuid());
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }, timeout * 1000);
                Optional.ofNullable(roomTimers.put(room.getUuid(), timer)).ifPresent(Timer::cancel);
            } else room.setStartTime(-1);
            rooms.replaceOne(eq("uuid", roomUuid.toString()), Document.parse(new Gson().toJson(room)));
            informRoomUsers(room);
            return room;
        } catch (RemoteException e) {
            throw e;
        } catch (Exception ignored) {
            throw new RemoteException("Something went wrong, sometimes it happens!!");
        }
        else throw new RemoteException("The UUID!!");
    }

    @Override
    public @NotNull Room createRoom(@Nullable String token, @Nullable Room newRoom) throws RemoteException {
        var user = SecureUserController.getUser(token);
        if (newRoom != null) try {
            if (findGame(user.getUuid()) != null) throw new RemoteException("Exit your active game first!!");
            var room = new Room(newRoom.getName(), user);
            room.setActionTimeout(newRoom.getActionTimeout());
            room.setGameType(newRoom.getGameType());
            room.setSkulls(newRoom.getSkulls());
            room.setStartTime(-1);
            rooms.insertOne(Document.parse(new Gson().toJson(room)));
            return room;
        } catch (RemoteException e) {
            throw e;
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
    public void startGame(@Nullable String token, @Nullable UUID roomUuid) throws RemoteException {
        var user = SecureUserController.getUser(token);
        if (roomUuid != null) try {
            var room = new Gson().fromJson(Opt.of(rooms.find(eq("uuid", roomUuid.toString())).first()).e(Document::toJson).get(""), Room.class);
            if (!room.getUsers().get(0).getUuid().equals(user.getUuid()))
                throw new RemoteException("You can't do this!");
            startGame(room.getUuid());
        } catch (RemoteException e) {
            throw e;
        } catch (Exception ignored) {
            throw new RemoteException("Something went wrong, sometimes it happens!!");
        }
        else throw new RemoteException("The UUID!!");
    }

    private void startGame(@NotNull UUID roomUuid) throws RemoteException {
        var room = new Gson().fromJson(Opt.of(rooms.find(eq("uuid", roomUuid.toString())).first()).e(Document::toJson).get(""), Room.class);
        if (room.getUsers().size() < 3)
            throw new RemoteException("Too few players!");
        var game = GameImpl.Creator.newGame(room);
        games.insertOne(Document.parse(new Gson().toJson(game)));
        room.setGameCreated();
        informRoomUsers(room);
        rooms.deleteOne(eq("uuid", roomUuid.toString()));
    }

    @Override
    public void quitGame(@Nullable String token, @Nullable UUID gameUuid) throws RemoteException {
        var user = SecureUserController.getUser(token);
        if (gameUuid != null) try {
            var game = new Gson().fromJson(Opt.of(games.find(eq("uuid", gameUuid.toString())).first()).e(Document::toJson).get(""), GameImpl.class);
            if (game.getPlayers().parallelStream().noneMatch(e -> e.getUuid().equals(user.getUuid()))) return;
            game.getPlayers().remove(new Player(user, Player.BoardType.BANSHEE));
            if (game.getPlayers().size() < 3) {
                //TODO
                //games.deleteOne(eq("uuid", gameUuid.toString()));
                game.setCompleted(true);
            } //else games.replaceOne(eq("uuid", gameUuid.toString()), Document.parse(new Gson().toJson(game)));
            sendBroadcastToGame(game, user.getNickname() + " left the game." + (game.isCompleted() ? "\nNot enough players." : ""));
            informGamePlayers(game);
        } catch (Exception ignored) {
            throw new RemoteException("Something went wrong, sometimes it happens!!");
        }
        else throw new RemoteException("The action is not valid!!");
    }

    @Override
    public boolean doAction(@Nullable String token, @Nullable Action action) throws RemoteException {
        var user = SecureUserController.getUser(token);
        if (action != null) try {
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
    public void sendMessage(@Nullable String token, @Nullable Message message) throws RemoteException {
        var user = SecureUserController.getUser(token);
        if (message != null) try {
            var game = new Gson().fromJson(Opt.of(games.find(eq("uuid", message.getGameUuid().toString())).first()).e(Document::toJson).get(""), GameImpl.class);
            if (game.getPlayers().parallelStream().noneMatch(e -> e.getUuid().equals(user.getUuid()))) return;
            sendMessageToGame(game, new Message(user, message.getGameUuid(), message.getMessage(), System.currentTimeMillis()));
        } catch (Exception e) {
            throw new RemoteException("Something went wrong, sometimes it happens!!");
        }
        else throw new RemoteException("Is null!!");
    }

    @Override
    public void addListener(@Nullable String token, @Nullable Listener listener) throws RemoteException {
        var user = SecureUserController.getUser(token);
        if (listener == null) throw new RemoteException("What the heck?!?!");
        listeners.put(user.getUuid(), listener);
    }

    @Override
    public void removeListener(@Nullable String token) throws RemoteException {
        var user = SecureUserController.getUser(token);
        listeners.remove(user.getUuid());
    }

    private void informGamePlayers(@NotNull Game game) {
        game.getPlayers().parallelStream().map(Player::getUuid).forEach(e -> {
            try {
                listeners.get(e).onUpdate(game);
            } catch (Exception ex) {
                ex.printStackTrace();
                listeners.remove(e);
            }
        });
    }

    private void sendBroadcastToGame(@NotNull Game game, @NotNull String message) {
        game.getPlayers().parallelStream().map(Player::getUuid).forEach(e -> {
            try {
                listeners.get(e).onUpdate(message);
            } catch (Exception ex) {
                ex.printStackTrace();
                listeners.remove(e);
            }
        });
    }

    private void sendMessageToGame(@NotNull Game game, @NotNull Message message) {
        game.getPlayers().parallelStream().map(Player::getUuid).forEach(e -> {
            try {
                listeners.get(e).onUpdate(message);
            } catch (Exception ex) {
                ex.printStackTrace();
                listeners.remove(e);
            }
        });
    }

    private void informRoomUsers(@NotNull Room room) {
        room.getUsers().parallelStream().map(User::getUuid).forEach(e -> {
            try {
                listeners.get(e).onUpdate(room);
            } catch (Exception ex) {
                ex.printStackTrace();
                listeners.remove(e);
            }
        });
    }
}