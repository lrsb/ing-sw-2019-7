package it.polimi.ingsw.common.network.socket;

import com.google.gson.Gson;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public class AdrenalinePacket {
    private @NotNull Type type;
    private @Nullable String jsonObject;

    @Contract(pure = true)
    public AdrenalinePacket(@NotNull Type type, @Nullable String token, @Nullable Object object) {
        this.type = type;
        this.jsonObject = new Gson().toJson(Arrays.asList(token, object));
    }

    public @NotNull Type getType() {
        return type;
    }

    public @Nullable String getToken() {
        return (String) new Gson().fromJson(jsonObject, ArrayList.class).get(0);
    }

    public @Nullable <T> T getAssociatedObject() {
        try {
            //noinspection unchecked
            return (T) new Gson().fromJson(jsonObject, ArrayList.class).get(1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public enum Type {
        ROOM_LIST, JOIN_ROOM, CREATE_ROOM, JOIN_GAME, START_GAME
    }
}
