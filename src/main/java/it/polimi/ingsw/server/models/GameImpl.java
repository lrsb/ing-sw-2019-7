package it.polimi.ingsw.server.models;

import it.polimi.ingsw.common.models.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameImpl extends Game implements Serializable {
    private static final long serialVersionUID = 1;

    private @NotNull Deck<AmmoCard> ammoDeck = Deck.Creator.newAmmoDeck();
    private @NotNull Deck<PowerUp> powerUpsDeck = Deck.Creator.newPowerUpsDeck();
    private @NotNull Deck<Weapon.Name> weaponsDeck = Deck.Creator.newWeaponsDeck();

    private @NotNull ArrayList<PowerUp> exitedPowerUps = new ArrayList<>();

    private GameImpl(@NotNull UUID uuid, @NotNull Type type, @NotNull Cell[][] cells, @NotNull List<Player> players) {
        super(uuid, type, cells, players);
        //TODO: piazzare armi
        //Arrays.stream(cells).forEach(e -> Arrays.stream(e).filter(f -> !f.isSpawnPoint()).forEach(g -> g.setAmmoCard(ammoDeck.exitCard())));
    }

    public List<PowerUp> getFirstMoveColors() {
        exitedPowerUps.addAll(powerUpsDeck.exitCards(2));
        return exitedPowerUps;
    }

    public void completeFirstMove(PowerUp cardToThrow) {
        assert exitedPowerUps.contains(cardToThrow);
        exitedPowerUps.remove(cardToThrow);
        powerUpsDeck.discardCard(cardToThrow);
        var cardToKeep = exitedPowerUps.remove(0);
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

    private boolean moveTo(@NotNull Point to) {
        if (!lastTurn && (skulls == 0 && canMove(getActualPlayer().getPosition(), to, 4) ||
                canMove(getActualPlayer().getPosition(), to, 3))) {
            getActualPlayer().setPosition(to);
            return true;
        }
        return false;
    }

    //RUN AROUND - End
    //GRAB STUFF - Start

    private boolean grabIn(@NotNull Point point, @Nullable Weapon.Name weapon, @Nullable ArrayList<PowerUp> powerUpPayment) {
        if (!canMove(getActualPlayer().getPosition(), point, getActualPlayer().getDamagesTaken().size() >= 3 ? 2 : 1))
            return false;
        if (Stream.of(Cell.Color.values()).anyMatch(e -> getCell(point).getColor() == e &&
                getCell(point).isSpawnPoint() && getCell(point).getWeapons().contains(weapon))) {
            if (canPayGrabAndPay(weapon, powerUpPayment)) {
                getActualPlayer().setPosition(point);
                getActualPlayer().addWeapon(weapon);
                return true;
            }
        } else if (getCell(point).getAmmoCard() != null) {
            getActualPlayer().setPosition(point);
            getActualPlayer().ammoCardRecharging(getCell(point).getAmmoCard(),
                    getCell(point).getAmmoCard().getType() == AmmoCard.Type.POWER_UP &&
                            getActualPlayer().getPowerUps().size() < 3 ? powerUpsDeck.exitCard() : null);
            ammoDeck.discardCard(getCell(point).getAmmoCard());
            return true;
        }
        return false;
    }

    private boolean canPayGrabAndPay(@NotNull Weapon.Name weapon, @Nullable ArrayList<PowerUp> powerUpPayment) {
        int[] cost = new int[AmmoCard.Color.values().length];
        Stream.of(AmmoCard.Color.values()).forEach(e -> cost[e.getIndex()] = weapon.getGrabCost(e));
        if (powerUpPayment != null) for (PowerUp e : powerUpPayment) {
            if (cost[e.getAmmoColor().getIndex()] > 0) cost[e.getAmmoColor().getIndex()]--;
            else powerUpPayment.remove(e);
        }
        if (Stream.of(AmmoCard.Color.values())
                .allMatch(e -> getActualPlayer().getColoredCubes(e) >= cost[e.getIndex()])) {
            if (powerUpPayment != null)
                powerUpPayment.forEach(e -> {
                    getActualPlayer().removePowerUp(e);
                    powerUpsDeck.discardCard(e);
                });
            Stream.of(AmmoCard.Color.values()).forEach(e -> getActualPlayer().removeColoredCubes(e, cost[e.getIndex()]));
            return true;
        }
        return false;
    }

    //GRAB STUFF - End

    private void nextTurn() {
        for (var cells : cells)
            for (var cell : cells) {
                if (!cell.isSpawnPoint() && cell.getAmmoCard() == null) cell.setAmmoCard(ammoDeck.exitCard());
                if (cell.isSpawnPoint() && cell.getWeapons().size() < 3 && weaponsDeck.remainedCards() > 0)
                    cell.addWeapon(weaponsDeck.exitCard());
            }
        deathPointsRedistribution();
        reborn();
        seqPlay++;
    }

    protected void deathPointsRedistribution() {
        getActualPlayer().addPoints(getDeadPlayers().size() > 1 ? 1 : 0);
        getDeadPlayers().forEach(e -> e.getSortedHitters().forEach(f -> getPlayers().parallelStream()
                .filter(g -> g.getUuid() == f)
                .forEach(g -> {
                    g.addPoints(2 * e.getSortedHitters().indexOf(f) >= e.getMaximumPoints() ? 1 :
                            e.getMaximumPoints() - 2 * e.getSortedHitters().indexOf(f));
                    g.addPoints(e.getSortedHitters().indexOf(f) == 0 ? 1 : 0);
                    if (e.getDamagesTaken().size() == 12 && f == e.getDamagesTaken().get(11)) e.addMark(g);
                })));
        getDeadPlayers().forEach(Player::incrementDeaths);
    }

    protected void reborn() {
        /*TODO: foreach in getDeadPlayer draw a PowerUpCard and discard a PowerUp,
           player respawn on the spawnpoint of the color of the discarded PowerUp*/
    }

    public boolean doAction(@NotNull Action action) {
        switch (action.getActionType()) {
            case MOVE:
                if (action.getDestination() == null) return false;
                return moveTo(action.getDestination());
            case GRAB:
                if (action.getDestination() == null) action.setDestination(getActualPlayer().getPosition());
                return grabIn(action.getDestination(), action.getWeaponName(), action.getPowerUpPayment());
            case FIRE:
                break;
            case USE_POWER_UP:
                break;
            case RELOAD:
                break;
            case NEXT_TURN:
                nextTurn();
                return true;
        }
        return false;
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