package it.polimi.ingsw.client.others;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Preferences {
    private static final @NotNull java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userRoot().node(Preferences.class.getName());

    public static @Nullable String getToken() {
        return preferences.get("token", null);
    }

    public static void setToken(@NotNull String token) {
        preferences.put("token", token);
    }
}