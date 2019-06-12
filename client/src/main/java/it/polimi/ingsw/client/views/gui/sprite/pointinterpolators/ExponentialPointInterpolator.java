package it.polimi.ingsw.client.views.gui.sprite.pointinterpolators;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class ExponentialPointInterpolator extends PointInterpolator {
    public ExponentialPointInterpolator(@NotNull Point start, @NotNull Point end, long duration) {
        super(start, System.currentTimeMillis(), end, System.currentTimeMillis() + duration);
    }

    @Override
    public @NotNull Point interpolateImpl(float normalizedCompletion) {
        var completion = Math.exp(normalizedCompletion * 5 - 4) / Math.E;
        return new Point(start.x + (int) ((end.x - start.x) * completion), start.y + (int) ((end.y - start.y) * completion));
    }
}
