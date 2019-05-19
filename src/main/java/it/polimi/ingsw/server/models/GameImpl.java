package it.polimi.ingsw.server.models;

import it.polimi.ingsw.common.models.*;
import it.polimi.ingsw.common.models.wrappers.Opt;
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
        //TODO: come evitare le celle che non fanno parte della mappa?
        for (var e : this.cells) for (var cell : e) {
            //TODO: sistemare
            //if (cell.isSpawnPoint()) while (cell.getWeapons().size() < 3) cell.addWeapon(weaponsDeck.exitCard());
            //else cell.setAmmoCard(ammoDeck.exitCard());
        }
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
    //GRAB_WEAPON STUFF - Start

    private boolean grabIn(@NotNull Point point, @Nullable Weapon.Name weapon, @Nullable Weapon.Name discardedWeaponName, @Nullable ArrayList<PowerUp> powerUpPayment) {
        /*if (!canMove(getActualPlayer().getPosition(), point, getActualPlayer().getDamagesTaken().size() >= 3 ? 2 : 1))
            return false;
        if (Stream.of(Cell.Color.values()).anyMatch(e -> getCell(point).getColor() == e &&
                getCell(point).isSpawnPoint() && getCell(point).getWeapons().contains(weapon))) {
            if (getActualPlayer().getWeaponsSize() == 3 &&
                    (discardedWeaponName == null || !getActualPlayer().hasWeapon(discardedWeaponName))) return false;
            if (canPayWeaponAndPay(weapon, powerUpPayment)) {
                getCell(point).removeWeapon(weapon);
                getActualPlayer().setPosition(point);
                getActualPlayer().addWeapon(weapon);
                if (getActualPlayer().getWeaponsSize() == 4) {
                    getActualPlayer().removeWeapon(discardedWeaponName);
                    getCell(point).addWeapon(discardedWeaponName);
                }
                return true;
            }
        } else if (getCell(point).getAmmoCard() != null) {
            getActualPlayer().setPosition(point);
            getActualPlayer().ammoCardRecharging(getCell(point).getAmmoCard(),
                    getCell(point).getAmmoCard().getType() == AmmoCard.Type.POWER_UP &&
                            getActualPlayer().getPowerUps().size() < 3 ? powerUpsDeck.exitCard() : null);
            ammoDeck.discardCard(getCell(point).getAmmoCard());
            return true;
        }*/
        return false;
    }

    private boolean canPayWeaponAndPay(@NotNull Weapon.Name weapon, @Nullable ArrayList<PowerUp> powerUpPayment) {
        int[] cost = new int[AmmoCard.Color.values().length];
        Stream.of(AmmoCard.Color.values()).forEach(e -> cost[e.getIndex()] = weapon.getGrabCost(e));
        if (getActualPlayer().hasWeapon(weapon)) cost[weapon.getColor().getIndex()]++;
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

    //GRAB_WEAPON STUFF - End

    private boolean fireAction(@NotNull Action action) {
        var weapon = action.getWeapon().build(this, action.getAlternativeFire());
        action.getBasicTarget().stream().forEachOrdered(e -> weapon.addBasicTarget(e));
        weapon.setBasicTargetsPoint(action.getBasicTargetPoint());
        weapon.setBasicAlternativePayment(action.getBasicAlternativePayment());
        if ((action.getOptions() & Weapon.FIRST) == 1) {
            action.getFirstAdditionalTarget().stream().forEachOrdered(e -> weapon.addFirstAdditionalTarget(e));
            weapon.setFirstAdditionalTargetsPoint(action.getFirstAdditionalTargetPoint());
            weapon.setFirstAdditionalPayment(action.getFirstAdditionalPayment());
        }
        if ((action.getOptions() & Weapon.SECOND) == 2) {
            action.getSecondAdditionalTarget().stream().forEachOrdered(e -> weapon.addSecondAdditionalTarget(e));
            weapon.setSecondAdditionalTargetsPoint(action.getSecondAdditionalTargetPoint());
            weapon.setSecondAdditionalPayment(action.getSecondAdditionalPayment());
        }
        return weapon.fire(action.getOptions());
    }

    private void nextTurn() {
        /*for (var cells : cells)
            for (var cell : cells) {
                if (!cell.isSpawnPoint() && cell.getAmmoCard() == null) cell.setAmmoCard(ammoDeck.exitCard());
                if (cell.isSpawnPoint() && cell.getWeapons().size() < 3 && weaponsDeck.remainedCards() > 0)
                    cell.addWeapon(weaponsDeck.exitCard());
            }*/
        deathPointsRedistribution();
        reborn();
        seqPlay++;
    }

    private void deathPointsRedistribution() {
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

    private void reborn() {
        /*TODO: foreach in getDeadPlayer draw a PowerUpCard and discard a PowerUp,
           player respawn on the spawnpoint of the color of the discarded PowerUp*/
    }

    public boolean doAction(@NotNull Action action) {
        switch (Opt.of(action.getActionType()).get(Action.Type.NOTHING)) {
            case MOVE:
                if (action.getDestination() == null) return false;
                return moveTo(action.getDestination());
            case GRAB_WEAPON:
                return grabIn(Opt.of(action.getDestination()).get(Opt.of(getActualPlayer().getPosition())
                        .get(new Point(-1, -1))), action.getWeapon(), action.getDiscardedWeapon(),
                        action.getPowerUpPayment());
            case FIRE:
                if (action.getWeapon() != null && getActualPlayer().hasWeapon(action.getWeapon()) &&
                        getActualPlayer().isALoadedGun(action.getWeapon())) return fireAction(action);
                return false;
            case USE_POWER_UP:
                //TODO: creare PowerUp e customizzare
                return action.getPowerUpType() != null && Stream.of(AmmoCard.Color.values())
                        .anyMatch(e -> new PowerUp(e, action.getPowerUpType()).use(this));
            case RELOAD:
                return getActualPlayer().hasWeapon(action.getWeapon()) &&
                        !getActualPlayer().isALoadedGun(action.getWeapon()) &&
                        canPayWeaponAndPay(action.getWeapon(), action.getPowerUpPayment());
            case NEXT_TURN:
                //TODO: controllare se ha fatto le due mosse o da fare se scaduto il tempo
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
        @Contract("_ -> new")
        public static @NotNull GameImpl newGame(@NotNull Room room) {
            //assert users.size() >= MIN_PLAYERS && users.size() < MAX_PLAYERS;
            var cells = new Cell[MAX_X][MAX_Y];
            for (var i = 0; i < cells.length; i++) {
                for (var j = 0; j < cells[i].length; j++) {
                    //cells[i][j] = Cell.Creator.withBounds("----").color(Cell.Color.GREEN).spawnPoint(true).create();
                }
            }
            return new GameImpl(room.getUuid(), Type.SIX_SIX, cells, room.getUsers().stream().map(Player::new).collect(Collectors.toList()));
        }
    }
}