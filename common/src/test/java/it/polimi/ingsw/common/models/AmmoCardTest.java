package it.polimi.ingsw.common.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class AmmoCardTest {
    @Test
    void testAllAmmoCard() {
        for (var type : AmmoCard.Type.values())
            for (var left : AmmoCard.Color.values())
                for (var right : AmmoCard.Color.values()) {
                    left.getIndex();
                    right.getIndex();
                    var ammoCard = new AmmoCard(type, left, right);
                    assertEquals(ammoCard.getType(), type);
                    assertEquals(ammoCard.getLeft(), left);
                    assertEquals(ammoCard.getRight(), right);
                    assertEquals(ammoCard, new AmmoCard(type, left, right));
                    for (var type1 : AmmoCard.Type.values())
                        for (var left1 : AmmoCard.Color.values())
                            for (var right1 : AmmoCard.Color.values())
                                if (type == type1 && left == left1 && right == right1)
                                    assertEquals(ammoCard, new AmmoCard(type1, left1, right1));
                                else assertNotEquals(ammoCard, new AmmoCard(type1, left1, right1));
                    assertNotEquals(ammoCard, new Object());
                }
    }
}