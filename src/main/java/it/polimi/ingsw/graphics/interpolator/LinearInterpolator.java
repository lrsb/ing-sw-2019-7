package it.polimi.ingsw.graphics.interpolator;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class LinearInterpolator extends Interpolator {
    public LinearInterpolator(@NotNull Point start, long startMillis, @NotNull Point end, long duration) {
        super(start, startMillis, end, startMillis + duration);
    }

    @Override
    public @NotNull Point interpolateImpl(float normalizedCompletion) {
        return new Point((int) ((end.x - start.x) * normalizedCompletion), (int) ((end.y - start.y) * normalizedCompletion));
    }
}