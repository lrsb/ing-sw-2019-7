package it.polimi.ingsw.common.network.socket;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The type Adrenaline packet.
 */
@SuppressWarnings("NullableProblems")
public class AdrenalinePacket implements Serializable {
    private static final long serialVersionUID = 1;

    private final @Nullable Type type;
    private final @Nullable String jsonObject;

    /**
     * Instantiates a new Adrenaline packet.
     *
     * @param type   the type
     * @param token  the token
     * @param object the object
     */
    @Contract(pure = true)
    public AdrenalinePacket(@NotNull Type type, @Nullable String token, @Nullable Object object) {
        this.type = type;
        this.jsonObject = new Gson().toJson(Arrays.asList(token, new Gson().toJson(object)));
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public @Nullable Type getType() {
        return type;
    }

    /**
     * Gets token.
     *
     * @return the token
     */
    public @Nullable String getToken() {
        return (String) new Gson().fromJson(jsonObject, ArrayList.class).get(0);
    }

    /**
     * Gets associated object.
     *
     * @param <T> the type parameter
     * @return the associated object
     */
    public @NotNull <T> @Nullable T getAssociatedObject() {
        try {
            return new Gson().fromJson((String) new Gson().fromJson(jsonObject, ArrayList.class).get(1), new TypeToken<T>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * The enum Type.
     */
    public enum Type {
        /**
         * Auth user type.
         */
        AUTH_USER,
        /**
         * Create user type.
         */
        CREATE_USER,
        /**
         * Get active game type.
         */
        GET_ACTIVE_GAME,
        /**
         * Get rooms type.
         */
        GET_ROOMS,
        /**
         * Join room type.
         */
        JOIN_ROOM,
        /**
         * Create room type.
         */
        CREATE_ROOM,
        /**
         * Start game type.
         */
        START_GAME,
        /**
         * Do action type.
         */
        DO_ACTION,
        /**
         * Game update type.
         */
        GAME_UPDATE,
        /**
         * Room update type.
         */
        ROOM_UPDATE,
        /**
         * Remove game updates type.
         */
        REMOVE_GAME_UPDATES,
        /**
         * Remove room updates type.
         */
        REMOVE_ROOM_UPDATES,
        ERROR
    }
}