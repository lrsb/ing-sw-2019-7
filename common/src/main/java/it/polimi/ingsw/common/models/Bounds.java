package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Each cell has 4 bounds, each bound can be a door, a wall or nothing, that means that the two cells are in the same room.
 */
@SuppressWarnings({"SpellCheckingInspection"})
public class Bounds implements Serializable {
    private static final long serialVersionUID = 1;

    private @NotNull Type n;
    private @NotNull Type s;
    private @NotNull Type w;
    private @NotNull Type e;

    /**
     * Create new bounds for a cell.
     *
     * @param n Bound type in north direction.
     * @param s Bound type in south direction.
     * @param w Bound type in west direction.
     * @param e Bound type in east direction.
     */
    @Contract(pure = true)
    Bounds(@NotNull Type n, @NotNull Type s, @NotNull Type w, @NotNull Type e) {
        this.n = n;
        this.s = s;
        this.w = w;
        this.e = e;
    }

    /**
     * Get the bound's type.
     *
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
     *
     * @param direction The direction of the bound that you want to set up.
     * @param type      The type of the bound.
     */
    void setType(@NotNull Direction direction, @NotNull Type type) {
        switch (direction) {
            case N:
                n = type;
                break;
            case S:
                s = type;
                break;
            case W:
                w = type;
                break;
            case E:
                e = type;
                break;
        }
    }

    /**
     * Type of the bound.
     */
    public enum Type {
        /**
         * Door type.
         */
        DOOR,
        /**
         * Same room type.
         */
        SAME_ROOM,
        /**
         * Wall type.
         */
        WALL
    }

    /**
     * Direction enum.
     */
    public enum Direction {
        /**
         * N direction.
         */
        N(0, 1),
        /**
         * S direction.
         */
        S(0, -1),
        /**
         * W direction.
         */
        W(-1, 0),
        /**
         * E direction.
         */
        E(1, 0);

        private int dx;
        private int dy;

        @Contract(pure = true)
        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        /**
         * Get the movement on X axis.
         *
         * @return The movement on X axis.
         */
        @Contract(pure = true)
        public int getdX() {
            return dx;
        }

        /**
         * Get the movement on Y axis.
         *
         * @return The movement on Y axis.
         */
        @Contract(pure = true)
        public int getdY() {
            return dy;
        }
    }
}