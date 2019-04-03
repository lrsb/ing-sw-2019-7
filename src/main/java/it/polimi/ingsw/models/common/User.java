package it.polimi.ingsw.models.common;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private UUID uuid = UUID.randomUUID();
    private String nickName;

    @Contract(pure = true)
    public User(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public UUID getUuid() {
        return uuid;
    }
}
