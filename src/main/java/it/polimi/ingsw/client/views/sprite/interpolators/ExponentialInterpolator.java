package it.polimi.ingsw.client.views.sprite.interpolators;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * The type Exponential interpolator.
 */
public abstract class ExponentialInterpolator extends Interpolator {
    /**
     * Instantiates a new Exponential interpolator.
     *
     * @param start    the start
     * @param end      the end
     * @param duration the duration
     */
    public ExponentialInterpolator(@NotNull Point start, @NotNull Point end, long duration) {
        super(start, System.currentTimeMillis(), end, System.currentTimeMillis() + duration);
    }

    @Override
    public @NotNull Point interpolateImpl(float normalizedCompletion) {
        var completion = Math.exp(normalizedCompletion * 5 - 4) / Math.E;
        return new Point(start.x + (int) ((end.x - start.x) * completion), start.y + (int) ((end.y - start.y) * completion));
    }
}
