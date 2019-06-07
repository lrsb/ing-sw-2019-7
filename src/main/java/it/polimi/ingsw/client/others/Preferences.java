package it.polimi.ingsw.client.others;

import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.common.models.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class Preferences {
    private static final @NotNull java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userRoot().node(Preferences.class.getName());

    public static @Nullable String getToken() {
        return preferences.get("token", null);
    }

    public static @NotNull UUID getUuid() {
        return UUID.fromString(preferences.get("uuid", null));
    }

    public static void setToken(@NotNull User.Auth token) {
        preferences.put("uuid", token.getUuid().toString());
        preferences.put("token", token.getToken());
    }

    public static @NotNull Optional<String> getTokenOrJumpBack(@Nullable NavigationController navigationController) {
        if (navigationController != null && !isLoggedIn()) Utils.jumpBackToLogin(navigationController);
        return Optional.ofNullable(getToken());
    }

    public static void deleteToken() {
        preferences.remove("token");
    }

    public static boolean isLoggedIn() {
        return getToken() != null;
    }

    public static @NotNull Optional<String> getOptionalToken() {
        return Optional.ofNullable(getToken());
    }
}