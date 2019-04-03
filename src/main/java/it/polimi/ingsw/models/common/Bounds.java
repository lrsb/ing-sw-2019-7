package it.polimi.ingsw.models.common;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Bounds {
    private @NotNull Type n;
    private @NotNull Type s;
    private @NotNull Type w;
    private @NotNull Type e;

    @Contract(pure = true)
    private Bounds(@NotNull Type n, @NotNull Type s, @NotNull Type w, @NotNull Type e) {
        this.n = n;
        this.s = s;
        this.w = w;
        this.e = e;
    }

    public @NotNull Type getType(@NotNull Direction direction) {
        switch (direction) {
            case N:
                return n;
            case S:
                return s;
            case W:
                return w;
            case E:
                return e;
        }
        return null;
    }

    public @NotNull Bounds setType(@NotNull Direction direction, @NotNull Type type) {
        switch (direction) {
            case N:
                n = type;
                return this;
            case S:
                s = type;
                return this;
            case W:
                w = type;
                return this;
            case E:
                e = type;
        }
        return this;
    }

    enum Type {
        DOOR, SAME_ROOM, WALL
    }

    enum Direction {
        N, S, W, E
    }

    public static class Creator {
        @Contract(pure = true)
        private Creator() {
        }

        @Contract("_ -> new")
        public static @NotNull Bounds withType(@NotNull Type type) {
            return new Bounds(type, type, type, type);
        }
    }
}