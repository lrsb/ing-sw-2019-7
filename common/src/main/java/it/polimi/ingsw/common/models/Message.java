package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Message implements Comparable<Message> {
    private @NotNull User from;
    private @NotNull UUID gameUuid;
    private @NotNull String message;
    private long timestamp;

    @Contract(pure = true)
    public Message(@NotNull User from, @NotNull UUID gameUuid, @NotNull String message, long timestamp) {
        this.from = from;
        this.gameUuid = gameUuid;
        this.message = message;
        this.timestamp = timestamp;
    }

    public @NotNull User getFrom() {
        return from;
    }

    public @NotNull UUID getGameUuid() {
        return gameUuid;
    }

    public @NotNull String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(@NotNull Message o) {
        return (int) (timestamp - o.getTimestamp());
    }
}