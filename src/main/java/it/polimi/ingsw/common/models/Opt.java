package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Optionals in Java sucks.
 */
public class Opt<T> {
    private @Nullable T opt;

    @Contract(pure = true)
    private Opt(@Nullable T nullable) {
        this.opt = nullable;
    }

    /**
     * Create a new Opt with a nullable object.
     *
     * @param nullable The nullable object
     * @return A new Opt with a nullable object.
     */
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull <T> Opt<T> of(@Nullable T nullable) {
        return new Opt<>(nullable);
    }

    /**
     * Call a method on a not null object. If the object is null the null propagates until {@code get()} is called.
     *
     * @return A Opt that contains the return object.
     */
    public <U> @NotNull Opt<U> e(Function<T, U> function) {
        if (opt == null) return new Opt<>(null);
        else return new Opt<>(function.apply(opt));
    }

    /**
     * Get the object, it can be null.
     *
     * @return The object.
     */
    public @Nullable T get() {
        return opt;
    }

    /**
     * Get the object, if is null is replaced with notNull param.
     *
     * @param or The object used to replace the original if is null.
     * @return A not null object.
     */
    public @NotNull T get(@NotNull T or) {
        var object = get();
        return object == null ? or : object;
    }
}