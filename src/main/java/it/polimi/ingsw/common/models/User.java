package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.UUID;

/**
 * The type User.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1;

    private @NotNull UUID uuid = UUID.randomUUID();
    private @NotNull String nickname;

    /**
     * Instantiates a new User.
     *
     * @param nickname the nickname
     */
    public User(@NotNull String nickname) {
        this.nickname = nickname;
    }

    /**
     * Gets uuid.
     *
     * @return the uuid
     */
    public @NotNull UUID getUuid() {
        return uuid;
    }

    /**
     * Gets nickname.
     *
     * @return the nickname
     */
    public @NotNull String getNickname() {
        return nickname;
    }
}