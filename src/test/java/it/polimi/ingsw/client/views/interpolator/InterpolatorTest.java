package it.polimi.ingsw.client.views.interpolator;

import it.polimi.ingsw.client.views.interpolator.exceptions.TimestampOutOfRangeException;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class InterpolatorTest {
    @Test
    void testAllInterpolators() throws TimestampOutOfRangeException {
        var linear = new LinearInterpolator(new Point(0, 0), 0, new Point(100, 100), 1000) {
            @Override
            public void onInterpolationCompleted() {

            }
        };
        var exponential = new LinearInterpolator(new Point(0, 0), 0, new Point(100, 100), 1000) {
            @Override
            public void onInterpolationCompleted() {

            }
        };
        try {
            linear.interpolate(1001);
            fail();
        } catch (TimestampOutOfRangeException ignored) {
        }
        try {
            exponential.interpolate(1001);
            fail();
        } catch (TimestampOutOfRangeException ignored) {
        }
        assertTrue(linear.interpolateImpl(1).equals(new Point(100, 100)) &&
                linear.interpolate(1000).equals(new Point(100, 100)) &&
                exponential.interpolateImpl(1).equals(new Point(100, 100)) &&
                exponential.interpolate(1000).equals(new Point(100, 100)));
    }

}