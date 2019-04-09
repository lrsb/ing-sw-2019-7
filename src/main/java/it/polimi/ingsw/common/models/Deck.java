package it.polimi.ingsw.common.models;

import it.polimi.ingsw.common.models.exceptions.EmptyDeckException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Deck<T> implements Serializable {
    private final @NotNull ArrayList<T> playableCards = new ArrayList<>();
    private final @NotNull ArrayList<T> exitedCards = new ArrayList<>();
    private final @NotNull ArrayList<T> discardedCards = new ArrayList<>();
    private final boolean shuffleable;

    private Deck(@NotNull ArrayList<T> cards, boolean shuffleable) {
        playableCards.addAll(cards);
        this.shuffleable = shuffleable;
    }

    public @NotNull T discardCard() throws EmptyDeckException {
        if (playableCards.isEmpty()) shuffleDeck();
        var discardedCard = playableCards.remove(0);
        discardedCards.add(discardedCard);
        return discardedCard;
    }

    public @NotNull T exitCard() throws EmptyDeckException {
        if (playableCards.isEmpty()) shuffleDeck();
        var exitedCard = playableCards.remove(0);
        exitedCards.add(exitedCard);
        return exitedCard;
    }

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

    public void discardCard(@NotNull T exitedCard) {
        if (exitedCards.indexOf(exitedCard) >= 0) exitedCards.remove(exitedCard);
        discardedCards.add(exitedCard);
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

        @Contract(" -> new")
        public static @NotNull Deck<PowerUp> newPowerUpsDeck() {
            return new Deck<>(new ArrayList<>(), true);
        }

        @Contract(" -> new")
        public static @NotNull Deck<Weapon> newWeaponsDeck() {
            return new Deck<>(new ArrayList<>(Stream.of(Weapon.Name.values())
                    .map(e -> new Weapon(e, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()))
                    .collect(Collectors.toList())), false);
        }
    }
}