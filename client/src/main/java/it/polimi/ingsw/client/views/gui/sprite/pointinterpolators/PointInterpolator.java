package it.polimi.ingsw.client.views.gui.sprite.pointinterpolators;

import it.polimi.ingsw.client.views.gui.sprite.exceptions.TimestampOutOfRangeException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class PointInterpolator {
    protected final @NotNull Point start;
    protected final long startMillis;
    protected final @NotNull Point end;
    protected final long endMillis;

    @Contract(pure = true)
    public PointInterpolator(@NotNull Point start, long startMillis, @NotNull Point end, long endMillis) {
        this.start = start;
        this.startMillis = startMillis;
        this.end = end;
        this.endMillis = endMillis;
    }

    public final @NotNull Point interpolate(long timestamp) throws TimestampOutOfRangeException {
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