package it.polimi.ingsw.common.models;

import it.polimi.ingsw.client.others.Utils;
import it.polimi.ingsw.client.views.gui.sprite.Displayable;
import it.polimi.ingsw.common.models.exceptions.PlayerNotFoundException;
import it.polimi.ingsw.common.models.exceptions.SelfResponseException;
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
 * This class contains the information that a player needs to know the state of the game
 */
public class Game implements Displayable, Serializable {
    public static final int MAX_PLAYERS = 5;
    public static final int MIN_PLAYERS = 3;
    public static final int MAX_X = 4;
    public static final int MAX_Y = 3;
    private static final long serialVersionUID = 1;
    protected final @NotNull UUID uuid;
    protected final @NotNull Cell[][] cells;
    protected final @NotNull Type type;
    protected final @NotNull ArrayList<Player> players = new ArrayList<>();
    protected final @NotNull ArrayList<UUID> lastsDamaged = new ArrayList<>();
    protected int seqPlay = 0;

    protected @NotNull ArrayList<UUID> responsivePlayers = new ArrayList<>();

    protected int skulls;
    protected int maxSkulls; //cos'è?
    protected @NotNull ArrayList<UUID> arrayKillshotsTrack = new ArrayList<>();
    protected boolean isCompleted = false;
    protected boolean lastTurn = false;
    protected ArrayList<Weapon> redWeapons;
    protected ArrayList<Weapon> blueWeapons;
    protected ArrayList<Weapon> yellowWeapons;
    private @NotNull HashMap<UUID, Integer> hashKillshotsTrack = new HashMap<>();

    public Game() {
        uuid = UUID.randomUUID();
        cells = new Cell[MAX_X][MAX_Y];
        type = Type.FIVE_FIVE;
    }

    protected Game(@NotNull UUID uuid, @NotNull Type type, @NotNull Cell[][] cells, @NotNull List<Player> players, int maxSkulls) {
        this.uuid = uuid;
        this.type = type;
        this.cells = cells;
        this.players.addAll(players);
        this.maxSkulls = maxSkulls;
    }

    public int getMaxSkulls() {
        return maxSkulls;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * This method says if the action being processing is referred to a player that has to response
     *
     * @return true if the action being processing must be a possible response with a tagback grenade
     */
    public boolean isATagbackResponse() {
        return getTagbackPlayers().contains(getActualPlayer().getUuid());
    }

    /**
     * @return true if the action that is being processing must be referred to a player that has to respawn
     */
    public boolean isAReborn() {
        return !isATagbackResponse() && responsivePlayers.contains(getActualPlayer().getUuid());
    }

    public @NotNull UUID getUuid() {
        return uuid;
    }

    public @NotNull Cell[][] getCells() {
        return cells;
    }

    public @Nullable Cell getCell(@Nullable Point point) {
        if (point == null || point.x < 0 || point.x >= cells.length || point.y < 0 || point.y >= cells[point.x].length)
            return null;
        return cells[point.x][point.y];
    }

    public @NotNull Type getType() {
        return type;
    }

    public @NotNull ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * @return the player that must do the action that is being processing
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

    /**
     * Add the player to a list when this takes a damage and the list doesn't already contains the player
     *
     * @param player the player just hit
     */
    public void addToLastsDamaged(@NotNull Player player) {
        if (!lastsDamaged.contains(player.getUuid())) lastsDamaged.add(player.getUuid());
    }

    public @NotNull ArrayList<UUID> getLastsDamaged() {
        return lastsDamaged;
    }

    /**
     * @return an ArrayList with UUID of the players hit with a tagback grenade
     */
    public @NotNull ArrayList<UUID> getTagbackPlayers() {
        ArrayList<UUID> tagbackPlayers = new ArrayList<>();
        lastsDamaged.parallelStream().filter(e -> players.parallelStream().anyMatch(f -> f.getUuid().equals(e) &&
                Stream.of(AmmoCard.Color.values()).anyMatch(g -> f.hasPowerUp(new PowerUp(g, PowerUp.Type.TAGBACK_GRENADE))))).forEach(tagbackPlayers::add);
        return tagbackPlayers;
    }

    @NotNull Player getTagbackedPlayer() {
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
     * @return true if is the first turn of the current player
     */
    //non basterebbe dire che seqPlay < players.size()
    public boolean isFirstMove() {
        return getActualPlayer().isFirstMove();
    }

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
     * Takes names of the weapons in a certain spawnpoint cell
     *
     * @param color the color of a spawnpoint cell
     * @return ArrayList of weapon names of the spawnpoint cell with color "color"
     */
    public @NotNull ArrayList<Weapon> getWeapons(@NotNull Cell.Color color) {
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

    protected void addWeapon(@NotNull Cell.Color color, @NotNull Weapon weapon) {
        switch (color) {
            case BLUE:
                if (blueWeapons.size() < 3) blueWeapons.add(weapon);
                break;
            case RED:
                if (redWeapons.size() < 3) redWeapons.add(weapon);
                break;
            case YELLOW:
                if (yellowWeapons.size() < 3) yellowWeapons.add(weapon);
        }
    }

    protected void removeWeapon(@NotNull Cell.Color color, @NotNull Weapon weapon) {
        switch (color) {
            case BLUE:
                blueWeapons.remove(weapon);
                break;
            case RED:
                redWeapons.remove(weapon);
                break;
            case YELLOW:
                yellowWeapons.remove(weapon);
        }
    }

    /**
     * Determines if a player can move from "from" to "to" with "maxStep" steps
     *
     * @param from start point
     * @param to end point
     * @param maxStep usable steps
     *
     * @return true if "to" is attainable from "from" point with at most "maxStep" steps, false otherwise
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
     * Defines the different maps that players can choose as battlefield
     */
    public enum Type {
        FIVE_FIVE("L5", "R5"),
        FIVE_SIX("L5", "R6"),
        SIX_FIVE("L6", "R5"),
        SIX_SIX("L6", "R6");

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