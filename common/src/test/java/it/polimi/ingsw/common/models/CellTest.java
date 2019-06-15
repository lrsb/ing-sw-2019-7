package it.polimi.ingsw.common.models;

import org.junit.jupiter.api.RepeatedTest;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;

public class CellTest {

    @RepeatedTest(value = 100)
    void testCell() {
        SecureRandom rand = new SecureRandom();
        Cell cell = Cell.Creator.withBounds("_ | ").color(Cell.Color.values()[rand.nextInt(Cell.Color.values().length)]).spawnPoint().create();
        Cell cell1 = Cell.Creator.withBounds("|  _").color(Cell.Color.values()[rand.nextInt(Cell.Color.values().length)]).create();
        Cell cell2 = Cell.Creator.withBounds("||||").color(Cell.Color.values()[rand.nextInt(Cell.Color.values().length)]).spawnPoint().create();
        AmmoCard ammoCard = new AmmoCard(AmmoCard.Type.values()[rand.nextInt(AmmoCard.Type.values().length)],
                AmmoCard.Color.values()[rand.nextInt(AmmoCard.Color.values().length)],
                AmmoCard.Color.values()[rand.nextInt(AmmoCard.Color.values().length)]);
        assertTrue(cell != null && cell1 != null && cell2 != null);
        cell1.setAmmoCard(ammoCard);
        assertNull(cell.getAmmoCard());
        assertNull(cell2.getAmmoCard());
        assertNotNull(cell1.getAmmoCard());
        assertNotNull(cell.getBounds());
        assertNotNull(cell.getColor());
        cell1.removeAmmoCard();
        assertNull(cell1.getAmmoCard());
        assertFalse(cell1.isSpawnPoint());
        assertTrue(cell.isSpawnPoint());
    }
}
