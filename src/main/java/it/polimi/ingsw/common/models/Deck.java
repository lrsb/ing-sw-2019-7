package it.polimi.ingsw.common.models;

import it.polimi.ingsw.common.models.exceptions.EmptyDeckException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is composed of three deck that contains the playable, exited ( on the game board or in players' hand)
 * and discarded (already used and need to be reshuffled) cards.
 * @param <T> Indicate the type of cards in the deck.
 */
public class Deck<T> {
    private @NotNull ArrayList<T> playableCards = new ArrayList<>();
    private @NotNull ArrayList<T> exitedCards = new ArrayList<>();
    private @NotNull ArrayList<T> discardedCards = new ArrayList<>();
    private boolean shufflable;

    private Deck(@NotNull ArrayList<T> cards, boolean shufflable) {
        playableCards.addAll(cards);
        this.shufflable = shufflable;
    }

    /**
     * This method move a card from the playable deck to the discarded one.
     * @return The discarded card.
     * @throws EmptyDeckException Thrown when there are no more available cards, and the deck is not shufflable.
     */
    public @NotNull T discardCard() throws EmptyDeckException {
        if (playableCards.isEmpty()) shuffleDeck();
        var discardedCard = playableCards.remove(0);
        discardedCards.add(discardedCard);
        return discardedCard;
    }

    /**
     * This method is used to remove a card from playable deck and
     * (ex: replacement of card that are on the ground)
     * @return Name of exited card
     * @throws EmptyDeckException Thrown when there are no more available cards, and the deck is not shufflable.
     */
    public @NotNull T exitCard() throws EmptyDeckException {
        if (playableCards.isEmpty()) shuffleDeck();
        var exitedCard = playableCards.remove(0);
        exitedCards.add(exitedCard);
        return exitedCard;
    }

    /**
     *This method is used when more cards are removed from playable card deck adding them to the exited one
     * @param n Number of card that you have to remove from the deck.
     * @return List of card removed from the deck.
     * @throws EmptyDeckException Thrown when there are no more available cards, and the deck is not shufflable.
     */
    public @NotNull List<T> exitCards(int n) throws EmptyDeckException {
        if (playableCards.isEmpty()) shuffleDeck();
        var list = new ArrayList<T>();
        for (int i = 0; i < n; i++) {
            var exitedCard = playableCards.remove(0);
            list.add(exitedCard);
        }
        exitedCards.addAll(list);
        return list;
    }

    /**This method is used when an exited card is used, it will be added to discarded card deck
     *
     * @param exitedCard Name of the card that has been used.
     */
    public void discardCard(@NotNull T exitedCard) {
        if (exitedCards.indexOf(exitedCard) >= 0) exitedCards.remove(exitedCard);
        discardedCards.add(exitedCard);
    }

    private void shuffleDeck() throws EmptyDeckException {
        if (!shufflable) throw new EmptyDeckException();
        playableCards.addAll(discardedCards);
        discardedCards.clear();
        Collections.shuffle(playableCards);
    }

    public static class Creator {
        @Contract(pure = true)
        private Creator() {
        }

        //TODO: implementare tutto quanto
        @Contract(" -> new")
        public static @NotNull Deck<AmmoCard> newAmmoDeck() {
            return new Deck<>(new ArrayList<>(), true);
        }

        @Contract(" -> new")
        public static @NotNull Deck<PowerUp> newPowerUpsDeck() {
            return new Deck<>(new ArrayList<>(), true);
        }

        @Contract(" -> new")
        public static @NotNull Deck<Weapon> newWeaponsDeck() {
            return new Deck<>(new ArrayList<>(), true);
        }
    }
}