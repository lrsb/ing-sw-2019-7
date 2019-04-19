package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Each cell have 4 bounds, they can be a door, a wall or nothing, that means that the two cells are in the same room.
 */
@SuppressWarnings({"WeakerAccess", "SpellCheckingInspection"})
public class Bounds implements Serializable {
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
     * @param direction The direction of the bound that you want to set up.
     * @param type The type of the bound.
     */
    public void setType(@NotNull Direction direction, @NotNull Type type) {
        switch (direction) {
            case N:
                n = type;
            case S:
                s = type;
                break;
            case W:
                w = type;
                break;
            case E:
                e = type;
        }
    }

    enum Type {
        DOOR, SAME_ROOM, WALL
    }

    enum Direction {
        N(0, 1), S(0, -1), W(-1, 0), E(1, 0);

        private int dx;
        private int dy;

        @Contract(pure = true)
        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        @Contract(pure = true)
        int getdX() {
            return dx;
        }

        @Contract(pure = true)
        int getdY() {
            return dy;
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