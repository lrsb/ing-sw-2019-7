package deck;

/**
 * This deck.CharacterDeckCreator realizes a factoryMethod on the object
 * deck.CharacterDeck using the method createDeck().
 *
 * @author Luca Stornaiuolo
 * @version 1.0
 */

public class CharacterDeckCreator extends DeckCreator {
    /**
     * Creates a creator for a deck.CharacterDeck.
     */
    public CharacterDeckCreator() {
        super();
    }

    /**
     * This method effects an Overloading and allows to create a new object
     * deck.CharacterDeck and then to populate it in order to the specific number of
     * Player: - if numberPlayer is an even number then the number of HumanCards
     * are equals to the number of AlienCards - if numberPlayer is an odd number
     * then the number of AlienCards in will exceed the number of HumanCards by
     * one.
     *
     * @param numberPlayer that is
     * @return d that is the new shuffled deck.CharacterDeck that has been realized
     * using the factoryMethod.
     */
    public CharacterDeck createDeck(int numberPlayer) {

        CharacterDeck d = new CharacterDeck();

        for (int i = 0; i < numberPlayer / 2; i++) {
            CharacterCard c = new HumanCard();
            d.addCard(c);
        }
        for (int i = 0; i < numberPlayer / 2 + numberPlayer % 2; i++) {
            CharacterCard c = new AlienCard();
            d.addCard(c);
        }
        d.deckShuffle();

        return d;
    }

    /**
     * This method allows to create a new object deck.CharacterDeck and then to
     * populate it with: 4 deck.HumanCard; 4 deck.AlienCard.
     *
     * @return d that is the new shuffled deck.CharacterDeck that has been realized
     * using the factoryMethod.
     */
    @Override
    public CharacterDeck createDeck() {

        CharacterDeck d = new CharacterDeck();

        for (int i = 0; i < 4; i++) {
            CharacterCard c = new HumanCard();
            d.addCard(c);
        }
        for (int i = 0; i < 4; i++) {
            CharacterCard c = new AlienCard();
            d.addCard(c);
        }
        d.deckShuffle();

        return d;
    }

}
