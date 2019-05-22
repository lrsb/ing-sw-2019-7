package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * Cell class, each cell is the atom of the board, some cells are spawnpoint, the otherone are just normal cells which
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
    public Cell(@NotNull Color color, @NotNull Bounds bounds, boolean spawnPoint) {
        this.color = color;
        this.bounds = bounds;
        this.spawnPoint = spawnPoint;
    }

    /**
     * Indicate if a certain cell is a spawnpoint.
     *
     * @return True if the cell is a spawnpoint.
     */
    public boolean isSpawnPoint() {
        return spawnPoint;
    }

    /**
     * Indicate if a certain cell has an ammo card on it.
     *
     * @return True if there is an ammo card.
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
    public @NotNull Bounds getBounds() {
        return bounds;
    }

    /**
     * Color enum.
     */
    public enum Color {
        /**
         * White color.
         */
        WHITE,
        /**
         * Blue color.
         */
        BLUE,
        /**
         * Red color.
         */
        RED,
        /**
         * Purple color.
         */
        PURPLE,
        /**
         * Yellow color.
         */
        YELLOW,
        /**
         * Green color.
         */
        GREEN
    }
}