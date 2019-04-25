package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The place where users wait to begin a new {@link Game} .
 */
public class Room implements Serializable {
    private static final long serialVersionUID = 1;

    private @NotNull UUID uuid = UUID.randomUUID();
    private @NotNull String name;
    private @NotNull ArrayList<User> users = new ArrayList<>();
    private boolean gameCreated = false;

    /**
     * Create a new room.
     *
     * @param name    The name of the room.
     * @param creator The {@link User} that created this room.
     */
    public Room(@NotNull String name, @NotNull User creator) {
        this.name = name;
        this.users.add(creator);
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
     * Get a {@link List} of the users in this room.
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
     * @return true if there was enough space for the user, otherwise false.
     */
    public boolean addUser(@NotNull User user) {
        if (users.size() > 4) return false;
        users.add(user);
        return true;
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
}