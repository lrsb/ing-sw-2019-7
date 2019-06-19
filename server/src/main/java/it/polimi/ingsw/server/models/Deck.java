package it.polimi.ingsw.server.models;

import it.polimi.ingsw.common.models.AmmoCard;
import it.polimi.ingsw.common.models.PowerUp;
import it.polimi.ingsw.common.models.Weapon;
import it.polimi.ingsw.server.models.exceptions.CardNotFoundException;
import it.polimi.ingsw.server.models.exceptions.EmptyDeckException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is composed of three decks that contains the playable, exited ( on the game board or in players' hand)
 * and discarded (already used and need to be reshuffled) cards.
 *
 * @param <T> Indicates the type of cards in the deck.
 */
@SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
public class Deck<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 1;

    private final @NotNull ArrayList<T> playableCards = new ArrayList<>();
    private final @NotNull ArrayList<T> exitedCards = new ArrayList<>();
    private final @NotNull ArrayList<T> discardedCards = new ArrayList<>();
    private final boolean shuffleable;

    /**
     * Creates a new Deck.
     *
     * @param cards       The cards included in the deck.
     * @param shuffleable true if the deck is shuffleable when finished.
     * @throws EmptyDeckException Thrown when {@code cards} is empty.
     */
    Deck(@NotNull List<T> cards, boolean shuffleable) {
        if (cards.isEmpty()) throw new EmptyDeckException();
        this.playableCards.addAll(cards);
        this.shuffleable = shuffleable;
        Collections.shuffle(playableCards, new SecureRandom());
    }

    /**
     * This method moves a card from the playable deck to the discarded one.
     *
     * @return The discarded card.
     * @throws EmptyDeckException Thrown when there are no more available cards, and the deck is not shuffleable.
     */
    public @NotNull T discardCard() {
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
    public @NotNull T exitCard() {
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
    public @NotNull List<T> exitCards(int n) {
        if (playableCards.size() + discardedCards.size() < n) throw new InvalidParameterException();
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
    public void discardCard(@NotNull T exitedCard) {
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

    private void shuffleDeck() {
        if (!shuffleable) throw new EmptyDeckException();
        playableCards.addAll(discardedCards);
        discardedCards.clear();
        Collections.shuffle(playableCards, new SecureRandom());
    }

    /**
     * Creator.
     */
    public static class Creator {
        @Contract(pure = true)
        private Creator() {
        }

        /**
         * New ammo deck deck.
         *
         * @return the deck
         */
        @Contract(" -> new")
        public static @NotNull Deck<AmmoCard> newAmmoDeck() {
            var cards = Stream.of(AmmoCard.Type.values()).map(e -> Stream.of(AmmoCard.Color.values()).filter(f -> !e.name().equals(f.name()))
                    .map(f -> new AmmoCard(e, f, f))).flatMap(e -> e).collect(Collectors.toCollection(ArrayList::new));
            cards.addAll(Stream.of(AmmoCard.Type.values()).map(e -> Stream.of(AmmoCard.Color.values()).filter(f -> !e.name().equals(f.name()))
                    .map(f -> new AmmoCard(e, f, f))).flatMap(e -> e).collect(Collectors.toList()));
            cards.addAll(Stream.of(AmmoCard.Type.values()).filter(e -> !e.equals(AmmoCard.Type.POWER_UP)).map(e -> Stream.of(AmmoCard.Color.values())
                    .filter(f -> !e.name().equals(f.name())).map(f -> new AmmoCard(e, f, f))).flatMap(e -> e).collect(Collectors.toList()));
            for (int i = 0; i < 4; i++)
                cards.add(new AmmoCard(AmmoCard.Type.POWER_UP, AmmoCard.Color.RED, AmmoCard.Color.BLUE));
            for (int i = 0; i < 4; i++)
                cards.add(new AmmoCard(AmmoCard.Type.POWER_UP, AmmoCard.Color.YELLOW, AmmoCard.Color.BLUE));
            for (int i = 0; i < 4; i++)
                cards.add(new AmmoCard(AmmoCard.Type.POWER_UP, AmmoCard.Color.YELLOW, AmmoCard.Color.BLUE));
            return new Deck<>(cards, true);
        }

        /**
         * New power ups deck deck.
         *
         * @return the deck
         */
        @Contract(" -> new")
        public static @NotNull Deck<PowerUp> newPowerUpsDeck() {
            var cards = Stream.of(PowerUp.Type.values()).map(e -> Stream.of(AmmoCard.Color.values()).map(f -> new PowerUp(f, e)))
                    .flatMap(e -> e).collect(Collectors.toCollection(ArrayList::new));
            cards.addAll(Stream.of(PowerUp.Type.values()).map(e -> Stream.of(AmmoCard.Color.values()).map(f -> new PowerUp(f, e)))
                    .flatMap(e -> e).collect(Collectors.toCollection(ArrayList::new)));
            return new Deck<>(cards, true);
        }

        /**
         * New weapons deck deck.
         *
         * @return the deck
         */
        @Contract(" -> new")
        public static @NotNull Deck<Weapon> newWeaponsDeck() {
            var cards = new ArrayList<>(List.of(Weapon.values()));
            return new Deck<>(cards, false);
        }
    }
}