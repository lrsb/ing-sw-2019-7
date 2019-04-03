package it.polimi.ingsw.models.common;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Bounds {
    private Type n;
    private Type s;
    private Type w;
    private Type e;

    @Contract(pure = true)
    private Bounds(Type n, Type s, Type w, Type e) {
        this.n = n;
        this.s = s;
        this.w = w;
        this.e = e;
    }

    public Type getType(@NotNull Direction direction) {
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

    public Bounds setType(@NotNull Direction direction, Type type) {
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

        @NotNull
        @Contract("_ -> new")
        public static Bounds withType(Type type) {
            return new Bounds(type, type, type, type);
        }
    }
}