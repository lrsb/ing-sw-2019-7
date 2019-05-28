package it.polimi.ingsw.client.views.gui.sprite.fadeinterpolators;

import it.polimi.ingsw.client.views.gui.sprite.exceptions.TimestampOutOfRangeException;
import org.jetbrains.annotations.Contract;

public abstract class FadeInterpolator {
    protected final double start;
    protected final long startMillis;
    protected final double end;
    protected final long endMillis;

    @Contract(pure = true)
    public FadeInterpolator(double start, long startMillis, double end, long endMillis) {
        this.start = start;
        this.startMillis = startMillis;
        this.end = end;
        this.endMillis = endMillis;
    }

    public final double interpolate(long timestamp) throws TimestampOutOfRangeException {
        checkTimestamp(timestamp);
        return interpolateImpl((float) (timestamp - startMillis) / (endMillis - startMillis));
    }

    public double getEndFade() {
        return end;
    }

    public long getEndMillis() {
        return endMillis;
    }

    public abstract double interpolateImpl(float normalizedCompletion);

    public void onInterpolationCompleted() {
    }

    private void checkTimestamp(long timestamp) throws TimestampOutOfRangeException {
        if (timestamp < startMillis || endMillis < timestamp)
            throw new TimestampOutOfRangeException("Not valid: " + startMillis + " < " + timestamp + " < " + endMillis);
    }
}