package it.polimi.ingsw.client.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.common.models.Action;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.network.API;
import it.polimi.ingsw.common.network.GameListener;
import it.polimi.ingsw.common.network.RoomListener;
import it.polimi.ingsw.common.network.exceptions.UserRemoteException;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClientRestImpl implements API {
    private final @NotNull HttpClient client = HttpClient.newHttpClient();
    private final @NotNull String hostname;
    private final @NotNull String host;

    private @Nullable WebSocketClient gameWebSocket;
    private @Nullable WebSocketClient roomWebSocket;

    @Contract(pure = true)
    public ClientRestImpl(@NotNull String hostname) throws RemoteException {
        this.hostname = hostname;
        this.host = (hostname.equals("localhost") ? "http" : "https") + "://" + hostname;
        try {
            client.send(HttpRequest.newBuilder().uri(URI.create(host)).build(), HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RemoteException();
        }
    }

    private static <T> @NotNull T processResponse(@NotNull HttpResponse<T> response) throws RemoteException {
        if (response.statusCode() == 200) return response.body();
        if (response.statusCode() == 401)
            throw new UserRemoteException(response.body() instanceof String ? (String) response.body() : "Unknown error!!");
        else
            throw new RemoteException(response.body() instanceof String ? (String) response.body() : "Unknown error!!");
    }

    @Override
    public @NotNull String authUser(@NotNull String nickname, @NotNull String password) throws RemoteException {
        try {
            var request = HttpRequest.newBuilder().uri(URI.create(host + "/authUser?nickname=" + nickname + "&password=" + password)).POST(HttpRequest.BodyPublishers.noBody()).build();
            return new Gson().fromJson(processResponse(client.send(request, HttpResponse.BodyHandlers.ofString())), String.class);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RemoteException("Unknown error!!");
        }
    }

    @Override
    public @NotNull String createUser(@NotNull String nickname, @NotNull String password) throws RemoteException {
        try {
            var request = HttpRequest.newBuilder().uri(URI.create(host + "/createUser?nickname=" + nickname + "&password=" + password)).POST(HttpRequest.BodyPublishers.noBody()).build();
            return new Gson().fromJson(processResponse(client.send(request, HttpResponse.BodyHandlers.ofString())), String.class);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RemoteException("Unknown error!!");
        }
    }

    @Override
    public @NotNull Game getActiveGame(@NotNull String token) throws RemoteException {
        try {
            var request = HttpRequest.newBuilder().uri(URI.create(host + "/getActiveGame")).GET().header("auth-token", token).build();
            return new Gson().fromJson(processResponse(client.send(request, HttpResponse.BodyHandlers.ofString())), Game.class);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RemoteException("Unknown error!!");
        }
    }

    @Override
    public @NotNull List<Room> getRooms(@NotNull String token) throws RemoteException {
        try {
            var request = HttpRequest.newBuilder().uri(URI.create(host + "/getRooms")).GET().header("auth-token", token).build();
            return new Gson().fromJson(processResponse(client.send(request, HttpResponse.BodyHandlers.ofString())), new TypeToken<List<Room>>() {
            }.getType());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RemoteException("Unknown error!!");
        }
    }

    @Override
    public @NotNull Room joinRoom(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException {
        try {
            var request = HttpRequest.newBuilder().uri(URI.create(host + "/joinRoom?uuid=" + roomUuid)).POST(HttpRequest.BodyPublishers.noBody()).header("auth-token", token).build();
            return new Gson().fromJson(processResponse(client.send(request, HttpResponse.BodyHandlers.ofString())), Room.class);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RemoteException("Unknown error!!");
        }
    }

    @Override
    public @NotNull Room createRoom(@NotNull String token, @NotNull String name, int timeout, @NotNull Game.Type gameType) throws RemoteException {
        try {
            var request = HttpRequest.newBuilder().uri(URI.create(host + "/createRoom?name=" + name + "&timeout=" + timeout + "&gameType=" + gameType)).POST(HttpRequest.BodyPublishers.noBody()).header("auth-token", token).build();
            return new Gson().fromJson(processResponse(client.send(request, HttpResponse.BodyHandlers.ofString())), Room.class);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RemoteException("Unknown error!!");
        }
    }

    @Override
    public @NotNull Game startGame(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException {
        try {
            var request = HttpRequest.newBuilder().uri(URI.create(host + "/startGame?uuid=" + roomUuid)).POST(HttpRequest.BodyPublishers.noBody()).header("auth-token", token).build();
            return new Gson().fromJson(processResponse(client.send(request, HttpResponse.BodyHandlers.ofString())), Game.class);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RemoteException("Unknown error!!");
        }
    }

    @Override
    public boolean doAction(@NotNull String token, @NotNull Action action) throws RemoteException {
        try {
            var request = HttpRequest.newBuilder().uri(URI.create(host + "/doAction")).POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(action))).header("auth-token", token).build();
            return new Gson().fromJson(processResponse(client.send(request, HttpResponse.BodyHandlers.ofString())), boolean.class);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RemoteException("Unknown error!!");
        }
    }

    //TODO: auth-token is cleartext
    @Override
    public void addGameListener(@NotNull String token, @NotNull GameListener gameListener) throws RemoteException {
        try {
            var headers = new HashMap<String, String>();
            headers.put("auth-token", token);
            gameWebSocket = new WebSocketClient(new URI("ws://" + hostname + "/gameUpdate"), headers) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                }

                @Override
                public void onMessage(String s) {
                    try {
                        gameListener.onGameUpdate(new Gson().fromJson(s, Game.class));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    gameWebSocket = null;
                }

                @Override
                public void onError(Exception e) {
                    gameWebSocket = null;
                }
            };
            gameWebSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RemoteException("Unknown error!!");
        }
    }

    @Override
    public void removeGameListener(@NotNull String token) {
        Optional.ofNullable(gameWebSocket).ifPresent(WebSocketClient::close);
        gameWebSocket = null;
    }

    @Override
    public void removeRoomListener(@NotNull String token) {
        Optional.ofNullable(roomWebSocket).ifPresent(WebSocketClient::close);
        roomWebSocket = null;
    }

    @Override
    public void addRoomListener(@NotNull String token, @NotNull RoomListener roomListener) throws RemoteException {
        try {
            var headers = new HashMap<String, String>();
            headers.put("auth-token", token);
            roomWebSocket = new WebSocketClient(new URI("ws://" + hostname + "/roomUpdate"), headers) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                }

                @Override
                public void onMessage(String s) {
                    try {
                        roomListener.onRoomUpdate(new Gson().fromJson(s, Room.class));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    roomWebSocket = null;
                }

                @Override
                public void onError(Exception e) {
                    roomWebSocket = null;
                }
            };
            roomWebSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RemoteException("Unknown error!!");
        }
    }
}