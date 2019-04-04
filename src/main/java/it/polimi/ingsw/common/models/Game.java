package it.polimi.ingsw.common.models;

import it.polimi.ingsw.common.models.wrappers.Triplet;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class Game {
    private static final int MAX_PLAYERS = 5;

    private @NotNull UUID uuid = UUID.randomUUID();
    private Cell[][] cells;//x,y
    private ArrayList<Player> players = new ArrayList<>();
    private int seqPlay = 0;

    private int skulls = 5;//da 5 a 8

    private Deck<AmmoCard> ammoDeck = Deck.Creator.newAmmoDeck();
    private Deck<PowerUp> powerUpsDeck = Deck.Creator.newPowerUpsDeck();
    private Deck<Weapon> weaponsDeck = Deck.Creator.newWeaponsDeck();

    private Triplet<Weapon> redWeapons;
    private Triplet<Weapon> blueWeapons;
    private Triplet<Weapon> yellowWeapons;

    private Game(@NotNull Cell[][] cells) {
        this.cells = cells;
        //redWeapons = new Triplet<>(newWeaponsDeck.exitCards(3));
        //blueWeapons = new Triplet<>(newWeaponsDeck.exitCards(3));
        //yellowWeapons = new Triplet<>(newWeaponsDeck.exitCards(3));
        //Arrays.stream(cells).forEach(e -> Arrays.stream(e).filter(f -> !f.isSpawnPoint()).forEach(g -> g.setAmmoCard(newAmmoDeck.exitCard())));
    }

    public Player getActualPlayer() {
        return players.get(seqPlay);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public UUID getUuid() {
        return uuid;
    }

    public static class Creator {
        @Contract(pure = true)
        private Creator() {
        }

        @NotNull
        @Contract(" -> new")
        public static Game newGame() {
            var cells = new Cell[4][3];
            for (var i = 0; i < cells.length; i++)
                for (var j = 0; j < cells[i].length; j++) {
                    cells[i][j] = Cell.Creator.withBounds("----").color(Cell.Color.GREEN).spawnPoint(true).create();
                }
            return new Game(cells);
        }
    }
}