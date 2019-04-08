package it.polimi.ingsw.client.views.interpolator;

import it.polimi.ingsw.client.views.interpolator.exceptions.TimestampOutOfRangeException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class Interpolator {
    protected @NotNull Point start;
    protected long startMillis;
    protected @NotNull Point end;
    protected long endMillis;

    @Contract(pure = true)
    public Interpolator(@NotNull Point start, long startMillis, @NotNull Point end, long endMillis) {
        this.start = start;
        this.startMillis = startMillis;
        this.end = end;
        this.endMillis = endMillis;
    }

    public @NotNull Point interpolate(long timestamp) throws TimestampOutOfRangeException {
        checkTimestamp(timestamp);
        return interpolateImpl((float) (timestamp - startMillis) / (endMillis - startMillis));
    }

    public @NotNull Point getEndPoint() {
        return end;
    }

    public long getEndMillis() {
        return endMillis;
    }

    public abstract @NotNull Point interpolateImpl(float normalizedCompletion);

    public void onInterpolationCompleted() {
    }

    private void checkTimestamp(long timestamp) throws TimestampOutOfRangeException {
        if (timestamp < startMillis || endMillis < timestamp)
            throw new TimestampOutOfRangeException("Not valid: " + startMillis + " < " + timestamp + " < " + endMillis);
    }
}