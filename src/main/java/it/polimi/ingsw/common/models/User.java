package it.polimi.ingsw.common.models;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private UUID uuid = UUID.randomUUID();
    private String nickname;

    public User(String nickname) {
        this.nickname = nickname;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getNickname() {
        return nickname;
    }
}