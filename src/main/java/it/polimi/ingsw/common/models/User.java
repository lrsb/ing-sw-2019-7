package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 1;

    @NotNull UUID uuid = UUID.randomUUID();
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

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj) {
        return obj instanceof User && ((User) obj).getUuid().equals(uuid);
    }

    public static class Auth extends User implements Serializable {
        private @NotNull String token;

        public Auth(@NotNull User user, @NotNull String token) {
            super(user.nickname);
            this.uuid = user.uuid;
            this.token = token;
        }

        public @NotNull String getToken() {
            return token;
        }
    }
}