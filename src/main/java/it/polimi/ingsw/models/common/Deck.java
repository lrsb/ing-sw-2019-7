package it.polimi.ingsw.models.common;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck<T> {
    private @NotNull ArrayList<T> playableCards = new ArrayList<>();
    private @NotNull ArrayList<T> exitedCards = new ArrayList<>();
    private @NotNull ArrayList<T> discardedCards = new ArrayList<>();
    private boolean shufflable;

    private Deck(@NotNull ArrayList<T> cards, boolean shufflable) {
        playableCards.addAll(cards);
        this.shufflable = shufflable;
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