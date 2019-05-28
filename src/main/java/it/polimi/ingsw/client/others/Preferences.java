package it.polimi.ingsw.client.others;

import it.polimi.ingsw.client.controllers.base.NavigationController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class Preferences {
    private static final @NotNull java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userRoot().node(Preferences.class.getName());

    public static @Nullable String getToken() {
        return preferences.get("token", null);
    }

    public static void setToken(@NotNull String token) {
        preferences.put("token", token);
    }

    public static @NotNull Optional<String> getTokenOrJumpBack(@NotNull NavigationController navigationController) {
        if (!isLoggedIn()) Utils.jumpBackToLogin(navigationController);
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