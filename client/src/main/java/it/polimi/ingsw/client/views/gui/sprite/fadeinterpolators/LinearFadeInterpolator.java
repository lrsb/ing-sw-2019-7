package it.polimi.ingsw.client.views.gui.sprite.fadeinterpolators;

public abstract class LinearFadeInterpolator extends FadeInterpolator {
    public LinearFadeInterpolator(double start, double end, long duration) {
        super(start, System.currentTimeMillis(), end, System.currentTimeMillis() + duration);
    }

    @Override
    public double interpolateImpl(float normalizedCompletion) {
        return start + normalizedCompletion * (end - start);
    }
}