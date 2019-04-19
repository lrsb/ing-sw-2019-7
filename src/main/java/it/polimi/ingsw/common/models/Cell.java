package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Cell class, each cell is the atom of the board, some cells are spawnpoint, the otherone are just normal cells which
 * have an ammo card on it, more cells compose a room and more rooms compose the game board.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Cell implements Serializable {
    private Color color;
    private boolean spawnPoint;
    private Bounds bounds;
    private AmmoCard ammoCard;

    @Contract(pure = true)
    private Cell(@NotNull Bounds bounds) {
        this.bounds = bounds;
    }

    @Contract(pure = true)
    private Cell(Color color, boolean spawnPoint, Bounds bounds, AmmoCard ammoCard) {
        this.color = color;
        this.spawnPoint = spawnPoint;
        this.bounds = bounds;
        this.ammoCard = ammoCard;
    }

    /**
     * Indicate if a certain cell is a spawnpoint.
     * @return True if the cell is a spawnpoint.
     */
    public boolean isSpawnPoint() {
        return spawnPoint;
    }

    /**
     * Indicate if a certain cell has an ammo card on it.
     * @return True if there is an ammo card.
     */
    public AmmoCard getAmmoCard() {
        return ammoCard;
    }

    /**
     * Set a new ammo card on a cell.
     * @param ammoCard The new ammo card that you want to assign to the cell.
     */
    public void setAmmoCard(AmmoCard ammoCard) {
        this.ammoCard = ammoCard;
    }

    /**
     * Get the color of the cell.
     * @return The cell's color.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Get the bounds of the cell.
     * @return the cell's bounds.
     */
    public Bounds getBounds() {
        return bounds;
    }

    public enum Color {
        WHITE, BLUE, RED, PURPLE, YELLOW, GREEN
    }

    public static class Creator {
        private Cell cell;

        //"nesw" in senso orario, _ : chiuso, | : porta,   : stessa stanza
        public static Creator withBounds(@NotNull String boundsString) {
            var creator = new Creator();
            var bounds = Bounds.Creator.withType(Bounds.Type.SAME_ROOM);
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
                }
                switch (boundsString.charAt(index)) {
                    case '_':
                        bounds.setType(direction, Bounds.Type.WALL);
                        break;
                    case '|':
                        bounds.setType(direction, Bounds.Type.DOOR);
                        break;
                    case ' ':
                        bounds.setType(direction, Bounds.Type.SAME_ROOM);
                }
            }
            creator.cell = new Cell(bounds);
            return creator;
        }

        public Creator color(Color color) {
            cell.color = color;
            return this;
        }

        public Creator spawnPoint(boolean isSpawnPoint) {
            cell.spawnPoint = isSpawnPoint;
            return this;
        }

        public Creator ammoCard(AmmoCard ammoCard) {
            cell.ammoCard = ammoCard;
            return this;
        }

        public Cell create() {
            return cell;
        }
    }
}