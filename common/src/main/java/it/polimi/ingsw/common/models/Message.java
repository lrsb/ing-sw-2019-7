package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.UUID;

/**
 * In game message wrapper class.
 */
public class Message implements Comparable<Message>, Serializable {
    private static final long serialVersionUID = 1;

    private @NotNull User from;
    private @NotNull UUID uuid;
    private @NotNull String message;
    private long timestamp;

    /**
     * Instantiates a new Message.
     *
     * @param from      the from
     * @param uuid   room or game uuid
     * @param message   the message
     * @param timestamp the timestamp
     */
    @Contract(pure = true)
    public Message(@NotNull User from, @NotNull UUID uuid, @NotNull String message, long timestamp) {
        this.from = from;
        this.uuid = uuid;
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
     * Gets room or game uuid.
     *
     * @return the room or game uuid
     */
    public @NotNull UUID getUuid() {
        return uuid;
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