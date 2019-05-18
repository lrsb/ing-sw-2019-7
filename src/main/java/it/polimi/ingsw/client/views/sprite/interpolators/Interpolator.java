package it.polimi.ingsw.client.views.sprite.interpolators;

import it.polimi.ingsw.client.views.sprite.interpolators.exceptions.TimestampOutOfRangeException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * The type Interpolator.
 */
public abstract class Interpolator {
    /**
     * The Start.
     */
    protected final @NotNull Point start;
    /**
     * The Start millis.
     */
    protected final long startMillis;
    /**
     * The End.
     */
    protected final @NotNull Point end;
    /**
     * The End millis.
     */
    protected final long endMillis;

    /**
     * Instantiates a new Interpolator.
     *
     * @param start       the start
     * @param startMillis the start millis
     * @param end         the end
     * @param endMillis   the end millis
     */
    @Contract(pure = true)
    public Interpolator(@NotNull Point start, long startMillis, @NotNull Point end, long endMillis) {
        this.start = start;
        this.startMillis = startMillis;
        this.end = end;
        this.endMillis = endMillis;
    }

    /**
     * Interpolate point.
     *
     * @param timestamp the timestamp
     * @return the point
     * @throws TimestampOutOfRangeException the timestamp out of range exception
     */
    public final @NotNull Point interpolate(long timestamp) throws TimestampOutOfRangeException {
        checkTimestamp(timestamp);
        return interpolateImpl((float) (timestamp - startMillis) / (endMillis - startMillis));
    }

    /**
     * Gets end point.
     *
     * @return the end point
     */
    public @NotNull Point getEndPoint() {
        return end;
    }

    /**
     * Gets end millis.
     *
     * @return the end millis
     */
    public long getEndMillis() {
        return endMillis;
    }

    /**
     * Interpolate point.
     *
     * @param normalizedCompletion the normalized completion
     * @return the point
     */
    public abstract @NotNull Point interpolateImpl(float normalizedCompletion);

    /**
     * On interpolation completed.
     */
    public void onInterpolationCompleted() {
    }

    private void checkTimestamp(long timestamp) throws TimestampOutOfRangeException {
        if (timestamp < startMillis || endMillis < timestamp)
            throw new TimestampOutOfRangeException("Not valid: " + startMillis + " < " + timestamp + " < " + endMillis);
    }
}