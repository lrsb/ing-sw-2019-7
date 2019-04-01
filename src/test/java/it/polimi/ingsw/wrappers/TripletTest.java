package it.polimi.ingsw.wrappers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class TripletTest {
    @Test
    void getFirst() {
        var object = new Object();
        var triplet = new Triplet<>(object, null, null);
        assertSame(triplet.getFirst(), object);
    }

    @Test
    void getSecond() {
        var object = new Object();
        var triplet = new Triplet<>(null, object, null);
        assertSame(triplet.getSecond(), object);
    }

    @Test
    void getThird() {
        var object = new Object();
        var triplet = new Triplet<>(null, null, object);
        assertSame(triplet.getThird(), object);
    }
}