package it.polimi.ingsw.client.controllers.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.prefs.Preferences;

public class PreferenceController {
    private static final @NotNull Preferences preferences = Preferences.userRoot().node(PreferenceController.class.getName());

    public static @Nullable String getToken() {
        return preferences.get("token", null);
    }

    public static void setToken(@NotNull String token) {
        preferences.put("token", token);
    }
}