package it.polimi.ingsw.server.models;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class DeckTest {
    @Test
    void testAmmoDeck() throws IOException {
        var ammoDeck = Deck.Creator.newAmmoDeck();
        var ammoCards = ammoDeck.exitCards(ammoDeck.remainedCards());
        for (var ammoCard : ammoCards) {
            assertDoesNotThrow(ammoCard::getImage);
            assertNotEquals(ammoCard.getImage(), null);
        }
    }
}