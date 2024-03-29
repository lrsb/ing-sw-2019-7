package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The place where users wait to begin a new {@link Game}.
 */
public class Room implements Serializable {
    private static final long serialVersionUID = 1;

    private @NotNull UUID uuid = UUID.randomUUID();
    private @NotNull String name;
    private @NotNull ArrayList<User> users = new ArrayList<>();
    private boolean gameCreated = false;
    private int actionTimeout = 30;
    private int skulls;
    private Game.Type gameType;
    private String startTime;

    /**
     * Create a new room.
     *
     * @param name    The room's name.
     * @param creator The {@link User} that created this room.
     */
    public Room(@NotNull String name, @NotNull User creator) {
        SecureRandom rand = new SecureRandom();
        this.name = name;
        this.users.add(creator);
        this.skulls = rand.nextInt(5) + 3;
        this.gameType = Game.Type.values()[rand.nextInt(Game.Type.values().length)];
    }

    /**
     * Get the timestamp when the game starts.
     *
     * @return The timestamp when the game starts.
     */
    public long getStartTime() {
        return Long.parseLong(startTime);
    }

    /**
     * Set the timestamp when the game starts.
     *
     * @param startType The timestamp when the game starts.
     */
    public void setStartTime(long startType) {
        this.startTime = Long.toString(startType);
    }

    /**
     * Return the {@link UUID} of the room.
     *
     * @return The {@link UUID} of the room.
     */
    public @NotNull UUID getUuid() {
        return uuid;
    }

    /**
     * Return the name of the room.
     *
     * @return The name of the room.
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Gets a {@link List} of the users in this room.
     *
     * @return A {@link List} of the users in this room.
     */
    public @NotNull List<User> getUsers() {
        return users;
    }

    /**
     * Add a {@link User} to this room.
     *
     * @param user The {@link User} to add.
     * @return true if there is enough space for the user, otherwise false.
     */
    public boolean addUser(@NotNull User user) {
        if (users.contains(user)) return true;
        if (users.size() > 4) return false;
        users.add(user);
        return true;
    }

    /**
     * Remove a user
     *
     * @param user the user that you want to remove
     */
    public void removeUser(@NotNull User user) {
        users.remove(user);
    }

    /**
     * This room was converted into a {@link Game}?
     *
     * @return true if this room was converted into a {@link Game}.
     */
    public boolean isGameCreated() {
        return gameCreated;
    }

    /**
     * Set this room was converted into a {@link Game}.
     */
    public void setGameCreated() {
        gameCreated = true;
    }

    /**
     * Gets action timeout. Is the max delay between actions in game.
     *
     * @return the action timeout
     */
    public int getActionTimeout() {
        return actionTimeout;
    }

    /**
     * Sets action timeout.
     *
     * @param actionTimeout the action timeout
     */
    public void setActionTimeout(int actionTimeout) {
        if (actionTimeout >= 60 && actionTimeout <= 120) this.actionTimeout = actionTimeout;
    }

    /**
     * Gets game type.
     *
     * @return the game type
     */
    public Game.Type getGameType() {
        return gameType;
    }

    /**
     * Sets game type.
     *
     * @param gameType the game type
     */
    public void setGameType(Game.Type gameType) {
        this.gameType = gameType;
    }

    /**
     * Gets game's parameter "number of skulls"
     *
     * @return skulls number
     */
    public int getSkulls() {
        return skulls;
    }

    /**
     * Sets the game's parameter "number of skulls"
     *
     * @param skulls the number of skulls
     */
    public void setSkulls(int skulls) {
        if (skulls >= 5 && skulls <= 8) this.skulls = skulls;
        else if (skulls > 8) this.skulls = 8;
        else this.skulls = 5;
    }
}