package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * {@link java.util.Optional} sucks.
 *
 * @param <T>
 */
public class Opt<T> {
    private T opt;

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
    public <U> Opt<U> e(OptLambda<T, U> lambda) {
        if (opt == null) return new Opt<>(null);
        return new Opt<>(lambda.run(opt));
    }

    /**
     * Get the object, it can be null.
     *
     * @return The object
     */
    public @Nullable T get() {
        return opt;
    }

    public interface OptLambda<T, U> {
        @Nullable U run(@NotNull T notNull);
    }
}