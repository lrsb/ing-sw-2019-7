package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * In game message wrapper class.
 */
public class Message implements Comparable<Message> {
    private @NotNull User from;
    private @NotNull UUID gameUuid;
    private @NotNull String message;
    private long timestamp;

    /**
     * Instantiates a new Message.
     *
     * @param from      the from
     * @param gameUuid  the game uuid
     * @param message   the message
     * @param timestamp the timestamp
     */
    @Contract(pure = true)
    public Message(@NotNull User from, @NotNull UUID gameUuid, @NotNull String message, long timestamp) {
        this.from = from;
        this.gameUuid = gameUuid;
        this.message = message;
        this.timestamp = timestamp;
    }

    /**
     * Gets from {@link User}.
     *
     * @return the from {@link User}.
     */
    public @NotNull User getFrom() {
        return from;
    }

    /**
     * Gets game uuid.
     *
     * @return the game uuid
     */
    public @NotNull UUID getGameUuid() {
        return gameUuid;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public @NotNull String getMessage() {
        return message;
    }

    /**
     * Gets timestamp.
     *
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(@NotNull Message o) {
        return (int) (timestamp - o.getTimestamp());
    }
}