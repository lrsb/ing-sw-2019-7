package it.polimi.ingsw.server.models;

import it.polimi.ingsw.common.models.AmmoCard;
import it.polimi.ingsw.server.models.exceptions.EmptyDeckException;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    @RepeatedTest(value = 100)
    void testDeck() {
        var random = new SecureRandom();
        var list = Collections.nCopies(random.nextInt(1000) + 1, null).parallelStream().map(e ->
                new AmmoCard(AmmoCard.Type.values()[random.nextInt(AmmoCard.Type.values().length)],
                        AmmoCard.Color.values()[random.nextInt(AmmoCard.Color.values().length)],
                        AmmoCard.Color.values()[random.nextInt(AmmoCard.Color.values().length)])).collect(Collectors.toList());
        var deck = new Deck<>(list, true);
        for (int i = 0; i < list.size() + 1; i++) deck.discardCard(deck.exitCard());
        deck.discardCard();
        deck.exitCards(random.nextInt(list.size()));
        var deck1 = new Deck<>(list, false);
        deck1.exitCards(list.size()).forEach(deck1::discardCard);
        assertThrows(EmptyDeckException.class, deck1::exitCard);
        assertThrows(EmptyDeckException.class, deck1::discardCard);
        var deck2 = new Deck<>(list, false);
        deck2.discardCard();
        deck2.exitCards(random.nextInt(list.size()));
        assertThrows(InvalidParameterException.class, () -> deck2.exitCards(list.size() + 1));
    }

    @Test
    void testAmmoDeck() {
        var ammoDeck = Deck.Creator.newAmmoDeck();
        ammoDeck.exitCards(ammoDeck.remainedCards()).parallelStream().forEach(e -> {
            assertDoesNotThrow(e::getFrontImage);
            assertDoesNotThrow(e::getBackImage);
            try {
                assertNotEquals(e.getFrontImage(), null);
                assertNotEquals(e.getBackImage(), null);
            } catch (IOException ex) {
                ex.printStackTrace();
                fail();
            }
        });
    }

    @Test
    void testPowerUpDeck() {
        var powerUpDeck = Deck.Creator.newPowerUpsDeck();
        powerUpDeck.exitCards(powerUpDeck.remainedCards()).parallelStream().forEach(e -> {
            assertDoesNotThrow(e::getFrontImage);
            assertDoesNotThrow(e::getBackImage);
            try {
                assertNotEquals(e.getFrontImage(), null);
                assertNotEquals(e.getBackImage(), null);
            } catch (IOException ex) {
                ex.printStackTrace();
                fail();
            }
        });
    }

    @Test
    void testWeaponDeck() {
        var weaponsDeck = Deck.Creator.newWeaponsDeck();
        weaponsDeck.exitCards(weaponsDeck.remainedCards()).parallelStream().forEach(e -> {
            assertDoesNotThrow(e::getFrontImage);
            assertDoesNotThrow(e::getBackImage);
            try {
                assertNotEquals(e.getFrontImage(), null);
                assertNotEquals(e.getBackImage(), null);
            } catch (IOException ex) {
                ex.printStackTrace();
                fail();
            }
        });
    }
}