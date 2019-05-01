package it.polimi.ingsw.server.models;

import it.polimi.ingsw.common.models.*;
import it.polimi.ingsw.server.models.exceptions.EmptyDeckException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameImpl extends Game implements Serializable {
    private static final long serialVersionUID = 1;

    private Deck<AmmoCard> ammoDeck = Deck.Creator.newAmmoDeck();
    private Deck<PowerUp> powerUpsDeck = Deck.Creator.newPowerUpsDeck();
    private Deck<Weapon.Name> weaponsDeck = Deck.Creator.newWeaponsDeck();

    private List<PowerUp> exitedPowerUps;

    private GameImpl(@NotNull UUID uuid, @NotNull Type type, @NotNull Cell[][] cells, @NotNull List<Player> players) {
        super(uuid, type, cells, players);
        redWeapons = new ArrayList<>(weaponsDeck.exitCards(3));
        blueWeapons = new ArrayList<>(weaponsDeck.exitCards(3));
        yellowWeapons = new ArrayList<>(weaponsDeck.exitCards(3));
        //Arrays.stream(cells).forEach(e -> Arrays.stream(e).filter(f -> !f.isSpawnPoint()).forEach(g -> g.setAmmoCard(ammoDeck.exitCard())));
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
                    nextTurn();
                    return;
                }
            }
        }
        assert getActualPlayer().getPosition() != null;
    }

    //FIRST MOVE - End
    //RUN AROUND - Start

    public boolean moveTo(@NotNull Point to) {
        if (!canMove(getActualPlayer().getPosition(), to, 3)) return false;
        getActualPlayer().setPosition(to);
        return true;
    }

    //RUN AROUND - End
    //GRAB STUFF - Start

    public boolean grabIn(@NotNull Point point, @Nullable Weapon.Name weapon) {
        assert point.x >= 0 && point.x < MAX_X && point.y >= 0 && point.y < MAX_Y;
        if (!canMove(getActualPlayer().getPosition(), point, 1)) return false;
        getActualPlayer().setPosition(point);
        //TODO: pay cost
        if (cells[point.x][point.y].isSpawnPoint()) switch (cells[point.x][point.y].getColor()) {
            case BLUE:
                if (blueWeapons.remove(weapon)) getActualPlayer().addWeapon(weapon);
                return true;
            case RED:
                if (redWeapons.remove(weapon)) getActualPlayer().addWeapon(weapon);
                return true;
            case YELLOW:
                if (yellowWeapons.remove(weapon)) getActualPlayer().addWeapon(weapon);
                return true;
            default:
                return false;
        }
        else getActualPlayer().addAmmoCard(cells[point.x][point.y].getAmmoCard());
        return true;
    }

    //GRAB STUFF - End

    private void nextTurn() {
        for (var cells : cells)
            for (var cell : cells)
                if (!cell.isSpawnPoint() && cell.getAmmoCard() == null) cell.setAmmoCard(ammoDeck.exitCard());
        try {
            while (redWeapons.size() < 3) redWeapons.add(weaponsDeck.exitCard());
            while (blueWeapons.size() < 3) blueWeapons.add(weaponsDeck.exitCard());
            while (yellowWeapons.size() < 3) yellowWeapons.add(weaponsDeck.exitCard());
        } catch (EmptyDeckException e) {
            e.printStackTrace();
        }
        seqPlay++;
    }

    public static class Creator {
        @Contract(pure = true)
        private Creator() {
        }

        //TODO: impl
        @Contract("_, _ -> new")
        public static @NotNull GameImpl newGame(@NotNull UUID roomUuid, @NotNull List<User> users) {
            //assert users.size() >= MIN_PLAYERS && users.size() < MAX_PLAYERS;
            var cells = new Cell[MAX_X][MAX_Y];
            for (var i = 0; i < cells.length; i++) {
                for (var j = 0; j < cells[i].length; j++) {
                    //cells[i][j] = Cell.Creator.withBounds("----").color(Cell.Color.GREEN).spawnPoint(true).create();
                }
            }
            return new GameImpl(roomUuid, Type.SIX_SIX, cells, users.stream().map(Player::new).collect(Collectors.toList()));
        }
    }
}