package it.polimi.ingsw.common.models;

import it.polimi.ingsw.client.others.Utils;
import it.polimi.ingsw.client.views.gui.sprite.Displayable;
import it.polimi.ingsw.common.models.modelsExceptions.PlayerNotFoundException;
import it.polimi.ingsw.common.models.modelsExceptions.SelfResponseException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Game.
 */
public class Game implements Displayable, Serializable {
    /**
     * The constant MAX_PLAYERS.
     */
    public static final int MAX_PLAYERS = 5;
    /**
     * The constant MIN_PLAYERS.
     */
    public static final int MIN_PLAYERS = 3;
    /**
     * The constant MAX_X.
     */
    protected static final int MAX_X = 4;
    /**
     * The constant MAX_Y.
     */
    protected static final int MAX_Y = 3;
    private static final long serialVersionUID = 1;
    /**
     * The Uuid.
     */
    protected final @NotNull UUID uuid;
    /**
     * The Cells.
     */
    protected final @NotNull Cell[][] cells;
    /**
     * The Type.
     */
    protected final @NotNull Type type;
    /**
     * The Players.
     */
    protected final @NotNull ArrayList<Player> players = new ArrayList<>();
    /**
     * The Lasts damaged.
     */
    protected final @NotNull ArrayList<UUID> lastsDamaged = new ArrayList<>();
    /**
     * The Seq play.
     */
    protected int seqPlay = 0;

    protected @NotNull ArrayList<UUID> responsivePlayers = new ArrayList<>();

    /**
     * The Skulls.
     */
    protected int skulls;
    protected @NotNull ArrayList<UUID> arrayKillshotsTrack = new ArrayList<>();

    protected boolean isCompleted = false;
    /**
     * The Last turn.
     */
//aggiunto perchè non basta che skulls == 0
    protected boolean lastTurn = false;
    protected ArrayList<Weapon.Name> redWeapons;
    protected ArrayList<Weapon.Name> blueWeapons;
    protected ArrayList<Weapon.Name> yellowWeapons;
    private @NotNull HashMap<UUID, Integer> hashKillshotsTrack = new HashMap<>();

    public Game() {
        uuid = UUID.randomUUID();
        cells = new Cell[MAX_X][MAX_Y];
        type = Type.FIVE_FIVE;
    }

