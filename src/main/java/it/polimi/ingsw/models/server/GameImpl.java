package it.polimi.ingsw.models.server;

import it.polimi.ingsw.models.common.*;
import it.polimi.ingsw.models.interfaces.IRmiGame;
import it.polimi.ingsw.wrappers.Triplet;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class GameImpl extends UnicastRemoteObject implements IRmiGame {
    private static final int MAX_PLAYERS = 5;

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

    private GameImpl(@NotNull Cell[][] cells) throws RemoteException {
        super();
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

    @Override
    public String makeMove() {
        return "ciao";
    }

    //TODO: impl
    @Override
    public String waitBoardUpdate() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ok fun";
    }

    public static class Creator {
        @Contract(pure = true)
        private Creator() {
        }

        @NotNull
        @Contract(" -> new")
        public static GameImpl newGame() throws RemoteException {
            var cells = new Cell[4][3];
            for (var i = 0; i < cells.length; i++)
                for (var j = 0; j < cells[i].length; j++) {
                    cells[i][j] = Cell.Creator.withBounds("----").color(Cell.Color.GREEN).spawnPoint(true).create();
                }
            return new GameImpl(cells);
        }
    }
}