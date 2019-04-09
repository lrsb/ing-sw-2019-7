package it.polimi.ingsw.common.models;

import it.polimi.ingsw.common.models.wrappers.Triplet;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Game implements Serializable {
    private static final int MAX_PLAYERS = 5;

    private @NotNull UUID uuid = UUID.randomUUID();
    private Cell[][] cells;//x,y
    private ArrayList<Player> players = new ArrayList<>();
    private int seqPlay = 0;

    private int skulls = 5;//da 5 a 8

    private transient Deck<AmmoCard> ammoDeck = Deck.Creator.newAmmoDeck();
    private transient Deck<PowerUp> powerUpsDeck = Deck.Creator.newPowerUpsDeck();
    private transient Deck<Weapon> weaponsDeck = Deck.Creator.newWeaponsDeck();

    private Triplet<Weapon> redWeapons;
    private Triplet<Weapon> blueWeapons;
    private Triplet<Weapon> yellowWeapons;
    private transient List<PowerUp> exitedPowerUps;

    private Game(@NotNull Cell[][] cells, @NotNull List<Player> players) {
        this.cells = cells;
        this.players.addAll(players);
        redWeapons = new Triplet<>(weaponsDeck.exitCards(3));
        blueWeapons = new Triplet<>(weaponsDeck.exitCards(3));
        yellowWeapons = new Triplet<>(weaponsDeck.exitCards(3));
        Arrays.stream(cells).forEach(e -> Arrays.stream(e).filter(f -> !f.isSpawnPoint()).forEach(g -> g.setAmmoCard(ammoDeck.exitCard())));

    }

    public Player getActualPlayer() {
        return players.get(seqPlay);
    }

    //FIRST MOVE - Start

    public UUID getUuid() {
        return uuid;
    }

    public boolean isFirstMove() {
        return getActualPlayer().isFirstMove();
    }

    public List<PowerUp> getFirstMoveColors() {
        exitedPowerUps = powerUpsDeck.exitCards(2);
        return exitedPowerUps;
    }

    public void completeFirstMove(PowerUp cardToThrow) {
        assert exitedPowerUps.contains(cardToThrow);
        exitedPowerUps.remove(cardToThrow);
        powerUpsDeck.discardCard(cardToThrow);
        var cardToKeep = exitedPowerUps.remove(0);
        getActualPlayer().addPowerUp(cardToKeep);
        getActualPlayer().setPlayed();
        assert exitedPowerUps.isEmpty() && getActualPlayer().getPowerUps().size() == 1;
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (cells[i][j].isSpawnPoint() && cells[i][j].getColor().name().equals(cardToKeep.getAmmoColor().name())) {
                    getActualPlayer().setPosition(new Point(i, j));
                    seqPlay++;
                    return;
                }
            }
        }
        assert getActualPlayer().getPosition() != null;
    }

    //FIRST MOVE - End
    //RUN AROUND - Start

    public boolean moveTo(@NotNull Point to) {
        if (!canMove(getActualPlayer().getPosition(), to, 0, 3)) return false;
        getActualPlayer().setPosition(to);
        return true;
    }

    @Contract(pure = true)
    private boolean canMove(@NotNull Point from, @NotNull Point to, int step, int maxStep) {
        if (from.x < 0 || from.x >= cells.length || from.y < 0 || from.y >= cells[from.x].length) return false;
        if (to.x < 0 || to.x >= cells.length || to.y < 0 || to.y >= cells[to.x].length) return false;
        if (from.equals(to)) return true;
        if (step >= maxStep) return false;
        var north = cells[from.x][from.y].getBounds().getType(Bounds.Direction.N) != Bounds.Type.WALL && canMove(new Point(from.x, to.y + 1), to, step + 1, maxStep);
        var south = cells[from.x][from.y].getBounds().getType(Bounds.Direction.N) != Bounds.Type.WALL && canMove(new Point(from.x, to.y - 1), to, step + 1, maxStep);
        var east = cells[from.x][from.y].getBounds().getType(Bounds.Direction.N) != Bounds.Type.WALL && canMove(new Point(from.x + 1, to.y), to, step + 1, maxStep);
        var west = cells[from.x][from.y].getBounds().getType(Bounds.Direction.N) != Bounds.Type.WALL && canMove(new Point(from.x - 1, to.y), to, step + 1, maxStep);
        return north || south || east || west;
    }

    //RUN AROUND - End
    //GRAB STUFF - Start


    //GRAB STUFF - End

    public static class Creator {
        @Contract(pure = true)
        private Creator() {
        }

        @Contract("_ -> new")
        public static @NotNull Game newGame(@NotNull User... users) {
            assert users.length < MAX_PLAYERS;
            var cells = new Cell[4][3];
            for (var i = 0; i < cells.length; i++) {
                for (var j = 0; j < cells[i].length; j++) {
                    cells[i][j] = Cell.Creator.withBounds("----").color(Cell.Color.GREEN).spawnPoint(true).create();
                }
            }
            return new Game(cells, Stream.of(users).map(e -> new Player(e.getNickname())).collect(Collectors.toList()));
        }
    }
}