    protected Game(@NotNull UUID uuid, @NotNull Type type, @NotNull Cell[][] cells, @NotNull List<Player> players, int skulls) {
        this.uuid = uuid;
        this.type = type;
        this.cells = cells;
        this.players.addAll(players);
        this.skulls = skulls;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isATagbackResponse() {
        return getTagbackPlayers().contains(getActualPlayer().getUuid());
    }

    public boolean isAReborn() {
        return !isATagbackResponse() && responsivePlayers.contains(getActualPlayer().getUuid());
    }

    /**
     * Gets uuid.
     *
     * @return the uuid
     */
    public @NotNull UUID getUuid() {
        return uuid;
    }

    /**
     * Get cells cell [ ] [ ].
     *
     * @return the cell [ ] [ ]
     */
    public @NotNull Cell[][] getCells() {
        return cells;
    }

    /**
     * Gets cell.
     *
     * @param point the point
     * @return the cell
     */
    public @Nullable Cell getCell(@Nullable Point point) {
        if (point == null || point.x < 0 || point.x >= cells.length || point.y < 0 || point.y >= cells[point.x].length)
            return null;
        return cells[point.x][point.y];
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public @NotNull Type getType() {
        return type;
    }

    /**
     * Gets players.
     *
     * @return the players
     */
    public @NotNull ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Gets actual player.
     *
     * @return the actual player
     */
    public @NotNull Player getActualPlayer() {
        if (responsivePlayers.isEmpty()) return players.get(seqPlay % players.size());
        else if (responsivePlayers.contains(players.get(seqPlay % players.size()).getUuid()))
            throw new SelfResponseException();
        else for (Player player : players) {
                if (player.getUuid().equals(responsivePlayers.get(0))) return player;
            }
        throw new PlayerNotFoundException();
    }

    public int getSkulls() {
        return skulls;
    }

    public void addToLastsDamaged(@NotNull Player player) {
        if (!lastsDamaged.contains(player.getUuid())) lastsDamaged.add(player.getUuid());
    }

    /**
     * Gets lasts damaged.
     *
     * @return the lasts damaged
     */
    public @NotNull ArrayList<UUID> getLastsDamaged() {
        return lastsDamaged;
    }

    /**
     * Gets tagback players.
     *
     * @return the tagback players
     */
    public @NotNull ArrayList<UUID> getTagbackPlayers() {
        ArrayList<UUID> tagbackPlayers = new ArrayList<>();
        lastsDamaged.parallelStream().filter(e -> players.parallelStream().anyMatch(f -> f.getUuid().equals(e) &&
                Stream.of(AmmoCard.Color.values()).anyMatch(g -> f.hasPowerUp(new PowerUp(g, PowerUp.Type.TAGBACK_GRENADE))))).forEach(tagbackPlayers::add);
        return tagbackPlayers;
    }

    public @NotNull Player getTagbackedPlayer() {
        if (getActualPlayer().equals(players.get(seqPlay % players.size()))) throw new SelfResponseException();
        return players.get(seqPlay % players.size());
    }

    protected void addTagbackPlayers() {
        responsivePlayers.addAll(getTagbackPlayers());
    }

    protected void addReborningPlayers() {
        responsivePlayers.addAll(getDeadPlayers());
    }

    /**
     * Is first move boolean.
     *
     * @return the boolean
     */
    public boolean isFirstMove() {
        return getActualPlayer().isFirstMove();
    }

    /**
     * Gets dead players.
     *
     * @return the dead players
     */
    protected ArrayList<UUID> getDeadPlayers() {
        ArrayList<UUID> deadPlayers = new ArrayList<>();
        getPlayers().parallelStream().filter(e -> e.getDamagesTaken().size() >= 11).map(Player::getUuid).forEach(deadPlayers::add);
        return deadPlayers;
    }

    protected void addToKillshotsTrack(UUID uuid) {
        if (!hashKillshotsTrack.containsKey(uuid)) {
            hashKillshotsTrack.put(uuid, 1);
            arrayKillshotsTrack.add(uuid);
        } else hashKillshotsTrack.put(uuid, hashKillshotsTrack.get(uuid) + 1);
    }

    protected @NotNull ArrayList<UUID> getSortedKillshooters() {
        for (int i = 0; i < arrayKillshotsTrack.size() - 1; i++) {
            for (int j = i + 1; j < arrayKillshotsTrack.size(); j++) {
                if (hashKillshotsTrack.get(arrayKillshotsTrack.get(i)) < hashKillshotsTrack.get(arrayKillshotsTrack.get(j))) {
                    var tmp = arrayKillshotsTrack.get(i);
                    arrayKillshotsTrack.set(i, arrayKillshotsTrack.get(j));
                    arrayKillshotsTrack.set(j, tmp);
                }
            }
        }
        return arrayKillshotsTrack;
    }

    protected int getPlayerKillshots(@NotNull UUID uuid) {
        return hashKillshotsTrack.get(uuid);
    }

    /**
     * Gets weapons.
     *
     * @param color the color
     * @return the weapons
     */
    @NotNull
    public ArrayList<Weapon.Name> getWeapons(@NotNull Cell.Color color) {
        switch (color) {
            case BLUE:
                return blueWeapons;
            case RED:
                return redWeapons;
            case YELLOW:
                return yellowWeapons;
            default:
                return new ArrayList<>();
        }
    }

    protected void addWeapon(@NotNull Cell.Color color, @NotNull Weapon.Name weapon) {
        switch (color) {
            case BLUE:
                if (blueWeapons.size() < 3) blueWeapons.add(weapon);
            case RED:
                if (redWeapons.size() < 3) redWeapons.add(weapon);
            case YELLOW:
                if (yellowWeapons.size() < 3) yellowWeapons.add(weapon);
        }
    }

    protected void removeWeapon(@NotNull Cell.Color color, @NotNull Weapon.Name weapon) {
        switch (color) {
            case BLUE:
                blueWeapons.remove(weapon);
            case RED:
                redWeapons.remove(weapon);
            case YELLOW:
                yellowWeapons.remove(weapon);
        }
    }

    /**
     * Can move boolean.
     *
     * @param from    the from
     * @param to      the to
     * @param maxStep the max step
     * @return the boolean
     */
    @Contract(pure = true)
    public boolean canMove(@Nullable Point from, @Nullable Point to, int maxStep) {
        return from != null && to != null && canMoveImpl(from, to, 0, maxStep);
    }

    @Contract(pure = true)
    private boolean canMoveImpl(@NotNull Point from, @NotNull Point to, int step, int maxStep) {
        if (from.x < 0 || from.x >= cells.length || from.y < 0 || from.y >= cells[from.x].length ||
                to.x < 0 || to.x >= cells.length || to.y < 0 || to.y >= cells[to.x].length || step >= maxStep || step < 0)
            return false;
        if (from.equals(to)) return true;
        return Stream.of(Bounds.Direction.values()).anyMatch(e -> cells[from.x][from.y].getBounds().getType(e) != Bounds.Type.WALL &&
                canMoveImpl(new Point(from.x + e.getdX(), to.y + e.getdY()), to, step + 1, maxStep));
    }

    /**
     * Gets players at position.
     *
     * @param point the point
     * @return the players at position
     */
    public @NotNull List<Player> getPlayersAtPosition(@NotNull Point point) {
        return players.parallelStream().filter(e -> e.getPosition() != null).filter(e -> e.getPosition().equals(point)).collect(Collectors.toList());
    }

    @Override
    public @NotNull BufferedImage getBackImage() throws IOException {
        return Utils.readJpgImage(Game.class, getType().getLeft());
    }

    @Override
    public @NotNull BufferedImage getFrontImage() throws IOException {
        return Utils.readJpgImage(Game.class, getType().getRight());
    }

    /**
     * The enum Type.
     */
    public enum Type {
        /**
         * Five five type.
         */
        FIVE_FIVE("L5", "R5"),
        /**
         * Five six type.
         */
        FIVE_SIX("L5", "R6"),
        /**
         * Six five type.
         */
        SIX_FIVE("L6", "R5"),
        /**
         * Six six type.
         */
        SIX_SIX("L6", "R6");

        private @NotNull String left;
        private @NotNull String right;

        @Contract(pure = true)
        Type(@NotNull String left, @NotNull String right) {
            this.left = left;
            this.right = right;
        }

        /**
         * Gets left.
         *
         * @return the left
         */
        @Contract(pure = true)
        public @NotNull String getLeft() {
            return left;
        }

        /**
         * Gets right.
         *
         * @return the right
         */
        @Contract(pure = true)
        public @NotNull String getRight() {
            return right;
        }
    }
}