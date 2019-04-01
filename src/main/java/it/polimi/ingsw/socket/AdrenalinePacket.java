package it.polimi.ingsw.socket;

import com.google.gson.Gson;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdrenalinePacket {
    private @NotNull Type type;
    private @Nullable String jsonObject;

    @Contract(pure = true)
    public AdrenalinePacket(@NotNull Type type, @Nullable Object jsonObject) {
        this.type = type;
        this.jsonObject = new Gson().toJson(jsonObject);
    }

    public @NotNull Type getType() {
        return type;
    }

    public @Nullable <E> E getAssociatedObject(Class<E> type) {
        try {
            return new Gson().fromJson(jsonObject, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public enum Type {
        REQUEST_GAMES_LIST, JOIN_GAME, RESPONSE
    }
}
