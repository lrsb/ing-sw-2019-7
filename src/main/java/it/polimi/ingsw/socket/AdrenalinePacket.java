package it.polimi.ingsw.socket;

import org.jetbrains.annotations.Contract;

public class AdrenalinePacket {
    private Type type;
    private String jsonObject;

    @Contract(pure = true)
    public AdrenalinePacket(Type type, String jsonObject) {
        this.type = type;
        this.jsonObject = jsonObject;
    }

    public Type getType() {
        return type;
    }

    public String getAssociatedJsonObject() {
        return jsonObject;
    }

    public enum Type {
        REQUEST_GAMES_LIST, JOIN_GAME, RESPONSE
    }
}
