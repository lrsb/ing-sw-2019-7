package it.polimi.ingsw.models.cards;

import it.polimi.ingsw.models.weapons.Weapon;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck<E extends Card> {
    private ArrayList<E> playableCards = new ArrayList<>();
    private ArrayList<E> exitedCards = new ArrayList<>();
    private ArrayList<E> discardedCards = new ArrayList<>();

    private Deck(ArrayList<E> cards) {
        playableCards.addAll(cards);
    }

    public E discardCard() {
        if (playableCards.isEmpty()) shuffleDeck();
        var discardedCard = playableCards.remove(0);
        discardedCards.add(discardedCard);
        return discardedCard;
    }

    public E exitCard() {
        if (playableCards.isEmpty()) shuffleDeck();
        var exitedCard = playableCards.remove(0);
        exitedCards.add(exitedCard);
        return exitedCard;
    }

    public List<E> exitCards(int n) {
        if (playableCards.isEmpty()) shuffleDeck();
        var list = new ArrayList<E>();
        for (int i = 0; i < n; i++) {
            var exitedCard = playableCards.remove(0);
            list.add(exitedCard);
        }
        exitedCards.addAll(list);
        return list;
    }

    public void discardCard(@NotNull E exitedCard) {
        if (exitedCards.indexOf(exitedCard) >= 0) exitedCards.remove(exitedCard);
        discardedCards.add(exitedCard);
    }

    private void shuffleDeck() {
        playableCards.addAll(discardedCards);
        discardedCards.clear();
        Collections.shuffle(playableCards);
    }

    public static class Creator {
        //TODO: implementare tutto quanto
        @NotNull
        @Contract(" -> new")
        public static Deck<AmmoCard> ammoDeck() {
            return new Deck<>(new ArrayList<>());
        }

        @NotNull
        @Contract(" -> new")
        public static Deck<PowerUp> powerUpsDeck() {
            return new Deck<>(new ArrayList<>());
        }

        @NotNull
        @Contract(" -> new")
        public static Deck<Weapon> weaponsDeck() {
            return new Deck<>(new ArrayList<>());
        }
    }
}
