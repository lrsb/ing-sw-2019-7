package it.polimi.ingsw.common.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Room implements Serializable {
    private UUID uuid = UUID.randomUUID();
    private String name;
    private ArrayList<User> users = new ArrayList<>();

    public Room(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public List<User> getUsers() {
        return users;
    }

    public boolean addUser(User user) {
        if (users.size() > 4) return false;
        users.add(user);
        return true;
    }
}