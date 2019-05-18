package it.polimi.ingsw.client.views.sprite.interpolators;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * The type Linear interpolator.
 */
public abstract class LinearInterpolator extends Interpolator {
    /**
     * Instantiates a new Linear interpolator.
     *
     * @param start    the start
     * @param end      the end
     * @param duration the duration
     */
    public LinearInterpolator(@NotNull Point start, @NotNull Point end, long duration) {
        super(start, System.currentTimeMillis(), end, System.currentTimeMillis() + duration);
    }

    @Override
    public @NotNull Point interpolateImpl(float normalizedCompletion) {
        return new Point(start.x + (int) ((end.x - start.x) * normalizedCompletion), start.y + (int) ((end.y - start.y) * normalizedCompletion));
    }
}