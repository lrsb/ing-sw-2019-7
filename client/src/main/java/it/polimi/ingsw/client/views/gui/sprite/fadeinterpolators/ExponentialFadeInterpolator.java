package it.polimi.ingsw.client.views.gui.sprite.fadeinterpolators;

public abstract class ExponentialFadeInterpolator extends FadeInterpolator {
    public ExponentialFadeInterpolator(double start, double end, long duration) {
        super(start, System.currentTimeMillis(), end, System.currentTimeMillis() + duration);
    }

    @Override
    public double interpolateImpl(float normalizedCompletion) {
        return start + Math.exp(normalizedCompletion * 5 - 4) / Math.E * (end - start);
    }
}
