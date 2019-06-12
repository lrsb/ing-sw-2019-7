package it.polimi.ingsw.client.views.gui.sprite.pointinterpolators;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class LinearPointInterpolator extends PointInterpolator {
    public LinearPointInterpolator(@NotNull Point start, @NotNull Point end, long duration) {
        super(start, System.currentTimeMillis(), end, System.currentTimeMillis() + duration);
    }

    @Override
    public @NotNull Point interpolateImpl(float normalizedCompletion) {
        return new Point(start.x + (int) ((end.x - start.x) * normalizedCompletion), start.y + (int) ((end.y - start.y) * normalizedCompletion));
    }
}