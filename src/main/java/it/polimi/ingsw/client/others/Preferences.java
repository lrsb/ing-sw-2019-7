package it.polimi.ingsw.client.others;

import it.polimi.ingsw.client.controllers.base.NavigationController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The type Preferences.
 */
public class Preferences {
    private static final @NotNull java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userRoot().node(Preferences.class.getName());

    /**
     * Gets token.
     *
     * @return the token
     */
    public static @Nullable String getToken() {
        return preferences.get("token", null);
    }

    /**
     * Sets token.
     *
     * @param token the token
     */
    public static void setToken(@NotNull String token) {
        preferences.put("token", token);
    }

    /**
     * Gets token or jump back.
     *
     * @param navigationController the navigation controller
     * @return the token or jump back
     */
    public static @NotNull Optional<String> getTokenOrJumpBack(@NotNull NavigationController navigationController) {
        if (!isLoggedIn()) Utils.jumpBackToLogin(navigationController);
        return Optional.ofNullable(getToken());
    }

    /**
     * Delete token.
     */
    public static void deleteToken() {
        preferences.remove("token");
    }

    /**
     * Is logged in boolean.
     *
     * @return the boolean
     */
    public static boolean isLoggedIn() {
        return getToken() != null;
    }
}