package it.polimi.ingsw.client.network;

import com.google.gson.Gson;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.network.API;
import it.polimi.ingsw.common.network.GameListener;
import it.polimi.ingsw.common.network.RoomListener;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.*;

public class ClientRestImpl implements API {
    private final @NotNull HttpClient httpclient = HttpClientBuilder.create().build();
    private final @NotNull HttpHost host;

    private @Nullable WebSocketClient gameWebSocket;
    private @Nullable WebSocketClient roomWebSocket;

    @Contract(pure = true)
    public ClientRestImpl(@NotNull String hostname) {
        this.host = new HttpHost(hostname, hostname.equals("localhost") ? 80 : 443, hostname.equals("localhost") ? "http" : "https");
    }

    @Override
    public @Nullable String authUser(@NotNull String nickname, @NotNull String password) {
        try {
            var request = new HttpPost("/authUser?nickname=" + nickname + "&password=" + password);
            return new Gson().fromJson(new Scanner(httpclient.execute(host, request).getEntity().getContent()).nextLine(), String.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public @Nullable String createUser(@NotNull String nickname, @NotNull String password) {
        try {
            var request = new HttpPost("/createUser?nickname=" + nickname + "&password=" + password);
            return new Gson().fromJson(new Scanner(httpclient.execute(host, request).getEntity().getContent()).nextLine(), String.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public @Nullable List<Room> getRooms(@NotNull String token) {
        try {
            var request = new HttpGet("/getRooms");
            request.addHeader("auth-token", token);
            return new Gson().fromJson(new Scanner(httpclient.execute(host, request).getEntity().getContent()).nextLine(), ArrayList.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public @Nullable Room joinRoom(@NotNull String token, @NotNull UUID roomUuid) {
        try {
            var request = new HttpPost("/joinRoom?uuid" + roomUuid);
            request.addHeader("auth-token", token);
            return new Gson().fromJson(new Scanner(httpclient.execute(host, request).getEntity().getContent()).nextLine(), Room.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public @Nullable Room createRoom(@NotNull String token, @NotNull String name) {
        try {
            var request = new HttpPost("/createRoom?name=" + name);
            request.addHeader("auth-token", token);
            return new Gson().fromJson(new Scanner(httpclient.execute(host, request).getEntity().getContent()).nextLine(), Room.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public @Nullable UUID startGame(@NotNull String token, @NotNull UUID roomUuid) {
        try {
            var request = new HttpPost("/startGame?uuid=" + roomUuid);
            request.addHeader("auth-token", token);
            return new Gson().fromJson(new Scanner(httpclient.execute(host, request).getEntity().getContent()).nextLine(), UUID.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean doMove(@NotNull String token, @NotNull Object move) {
        try {
            var request = new HttpPost("/doMove?move=" + move);
            request.addHeader("auth-token", token);
            return new Gson().fromJson(new Scanner(httpclient.execute(host, request).getEntity().getContent()).nextLine(), boolean.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //TODO: auth-token is cleartext
    @Override
    public void addGameListener(@NotNull String token, @NotNull GameListener gameListener) {
        try {
            var headers = new HashMap<String, String>();
            headers.put("auth-token", token);
            gameWebSocket = new WebSocketClient(new URI(host.getHostName() + "/gameUpdate"), headers) {
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
                    gameListener.disconnected();
                }

                @Override
                public void onError(Exception e) {
                    gameWebSocket = null;
                    gameListener.disconnected();
                }
            };
            gameWebSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addRoomListener(@NotNull String token, @NotNull RoomListener roomListener) {
        try {
            var headers = new HashMap<String, String>();
            headers.put("auth-token", token);
            roomWebSocket = new WebSocketClient(new URI("ws://" + host.getHostName() + "/roomUpdate"), headers) {
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
                    roomListener.disconnected();
                }

                @Override
                public void onError(Exception e) {
                    roomWebSocket = null;
                    roomListener.disconnected();
                }
            };
            roomWebSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
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
}