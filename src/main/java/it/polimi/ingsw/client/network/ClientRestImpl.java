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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientRestImpl implements API {
    private static final @NotNull ExecutorService executorService = Executors.newCachedThreadPool();
    private final @NotNull HttpClient httpclient = HttpClientBuilder.create().build();
    private final @NotNull HttpHost host;
    private volatile @Nullable GameListener gameListener;
    private volatile @Nullable RoomListener roomListener;

    @Contract(pure = true)
    public ClientRestImpl(@NotNull String ip) {
        this.host = new HttpHost(ip);
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

    @Override
    public void addGameListener(@NotNull String token, @NotNull UUID gameUuid, @NotNull GameListener gameListener) {
        this.gameListener = gameListener;
        executorService.submit(() -> {
            while (this.gameListener != null) try {
                var request = new HttpGet("/gameUpdate?uuid=" + gameUuid);
                var update = new Gson().fromJson(new Scanner(httpclient.execute(host, request).getEntity().getContent()).nextLine(), Game.class);
                if (update != null) Optional.ofNullable(this.gameListener).ifPresent(e -> e.onGameUpdated(update));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void addRoomListener(@NotNull String token, @NotNull UUID roomUuid, @NotNull RoomListener roomListener) {
        this.roomListener = roomListener;
        executorService.submit(() -> {
            while (this.roomListener != null) try {
                var request = new HttpGet("/roomUpdate?uuid=" + roomUuid);
                var update = new Gson().fromJson(new Scanner(httpclient.execute(host, request).getEntity().getContent()).nextLine(), Room.class);
                if (update != null) Optional.ofNullable(this.roomListener).ifPresent(e -> e.onRoomUpdated(update));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void removeGameListener(@NotNull String token, @NotNull UUID gameUuid) {
        this.gameListener = null;
    }

    @Override
    public void removeRoomListener(@NotNull String token, @NotNull UUID roomUuid) {
        this.roomListener = null;
    }
}
