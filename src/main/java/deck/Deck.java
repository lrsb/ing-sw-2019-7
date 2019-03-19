package deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * It is the super type to manage several deck.Deck type: deck.CharacterDeck,
 * DangerousSectorDeck, EscapeHatchDeck and ItemDeck.
 *
 * @author Luca Stornaiuolo
 * @version 1.0
 */
public abstract class Deck {

    /**
     * cardList is the list of card whence a Player can draw a deck.Card; cardList
     * related to a certain deck.Deck.
     */
    private List<Card> cardList;

    /**
     * discardPile is the list of card where a drawn and used deck.Card could be put;
     * discardPile related to a certain deck.Deck.
     */
    private List<Card> discardPile;

    /**
     * Construct a deck.Deck without initializing any attributes.
     */
    protected Deck() {
        cardList = new ArrayList<Card>();
        discardPile = new ArrayList<Card>();
    }

    /**
     * addCard allows to add a deck.Card to the cardList of a certain deck.Deck.
     *
     * @param card
     */
    protected void addCard(Card card) {
        this.cardList.add(card);
    }

    /**
     * deckSuffle allows to shuffle the cardList of a certain deck.Deck.
     */
    public void deckShuffle() {

        Collections.shuffle(this.cardList);
    }

    /**
     * Getter to obtain the cardList of a deck.Deck.
     *
     * @return cardList whence a Player can draw a deck.Card.
     */
    public List<Card> getCardList() {
        return cardList;
    }

    /**
     * Getter to obtain the discardPile of a deck.Deck.
     *
     * @return cardList where a Player can discard a used deck.Card.
     */
    public List<Card> getDiscardPile() {
        return discardPile;
    }

    /**
     * isEmpty allows to know if the cardList has size()==0.
     *
     * @return true (if a list of deck.Card is empty), false (otherwise).
     */
    public boolean isEmpty() {
        return this.cardList.isEmpty();
    }

    /**
     * drawCard allows to draw a deck.Card from a cardList of a certain deck.Deck.
     *
     * @return drawnCard that is the deck.Card has just drawn.
     */
    public Card drawCard() {

        if (this.getCardList().isEmpty())
            return null;

        return this.getCardList().remove(this.getCardList().size() - 1);

    }

    /**
     * discardCard allows to put a used deck.Card onto the discardPile of a certain
     * deck.Deck.
     *
     * @param card that is the deck.Card has just discarded.
     */
    public void discardCard(Card card) {

        this.getDiscardPile().add(card);
    }

    /**
     * reloadDeck allows to add all the Cards of the discardPile to the cardList
     * of a certain deck.Deck. Then it shuffles cardList.
     */
    public void reloadDeck() {

        for (Card currentCard : this.getDiscardPile()) {

            this.getCardList().add(currentCard);

        }
        this.getDiscardPile().clear();
        this.deckShuffle();

    }

}
