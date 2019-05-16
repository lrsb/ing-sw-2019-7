package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

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
    private @Nullable ArrayList<Weapon.Name> weapons;

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
        if (spawnPoint) { this.weapons = new ArrayList<>(); this.ammoCard = null;}
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

    public ArrayList<Weapon.Name> getWeapons() {
        return weapons;
    }

    public boolean addWeapon(Weapon.Name weapon) {
        if (isSpawnPoint() && weapons.size() < 3) {
            this.weapons.add(weapon);
            return true;
        }
        return false;
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
        WHITE, BLUE, RED, PURPLE, YELLOW, GREEN
    }
}