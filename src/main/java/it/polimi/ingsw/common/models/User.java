package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 1;

    private @NotNull UUID uuid = UUID.randomUUID();
    private @NotNull String nickname;

    public User(@NotNull String nickname) {
        this.nickname = nickname;
    }

    public @NotNull UUID getUuid() {
        return uuid;
    }

    public @NotNull String getNickname() {
        return nickname;
    }
}