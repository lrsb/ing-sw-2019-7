package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Room implements Serializable {
    private static final long serialVersionUID = 1;

    private @NotNull UUID uuid = UUID.randomUUID();
    private @NotNull String name;
    private @NotNull ArrayList<User> users = new ArrayList<>();

    public Room(@NotNull String name) {
        this.name = name;
    }

    public @NotNull UUID getUuid() {
        return uuid;
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull List<User> getUsers() {
        return users;
    }

    public boolean addUser(@NotNull User user) {
        if (users.size() > 4) return false;
        users.add(user);
        return true;
    }
}