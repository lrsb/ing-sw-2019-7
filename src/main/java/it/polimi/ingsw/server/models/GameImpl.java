package it.polimi.ingsw.server.models;

import it.polimi.ingsw.common.models.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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
        //TODO: come evitare le celle che non fanno parte della mappa
        redWeapons = new ArrayList<>(weaponsDeck.exitCards(3));
        blueWeapons = new ArrayList<>(weaponsDeck.exitCards(3));
        yellowWeapons = new ArrayList<>(weaponsDeck.exitCards(3));
        Stream.of(cells).flatMap(Stream::of).forEach(e -> {
            if (!e.isSpawnPoint()) e.setAmmoCard(ammoDeck.exitCard());
        });
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

    private boolean grabIn(@NotNull Point point, @Nullable Weapon.Name weapon, @Nullable Weapon.Name discardedWeaponName, @Nullable ArrayList<PowerUp> powerUpPayment) {
        if (!canMove(getActualPlayer().getPosition(), point, getActualPlayer().getDamagesTaken().size() >= 3 ? 2 : 1))
            return false;
        if (Stream.of(Cell.Color.values()).anyMatch(e -> getCell(point).getColor() == e &&
                getCell(point).isSpawnPoint() && getWeapons(getCell(point).getColor()).contains(weapon))) {
            if (getActualPlayer().getWeaponsSize() == 3 &&
                    (discardedWeaponName == null || !getActualPlayer().hasWeapon(discardedWeaponName))) return false;
            if (canPayWeaponAndPay(weapon, powerUpPayment)) {
                getWeapons(getCell(point).getColor()).remove(weapon);
                getActualPlayer().setPosition(point);
                getActualPlayer().addWeapon(weapon);
                if (getActualPlayer().getWeaponsSize() == 4) {
                    getActualPlayer().removeWeapon(discardedWeaponName);
                    getWeapons(getCell(point).getColor()).add(discardedWeaponName);
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
        }
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

    //GRAB STUFF - End

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
        for (var cells : cells)
            for (var cell : cells) {
                if (!cell.isSpawnPoint() && cell.getAmmoCard() == null) cell.setAmmoCard(ammoDeck.exitCard());
                if (cell.isSpawnPoint() && getWeapons(cell.getColor()).size() < 3 && weaponsDeck.remainedCards() > 0)
                    getWeapons(cell.getColor()).add(weaponsDeck.exitCard());
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
            case GRAB_WEAPON:
                return grabIn(action.getDestination(), action.getWeapon(), action.getDiscardedWeapon(), action.getPowerUpPayment());
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
                    cells[i][j] = new Cell(Cell.Color.GREEN, new Bounds(Bounds.Type.DOOR, Bounds.Type.DOOR, Bounds.Type.DOOR, Bounds.Type.DOOR), false);
                    //Cell.Creator.withBounds("----").color(Cell.Color.GREEN).spawnPoint(true).create();
                }
            }
            var boards = new ArrayList<>(List.of(Player.BoardType.values()));
            Collections.shuffle(boards);
            return new GameImpl(room.getUuid(), room.getGameType(), cells, room.getUsers().stream().map(e -> new Player(e, boards.remove(0))).collect(Collectors.toList()));
        }
    }
}