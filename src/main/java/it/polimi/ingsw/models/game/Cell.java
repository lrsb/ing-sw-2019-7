package it.polimi.ingsw.models.game;

import it.polimi.ingsw.models.cards.AmmoCard;
import org.jetbrains.annotations.NotNull;

public class Cell {
    private Color color;
    private boolean spawnPoint;
    private Bounds bounds;
    private AmmoCard ammoCard;

    public boolean isSpawnPoint() {
        return spawnPoint;
    }

    public AmmoCard getAmmoCard() {
        return ammoCard;
    }

    public void setAmmoCard(AmmoCard ammoCard) {
        this.ammoCard = ammoCard;
    }

    enum Color {
        WHITE, BLUE, RED, PURPLE, YELLOW, GREEN
    }

    public static class Creator {
        private Cell cell;

        //"nesw" in senso orario, _ : chiuso, | : porta,   : stessa stanza
        public static Creator withBounds(@NotNull String boundsString) {
            var creator = new Creator();
            creator.cell = new Cell();
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
            creator.cell.bounds = bounds;
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