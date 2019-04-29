package it.polimi.ingsw.server.models;

import it.polimi.ingsw.common.models.AmmoCard;
import it.polimi.ingsw.common.models.PowerUp;
import it.polimi.ingsw.common.models.Weapon;
import it.polimi.ingsw.server.models.exceptions.CardNotFoundException;
import it.polimi.ingsw.server.models.exceptions.EmptyDeckException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class is composed of three deck that contains the playable, exited ( on the game board or in players' hand)
 * and discarded (already used and need to be reshuffled) cards.
 *
 * @param <T> Indicate the type of cards in the deck.
 */
@SuppressWarnings({"WeakerAccess", "SpellCheckingInspection", "unused"})
public class Deck<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 1;

    private final @NotNull ArrayList<T> playableCards = new ArrayList<>();
    private final @NotNull ArrayList<T> exitedCards = new ArrayList<>();
    private final @NotNull ArrayList<T> discardedCards = new ArrayList<>();
    private final boolean shuffleable;

    /**
     * Create a new Deck.
     *
     * @param cards       The cards included in the deck.
     * @param shuffleable true if the deck is shuffleable when finished.
     * @throws EmptyDeckException Thrown when {@code cards} is empty.
     */
    private Deck(@NotNull ArrayList<T> cards, boolean shuffleable) throws EmptyDeckException {
        if (cards.isEmpty()) throw new EmptyDeckException();
        this.playableCards.addAll(cards);
        this.shuffleable = shuffleable;
    }

    /**
     * This method moves a card from the playable deck to the discarded one.
     *
     * @return The discarded card.
     * @throws EmptyDeckException Thrown when there are no more available cards, and the deck is not shuffleable.
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
     *
     * @return The exited card
     * @throws EmptyDeckException Thrown when there are no more available cards, and the deck is not shufflable.
     */
    public @NotNull T exitCard() throws EmptyDeckException {
        if (playableCards.isEmpty()) shuffleDeck();
        var exitedCard = playableCards.remove(0);
        exitedCards.add(exitedCard);
        return exitedCard;
    }

    /**
     * This method is used when more cards are removed from playable card deck adding them to the exited one
     *
     * @param n Number of cards that you have to remove from the deck.
     * @return List of card removed from the deck.
     * @throws EmptyDeckException Thrown when there are no more available cards, and the deck is not shuffleable.
     */
    public @NotNull List<T> exitCards(int n) throws EmptyDeckException {
        if (playableCards.size() < n) if (shuffleable) shuffleDeck();
        else throw new EmptyDeckException();
        var list = new ArrayList<T>();
        for (int i = 0; i < n; i++) list.add(playableCards.remove(0));
        exitedCards.addAll(list);
        return list;
    }

    /**
     * This method is used when an exited card is used, it will be added to discarded card deck.
     *
     * @param exitedCard The card that has been used.
     * @throws CardNotFoundException Thrown when specified card isn't in the group of the exited.
     */
    public void discardCard(@NotNull T exitedCard) throws CardNotFoundException {
        if (exitedCards.indexOf(exitedCard) > -1) {
            exitedCards.remove(exitedCard);
            discardedCards.add(exitedCard);
        } else throw new CardNotFoundException();
    }

    /**
     * Returns the number of playable cards.
     *
     * @return The number of playable cards.
     */
    @Contract(pure = true)
    public int remainedCards() {
        return playableCards.size();
    }

    private void shuffleDeck() throws EmptyDeckException {
        if (!shuffleable) throw new EmptyDeckException();
        playableCards.addAll(discardedCards);
        discardedCards.clear();
        Collections.shuffle(playableCards);
    }

    public static class Creator {
        @Contract(pure = true)
        private Creator() {
        }

        @Contract(" -> new")
        public static @NotNull Deck<AmmoCard> newAmmoDeck() {
            return new Deck<>(new ArrayList<>(Arrays.asList(
                    new AmmoCard(AmmoCard.Type.YELLOW, AmmoCard.Color.BLUE, AmmoCard.Color.BLUE),
                    new AmmoCard(AmmoCard.Type.YELLOW, AmmoCard.Color.RED, AmmoCard.Color.RED),
                    new AmmoCard(AmmoCard.Type.RED, AmmoCard.Color.BLUE, AmmoCard.Color.BLUE),
                    new AmmoCard(AmmoCard.Type.RED, AmmoCard.Color.YELLOW, AmmoCard.Color.YELLOW),
                    new AmmoCard(AmmoCard.Type.BLUE, AmmoCard.Color.YELLOW, AmmoCard.Color.YELLOW),
                    new AmmoCard(AmmoCard.Type.BLUE, AmmoCard.Color.RED, AmmoCard.Color.RED),
                    new AmmoCard(AmmoCard.Type.YELLOW, AmmoCard.Color.BLUE, AmmoCard.Color.BLUE),
                    new AmmoCard(AmmoCard.Type.YELLOW, AmmoCard.Color.RED, AmmoCard.Color.RED),
                    new AmmoCard(AmmoCard.Type.RED, AmmoCard.Color.BLUE, AmmoCard.Color.BLUE),
                    new AmmoCard(AmmoCard.Type.RED, AmmoCard.Color.YELLOW, AmmoCard.Color.YELLOW),
                    new AmmoCard(AmmoCard.Type.BLUE, AmmoCard.Color.YELLOW, AmmoCard.Color.YELLOW),
                    new AmmoCard(AmmoCard.Type.BLUE, AmmoCard.Color.RED, AmmoCard.Color.RED),
                    new AmmoCard(AmmoCard.Type.YELLOW, AmmoCard.Color.BLUE, AmmoCard.Color.BLUE),
                    new AmmoCard(AmmoCard.Type.YELLOW, AmmoCard.Color.RED, AmmoCard.Color.RED),
                    new AmmoCard(AmmoCard.Type.RED, AmmoCard.Color.BLUE, AmmoCard.Color.BLUE),
                    new AmmoCard(AmmoCard.Type.RED, AmmoCard.Color.YELLOW, AmmoCard.Color.YELLOW),
                    new AmmoCard(AmmoCard.Type.BLUE, AmmoCard.Color.YELLOW, AmmoCard.Color.YELLOW),
                    new AmmoCard(AmmoCard.Type.BLUE, AmmoCard.Color.RED, AmmoCard.Color.RED),
                    new AmmoCard(AmmoCard.Type.POWER_UP, AmmoCard.Color.YELLOW, AmmoCard.Color.YELLOW),
                    new AmmoCard(AmmoCard.Type.POWER_UP, AmmoCard.Color.RED, AmmoCard.Color.RED),
                    new AmmoCard(AmmoCard.Type.POWER_UP, AmmoCard.Color.BLUE, AmmoCard.Color.BLUE),
                    new AmmoCard(AmmoCard.Type.POWER_UP, AmmoCard.Color.YELLOW, AmmoCard.Color.RED),
                    new AmmoCard(AmmoCard.Type.POWER_UP, AmmoCard.Color.YELLOW, AmmoCard.Color.BLUE),
                    new AmmoCard(AmmoCard.Type.POWER_UP, AmmoCard.Color.RED, AmmoCard.Color.BLUE),
                    new AmmoCard(AmmoCard.Type.POWER_UP, AmmoCard.Color.YELLOW, AmmoCard.Color.YELLOW),
                    new AmmoCard(AmmoCard.Type.POWER_UP, AmmoCard.Color.RED, AmmoCard.Color.RED),
                    new AmmoCard(AmmoCard.Type.POWER_UP, AmmoCard.Color.BLUE, AmmoCard.Color.BLUE),
                    new AmmoCard(AmmoCard.Type.POWER_UP, AmmoCard.Color.YELLOW, AmmoCard.Color.RED),
                    new AmmoCard(AmmoCard.Type.POWER_UP, AmmoCard.Color.YELLOW, AmmoCard.Color.BLUE),
                    new AmmoCard(AmmoCard.Type.POWER_UP, AmmoCard.Color.RED, AmmoCard.Color.BLUE))), true);
        }

        //TODO: impl
        @Contract(" -> new")
        public static @NotNull Deck<PowerUp> newPowerUpsDeck() {
            return new Deck<>(new ArrayList<>(), true);
        }

        @Contract(" -> new")
        public static @NotNull Deck<Weapon.Name> newWeaponsDeck() {
            return new Deck<>(new ArrayList<>(List.of(Weapon.Name.values())), false);
        }
    }
}