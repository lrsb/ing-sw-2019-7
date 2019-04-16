package it.polimi.ingsw.client.views.interpolator;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class LinearInterpolator extends Interpolator {
    public LinearInterpolator(@NotNull Point start, long startMillis, @NotNull Point end, long duration) {
        super(start, startMillis, end, startMillis + duration);
    }

    @Override
    public @NotNull Point interpolateImpl(float normalizedCompletion) {
        return new Point(start.x + (int) ((end.x - start.x) * normalizedCompletion), start.y + (int) ((end.y - start.y) * normalizedCompletion));
    }
}