package it.polimi.ingsw.client.views.cli.base;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Segue {
    private final @NotNull String menu;
    private final @Nullable Class<?> aClass;
    private final @Nullable Object object;

    @Contract(pure = true)
    private Segue(@NotNull String menu, @Nullable Class<?> aClass, @Nullable Object object) {
        this.menu = menu;
        this.aClass = aClass;
        this.object = object;
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull Segue of(@NotNull String menu, @Nullable Class<?> aClass, @Nullable Object object) {
        return new Segue(menu, aClass, object);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull Segue of(@NotNull String menu, @Nullable Class<?> aClass) {
        return new Segue(menu, aClass, null);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull Segue of(@NotNull String menu, @Nullable Object object) {
        return new Segue(menu, null, object);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull Segue of(@NotNull String menu) {
        return new Segue(menu, null, null);
    }

    @NotNull String getMenu() {
        return menu;
    }

    @Nullable Class<?> getAClass() {
        return aClass;
    }

    @Nullable Object getObject() {
        return object;
    }
}