package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * Cell class, each cell is the atom of the board, some cells are spawnpoint, the others are just normal cells which
 * have an ammo card on it, more cells compose a room and more rooms compose the game board.
 */
public class Cell implements Serializable {
    private static final long serialVersionUID = 1;

    private @NotNull Color color;
    private @NotNull Bounds bounds;
    private boolean spawnPoint;
    private @Nullable AmmoCard ammoCard;

    /**
     * Create new cell.
     *
     * @param color      The color of the cell.
     * @param bounds     Related bounds of the cell.
     * @param spawnPoint True if this cell is a spawnpoint.
     */
    @Contract(pure = true)
    private Cell(@NotNull Color color, @NotNull Bounds bounds, boolean spawnPoint) {
        this.color = color;
        this.bounds = bounds;
        this.spawnPoint = spawnPoint;
    }

    /**
     * Indicates if a certain cell is a spawnpoint.
     *
     * @return True if the cell is a spawnpoint, false otherwise.
     */
    public boolean isSpawnPoint() {
        return spawnPoint;
    }

    /**
     * Pick the ammo card in the cell
     *
     * @return the ammo card if there is one, null otherwise
     */
    public @Nullable AmmoCard getAmmoCard() {
        return ammoCard;
    }

    /**
     * Set a new ammo card on a cell.
     *
     * @param ammoCard The new ammo card that you want to assign to the cell.
     */
    public void setAmmoCard(@Nullable AmmoCard ammoCard) {
        this.ammoCard = ammoCard;
    }

    public void removeAmmoCard() {
        ammoCard = null;
    }

    /**
     * Get the color of the cell.
     *
     * @return The cell's color.
     */
    public @NotNull Color getColor() {
        return color;
    }

    /**
     * Get the bounds of the cell.
     *
     * @return the cell's bounds.
     */
    @NotNull
    public Bounds getBounds() {
        return bounds;
    }

    /**
     * Color enum.
     */
    public enum Color {
        /**
         * White color.
         */
        WHITE("\u001B[37m"),
        /**
         * Blue color.
         */
        BLUE("\u001B[36m"),
        /**
         * Red color.
         */
        RED("\u001B[31m"),
        /**
         * Purple color.
         */
        PURPLE("\u001B[35m"),
        /**
         * Yellow color.
         */
        YELLOW("\u001B[33m"),
        /**
         * Green color.
         */
        GREEN("\u001B[32m");


        private @NotNull String escape;

        @Contract(pure = true)
        Color(@NotNull String escape) {
            this.escape = escape;
        }

        @Contract(pure = true)
        public @NotNull String escape() {
            return escape;
        }
    }

    public static class Creator {
        private @Nullable Bounds bounds;
        private @Nullable Color color;
        private boolean spawnPoint = false;

        public static Creator withBounds(@NotNull String boundsString) {
            var creator = new Creator();
            creator.bounds = new Bounds(Bounds.Type.SAME_ROOM, Bounds.Type.SAME_ROOM, Bounds.Type.SAME_ROOM, Bounds.Type.SAME_ROOM);
            for (var direction : Bounds.Direction.values()) {
                char index;
                switch (direction) {
                    case N:
                        index = 0;
                        break;
                    case E:
                        index = 1;
                        break;
                    case S:
                        index = 2;
                        break;
                    default:
                        index = 3;
                        break;
                }
                switch (boundsString.charAt(index)) {
                    case '_':
                        creator.bounds.setType(direction, Bounds.Type.WALL);
                        break;
                    case '|':
                        creator.bounds.setType(direction, Bounds.Type.DOOR);
                        break;
                    case ' ':
                        creator.bounds.setType(direction, Bounds.Type.SAME_ROOM);
                        break;
                    default:
                        break;
                }
            }
            return creator;
        }

        public @NotNull Creator color(Color color) {
            this.color = color;
            return this;
        }

        public @NotNull Creator spawnPoint() {
            this.spawnPoint = true;
            return this;
        }

        public @Nullable Cell create() {
            return bounds != null && color != null ? new Cell(color, bounds, spawnPoint) : null;
        }
    }
}