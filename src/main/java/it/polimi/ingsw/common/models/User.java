package it.polimi.ingsw.common.models;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private UUID uuid = UUID.randomUUID();
    private String nickname;
    private transient String token;
    private transient String password;

    public User(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getNickname() {
        return nickname;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }
}