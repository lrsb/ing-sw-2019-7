package it.polimi.ingsw.server.controllers;

import it.polimi.ingsw.common.models.User;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SecureUserController {
    @Contract(pure = true)
    private SecureUserController() {
    }

    @Contract(pure = true)
    public static @Nullable User getUser(@Nullable String token) {
        return null;
    }

    @Contract(pure = true)
    public static @Nullable User createUser(@NotNull String nickname, @NotNull String passwordn) {
        return null;
    }

    @Contract(pure = true)
    public static @Nullable User authUser(@NotNull String nickname, @NotNull String password) {
        return null;
    }
}
