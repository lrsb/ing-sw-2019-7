package it.polimi.ingsw.server.controllers;

import it.polimi.ingsw.common.models.User;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;
import java.util.ArrayList;

class SecureUserController {
    private static final @NotNull SecureRandom random = new SecureRandom();
    private static final @NotNull char[] symbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private static final @NotNull char[] buf = new char[256];

    private static final @NotNull ArrayList<User> users = new ArrayList<>();

    @Contract(pure = true)
    private SecureUserController() {
    }

    @Contract(pure = true)
    static @Nullable User getUser(@Nullable String token) {
        return users.parallelStream().filter(e -> e.getToken().equals(token)).findFirst().orElse(null);
    }

    static synchronized @Nullable User createUser(@Nullable String nickname, @Nullable String password) {
        if (users.parallelStream().anyMatch(e -> e.getToken().equals(nickname))) return null;
        var user = new User(nickname, password);
        user.setToken(nextToken());
        users.add(user);
        return user;
    }

    static @Nullable User authUser(@Nullable String nickname, @Nullable String password) {
        var user = users.parallelStream().filter(e -> e.getNickname().equals(nickname) && e.getPassword().equals(password))
                .findFirst().orElse(null);
        if (user == null) return null;
        user.setToken(nextToken());
        return user;
    }

    @Contract(" -> new")
    private static @NotNull String nextToken() {
        for (int idx = 0; idx < buf.length; ++idx) buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }
}