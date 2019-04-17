package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Each cell have 4 bounds, they can be a door, a wall or nothing, that means that the two cells are in the same room.
 */
public class Bounds {
    private @NotNull Type n;
    private @NotNull Type s;
    private @NotNull Type w;
    private @NotNull Type e;

//private non va fatto.
    @Contract(pure = true)
    private Bounds(@NotNull Type n, @NotNull Type s, @NotNull Type w, @NotNull Type e) {
        this.n = n;
        this.s = s;
        this.w = w;
        this.e = e;
    }

    /**
     * Get the bound's type.
     * @param direction The bound's direction.
     * @return Type of indicated bound.
     */
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

    /**
     * Set the bound type.
     * @param direction The direction of the bound that you want to set the type to. (?)
     * @param type The type of the bound.
     * @return The modified object.
     */
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
        N(0, 1), S(0, -1), W(-1, 0), E(1, 0);

        private int x;
        private int y;

        @Contract(pure = true)
        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Contract(pure = true)
        int getX() {
            return x;
        }

        @Contract(pure = true)
        int getY() {
            return y;
        }
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