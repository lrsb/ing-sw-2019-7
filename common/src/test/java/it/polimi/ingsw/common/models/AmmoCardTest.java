package it.polimi.ingsw.common.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AmmoCardTest {
    @Test
    void testAllAmmoCard() {
        for (var type : AmmoCard.Type.values())
            for (var left : AmmoCard.Color.values()) {
                switch (left) {
                    case RED:
                        assertEquals(0, left.getIndex());
                        break;
                    case YELLOW:
                        assertEquals(1, left.getIndex());
                        break;
                    case BLUE:
                        assertEquals(2, left.getIndex());
                        break;
                }
                for (var right : AmmoCard.Color.values()) {
                    switch (right) {
                        case RED:
                            assertEquals(0, right.getIndex());
                            break;
                        case YELLOW:
                            assertEquals(1, right.getIndex());
                            break;
                        case BLUE:
                            assertEquals(2, right.getIndex());
                            break;
                    }
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
                    var ammoCardCopy = new AmmoCard(ammoCard);
                    assertEquals(ammoCard, ammoCardCopy);
                }
            }
    }
}