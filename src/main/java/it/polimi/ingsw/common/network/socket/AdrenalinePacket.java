package it.polimi.ingsw.common.network.socket;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class AdrenalinePacket implements Serializable {
    private static final long serialVersionUID = 1;

    private final @NotNull Type type;
    private final @Nullable String jsonObject;

    @Contract(pure = true)
    public AdrenalinePacket(@NotNull Type type, @Nullable String token, @Nullable Object object) {
        this.type = type;
        this.jsonObject = new Gson().toJson(Arrays.asList(token, new Gson().toJson(object)));
    }

    public @NotNull Type getType() {
        return type;
    }

    public @Nullable String getToken() {
        return (String) new Gson().fromJson(jsonObject, ArrayList.class).get(0);
    }

    public @Nullable <T> T getAssociatedObject() {
        try {
            return new Gson().fromJson((String) new Gson().fromJson(jsonObject, ArrayList.class).get(1), new TypeToken<T>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public enum Type {
        AUTH_USER, CREATE_USER, GET_ACTIVE_GAME, GET_ROOMS, JOIN_ROOM, CREATE_ROOM, START_GAME, DO_ACTION, GAME_UPDATE, ROOM_UPDATE, REMOVE_GAME_UPDATES, REMOVE_ROOM_UPDATES
    }
}