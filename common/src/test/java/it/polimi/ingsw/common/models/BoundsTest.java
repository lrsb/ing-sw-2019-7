package it.polimi.ingsw.common.models;

import org.junit.jupiter.api.RepeatedTest;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;

class BoundsTest {

    @RepeatedTest(value = 100)
    void testBounds() {
        SecureRandom rand = new SecureRandom();
        Bounds bounds = new Bounds(Bounds.Type.values()[rand.nextInt(Bounds.Type.values().length)],
                Bounds.Type.values()[rand.nextInt(Bounds.Type.values().length)],
                Bounds.Type.values()[rand.nextInt(Bounds.Type.values().length)],
                Bounds.Type.values()[rand.nextInt(Bounds.Type.values().length)]);
        for (Bounds.Direction dir : Bounds.Direction.values()) {
            assertNotNull(bounds.getType(dir));
            bounds.setType(dir, Bounds.Type.values()[rand.nextInt(Bounds.Type.values().length)]);
            assertNotNull(bounds.getType(dir));
            switch (dir) {
                case N:
                    assertEquals(-1, dir.getdX());
                    assertEquals(0, dir.getdY());
                    break;
                case E:
                    assertEquals(0, dir.getdX());
                    assertEquals(1, dir.getdY());
                    break;
                case S:
                    assertEquals(1, dir.getdX());
                    assertEquals(0, dir.getdY());
                    break;
                case W:
                    assertEquals(0, dir.getdX());
                    assertEquals(-1, dir.getdY());
                    break;
            }
        }

    }
}
