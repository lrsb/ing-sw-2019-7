package it.polimi.ingsw.graphics.interpolator;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class ExponentialInterpolator extends Interpolator {
    public ExponentialInterpolator(@NotNull Point start, long startMillis, @NotNull Point end, long duration) {
        super(start, startMillis, end, startMillis + duration);
    }

    @Override
    public @NotNull Point interpolateImpl(float normalizedCompletion) {
        var completion = Math.exp(normalizedCompletion * 5 - 4) / Math.E;
        return new Point((int) ((end.x - start.x) * completion), (int) ((end.y - start.y) * completion));
    }
}
