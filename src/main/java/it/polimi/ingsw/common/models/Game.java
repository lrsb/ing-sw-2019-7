package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Game implements Serializable {
    public static final int MAX_PLAYERS = 5;
    public static final int MIN_PLAYERS = 3;

    protected static final int MAX_X = 4;
    protected static final int MAX_Y = 3;

    protected final @NotNull UUID uuid;
    protected final @NotNull Cell[][] cells;
    protected final @NotNull Type type;
    protected final @NotNull ArrayList<Player> players = new ArrayList<>();
    protected int seqPlay = 0;

    protected int skulls = 5;

    //the game has to remember who gave killshots
    protected ArrayList<String> killshots;

    protected ArrayList<Weapon.Name> redWeapons;
    protected ArrayList<Weapon.Name> blueWeapons;
    protected ArrayList<Weapon.Name> yellowWeapons;

    protected Game(@NotNull UUID uuid, @NotNull Type type, @NotNull Cell[][] cells, @NotNull List<Player> players) {
        this.uuid = uuid;
        this.type = type;
        this.cells = cells;
        this.players.addAll(players);
    }

    public @NotNull UUID getUuid() {
        return uuid;
    }

    public @NotNull Cell[][] getCells() {
        return cells;
    }

    public @NotNull Type getType() {
        return type;
    }

    public @NotNull ArrayList<Player> getPlayers() {
        return players;
    }

    public @NotNull Player getActualPlayer() {
        return players.get(seqPlay);
    }

    public boolean isFirstMove() {
        return getActualPlayer().isFirstMove();
    }

    public enum Type {
        FIVE_FIVE("L5", "R5"), FIVE_SIX("L5", "R6"), SIX_FIVE("L6", "R5"), SIX_SIX("L6", "R6");

        private @NotNull String left;
        private @NotNull String right;

        @Contract(pure = true)
        Type(@NotNull String left, @NotNull String right) {
            this.left = left;
            this.right = right;
        }

        @Contract(pure = true)
        public @NotNull String getLeft() {
            return left;
        }

        @Contract(pure = true)
        public @NotNull String getRight() {
            return right;
        }
    }
}