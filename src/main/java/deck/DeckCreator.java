package deck;

/**
 * This deck.DeckCreator realizes a factoryMethod on the object deck.Deck. It is the super
 * type of all DeckCreators to manage several deck.Deck type creation:
 * deck.CharacterDeckCreator, DangerousSectorDeckCreator, EscapeHatchDeckCreator and
 * ItemDeckCreator, using the method createDeck().
 *
 * @author Luca Stornaiuolo
 * @version 1.0
 */

public abstract class DeckCreator {
    /**
     * Creates a creator for a generic deck.Deck.
     */
    protected DeckCreator() {
        super();
    }

    /**
     * This method allows to create a new object deck.Deck and then to populate it.
     */
    public abstract Deck createDeck();
}
