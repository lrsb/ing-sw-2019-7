package deck;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeckTest {

    @Test
    public void testConstructorCharacterDeck() {

        int numAlien = 0;
        int numHuman = 0;

        // testing the CharacterDesk_constructor in case of a generic number n
        // of player
        // between 2 and 8
        for (int n = 2; n < 8; n++) {
            CharacterDeckCreator creatorC_nPlayer = new CharacterDeckCreator();
            CharacterDeck deckC_nPlayer = creatorC_nPlayer.createDeck(n);

            for (Card card : deckC_nPlayer.getCardList()) {
                if (card instanceof AlienCard)
                    numAlien++;
                if (card instanceof HumanCard)
                    numHuman++;
            }
            assertTrue(n == (numAlien + numHuman));
            numAlien = 0;
            numHuman = 0;
        }
    }

}
