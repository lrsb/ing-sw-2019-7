package it.polimi.ingsw.common.models;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AmmoCardTest {

    @Test
    void testAllAmmoCard() {
        for (var type : AmmoCard.Type.values())
            for (var left : AmmoCard.Color.values())
                for (var right : AmmoCard.Color.values()) {
                    var ammoCard = new AmmoCard(type, left, right);
                    assertEquals(ammoCard.getType(), type);
                    assertEquals(ammoCard.getLeft(), left);
                    assertEquals(ammoCard.getRight(), right);
                    assertEquals(ammoCard, new AmmoCard(type, left, right));
                    var otherType = Stream.of(AmmoCard.Type.values()).filter(e -> e != type).findAny();
                    var otherLeft = Stream.of(AmmoCard.Color.values()).filter(e -> e != left).findAny();
                    var otherRight = Stream.of(AmmoCard.Color.values()).filter(e -> e != right).findAny();
                    if (otherType.isEmpty() || otherLeft.isEmpty() || otherRight.isEmpty()) fail();
                    assertNotEquals(ammoCard, new AmmoCard(otherType.get(), otherLeft.get(), otherRight.get()));
                }
    }
}