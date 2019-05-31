package it.polimi.ingsw.server.models;

import it.polimi.ingsw.common.models.*;
import it.polimi.ingsw.common.models.wrappers.Opt;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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

    private GameImpl(@NotNull UUID uuid, @NotNull Type type, @NotNull Cell[][] cells, @NotNull List<Player> players, int skulls) {
        super(uuid, type, cells, players, skulls);
        for (var e : this.cells) for (var cell : e) {
            if (cell.isSpawnPoint())
                while(getWeapons(cell.getColor()).size() < 3) addWeapon(cell.getColor(), weaponsDeck.exitCard());
            else cell.setAmmoCard(ammoDeck.exitCard());
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
        getActualPlayer().addPowerUp(cardToKeep);
        assert exitedPowerUps.isEmpty() && getActualPlayer().getPowerUps().size() == 1;
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (cells[i][j].isSpawnPoint() && cells[i][j].getColor().name().equals(cardToThrow.getAmmoColor().name())) {
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

    private boolean grabWeapon(@NotNull Point to, @NotNull Weapon.Name weapon, @Nullable Weapon.Name discardedWeaponName, @Nullable ArrayList<PowerUp> powerUpPayment) {
        var cell = getCell(to);
        if (cell == null) return false;
        if (!canMove(getActualPlayer().getPosition(), to, 1) && !(skulls == 0 &&
                canMove(getActualPlayer().getPosition(), to, 2)) && !(lastTurn &&
                canMove(getActualPlayer().getPosition(), to, 3)) ||
                !cell.isSpawnPoint() || !getWeapons(cell.getColor()).contains(weapon) ||
                getActualPlayer().getWeaponsSize() > 2 && (discardedWeaponName == null ||
                        !getActualPlayer().hasWeapon(discardedWeaponName))) return false;
        if (canPayWeaponAndPay(weapon, powerUpPayment)) {
            getActualPlayer().setPosition(to);
            getActualPlayer().addWeapon(weapon);
            removeWeapon(cell.getColor(), weapon);
            if (getActualPlayer().getWeaponsSize() > 3 && discardedWeaponName != null) {
                getActualPlayer().removeWeapon(discardedWeaponName);
                addWeapon(cell.getColor(), discardedWeaponName);
            }
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

    private boolean grabAmmoCard(@NotNull Point to) {
        var cell = getCell(to);
        if (cell == null) return false;
        if (cell.isSpawnPoint() || cell.getAmmoCard() == null ||
                !canMove(getActualPlayer().getPosition(), to, 1) && !(skulls == 0 &&
                canMove(getActualPlayer().getPosition(), to, 2)) && !(lastTurn &&
                canMove(getActualPlayer().getPosition(), to, 3))) return false;
        getActualPlayer().setPosition(to);
        getActualPlayer().ammoCardRecharging(cell.getAmmoCard(),
                cell.getAmmoCard().getType() == AmmoCard.Type.POWER_UP &&
                        getActualPlayer().getPowerUps().size() > 2 ? powerUpsDeck.exitCard() : null);
        ammoDeck.discardCard(cell.getAmmoCard());
        cell.removeAmmoCard();
        return true;
    }

    //GRAB_WEAPON STUFF - End

    private boolean fireAction(@NotNull Action action) {
        if (action.getWeapon() == null) return false;
        var weapon = action.getWeapon().build(this, action.getAlternativeFire());
        if (action.getBasicTarget() != null) action.getBasicTarget().forEach(weapon::addBasicTarget);
        if (action.getBasicTargetPoint() != null) weapon.setBasicTargetsPoint(action.getBasicTargetPoint());
        if (action.getBasicAlternativePayment() != null)
            weapon.setBasicAlternativePayment(action.getBasicAlternativePayment());
        if ((action.getOptions() & Weapon.FIRST) == 1) {
            if (action.getFirstAdditionalTarget() != null)
                action.getFirstAdditionalTarget().forEach(weapon::addFirstAdditionalTarget);
            if (action.getFirstAdditionalTargetPoint() != null)
                weapon.setFirstAdditionalTargetsPoint(action.getFirstAdditionalTargetPoint());
            if (action.getFirstAdditionalPayment() != null)
                weapon.setFirstAdditionalPayment(action.getFirstAdditionalPayment());
        }
        if ((action.getOptions() & Weapon.SECOND) == 2) {
            if (action.getSecondAdditionalTarget() != null)
                action.getSecondAdditionalTarget().forEach(weapon::addSecondAdditionalTarget);
            if (action.getSecondAdditionalTargetPoint() != null)
                weapon.setSecondAdditionalTargetsPoint(action.getSecondAdditionalTargetPoint());
            if (action.getSecondAdditionalPayment() != null)
                weapon.setSecondAdditionalPayment(action.getSecondAdditionalPayment());
        }
        return weapon.fire(action.getOptions());
    }

    private void nextTurn() {
        final int beforeSkulls = skulls;
        for (var cells : cells) for (var cell : cells) {
                if (!cell.isSpawnPoint() && cell.getAmmoCard() == null) cell.setAmmoCard(ammoDeck.exitCard());
                if (cell.isSpawnPoint() && getWeapons(cell.getColor()).size() < 3 && weaponsDeck.remainedCards() > 0)
                    addWeapon(cell.getColor(), weaponsDeck.exitCard());
        }
        deathPointsRedistribution();
        if (beforeSkulls > 0 && skulls == 0) players.forEach(Player::setEasyBoard);
        if (skulls == 0 && seqPlay % (players.size() - 1) == 0) lastTurn = true;
        seqPlay++;
    }

    private void deathPointsRedistribution() {
        getActualPlayer().addPoints(getDeadPlayers().size() > 1 ? 1 : 0);
        getDeadPlayers().forEach(e -> e.getSortedHitters().forEach(f -> getPlayers().parallelStream().filter(g -> g.getUuid() == f)
                .forEachOrdered(g -> {
                    g.addPoints(2 * e.getSortedHitters().indexOf(f) >= e.getMaximumPoints() ? 1 : e.getMaximumPoints() - 2 * e.getSortedHitters().indexOf(f));
                    g.addPoints(e.getSortedHitters().indexOf(f) == 0 ? 1 : 0);
                    if (e.getDamagesTaken().size() == 12 && f == e.getDamagesTaken().get(11)) {
                        e.addMark(g);
                        addToKillshotsTrack(f);
                    }
                    if (f == e.getDamagesTaken().get(10)) addToKillshotsTrack(f);
                })));
        getDeadPlayers().forEach(e -> {e.manageDeath(); if (skulls > 0) skulls--;});
    }

    /**
     * when the game ends distributes the lasts points to players
     */
    public void finalPointsRedistribution() {
        players.parallelStream().filter(e -> e.getDamagesTaken().size() > 0).forEachOrdered(e -> e.getSortedHitters().forEach(f -> getPlayers().parallelStream()
                .filter(g -> g.getUuid() == f).forEachOrdered(g -> {
                    g.addPoints(2 * e.getSortedHitters().indexOf(f) >= e.getMaximumPoints() ? 1 : e.getMaximumPoints() - 2 * e.getSortedHitters().indexOf(f));
                    g.addPoints(e.getSortedHitters().indexOf(f) == 0 ? 1 : 0); })));
        int points = 8;
        for (int i = 0; i < getSortedKillshooters().size(); i++) {
            for (Player player : players) {
                if (player.getUuid().equals(getSortedKillshooters().get(i))) {
                    player.addPoints(points > 1 ? points : 1);
                    points -= 2;
                }
            }
        }
    }

    /**
     * It builds the ranking as an hashMap<String, Integer> where
     * String is the nickname of a player and Integer is his position in the ranking
     *
     * Example if two players are tied:
     * 1. firstPlayer
     * 1. firstPlayer
     * 3. thirdPlayer
     * 4. fourthPlayer
     * 5. fifthPlayer
     *
     * @return the HashMap
     */
    public @NotNull HashMap<String, Integer> getRanking() {
        var supportRanking = new HashMap<String, Integer>();
        var ranking = new HashMap<String, Integer>();
        players.forEach(e -> supportRanking.put(e.getNickname(), e.getPoints()));
        ranking.put(players.get(0).getNickname(), 1);
        for (int i = 1; i < players.size(); i++) {
            int j;
            for (j = 0; j < i && supportRanking.get(players.get(i).getNickname()) < supportRanking.get(players.get(j).getNickname()); j++);
            if (j != i) {
                final int I = i;
                ranking.put(players.get(i).getNickname(), ranking.get(players.get(j).getNickname()));
                if (supportRanking.get(players.get(i).getNickname()) > supportRanking.get(players.get(j).getNickname()))
                    players.parallelStream().filter(e -> ranking.containsKey(e.getNickname()) && !e.getNickname().equals(players.get(I).getNickname()) &&
                            supportRanking.get(e.getNickname()) <= supportRanking.get(players.get(I).getNickname())).forEach(e -> ranking.put(e.getNickname(), ranking.get(e.getNickname() + 1)));
                else players.parallelStream().filter(e -> ranking.containsKey(e.getNickname()) && !e.getNickname().equals(players.get(I).getNickname()) &&
                            supportRanking.get(e.getNickname()) < supportRanking.get(players.get(I).getNickname())).forEach(e -> ranking.put(e.getNickname(), ranking.get(e.getNickname() + 1)));
            } else ranking.put(players.get(i).getNickname(), i + 1);
        }
        for (int i = 1; i <= players.size(); i++) {
            final int I = i;
            ArrayList<Integer> tiedPlayers = new ArrayList<>();
            players.parallelStream().filter(e -> ranking.get(e.getNickname()).equals(I)).mapToInt(players::indexOf).forEach(tiedPlayers::add);
            for (int j = 0; j < tiedPlayers.size() - 1; j++) {
                for (int k = j; k < tiedPlayers.size(); k++) {
                    if (getSortedKillshooters().indexOf(players.get(tiedPlayers.get(j)).getUuid()) < getSortedKillshooters().indexOf(players.get(tiedPlayers.get(k)).getUuid())) {
                        if (getSortedKillshooters().indexOf(players.get(tiedPlayers.get(j)).getUuid()) == -1 || getSortedKillshooters().indexOf(players.get(tiedPlayers.get(k)).getUuid()) == -1) {
                            ranking.put(players.get(tiedPlayers.get(j)).getNickname(), ranking.get(players.get(tiedPlayers.get(j)).getNickname()) + 1);
                            tiedPlayers.remove(j);
                            j++;
                        } else {
                            ranking.put(players.get(tiedPlayers.get(k)).getNickname(), ranking.get(players.get(tiedPlayers.get(k)).getNickname()) + 1);
                            tiedPlayers.remove(k);
                            k--;
                        }
                    }
                    else if (getSortedKillshooters().indexOf(players.get(tiedPlayers.get(j)).getUuid()) > getSortedKillshooters().indexOf(players.get(tiedPlayers.get(k)).getUuid())) {
                        if (getSortedKillshooters().indexOf(players.get(tiedPlayers.get(j)).getUuid()) == -1 || getSortedKillshooters().indexOf(players.get(tiedPlayers.get(k)).getUuid()) == -1) {
                            ranking.put(players.get(tiedPlayers.get(k)).getNickname(), ranking.get(players.get(tiedPlayers.get(k)).getNickname()) + 1);
                            tiedPlayers.remove(k);
                            k--;
                        } else {
                            ranking.put(players.get(tiedPlayers.get(j)).getNickname(), ranking.get(players.get(tiedPlayers.get(j)).getNickname()) + 1);
                            tiedPlayers.remove(j);
                            j++;
                        }
                    }
                }
            }
        }
        return ranking;
    }

    private boolean reborn(@NotNull Action action) {
        if (action.getColor() == null || action.getPowerUpType() == null) return false;
        var powerUp = new PowerUp(action.getColor(), action.getPowerUpType());
        if (getActualPlayer().getPowerUps().contains(powerUp)) {
            powerUpsDeck.discardCard(powerUp);
            getActualPlayer().getPowerUps().remove(powerUp);
            for (int x = 0; x < 4; x++) for (int y = 0; y < 3; y++) {
                var cell = getCell(new Point(x, y));
                if (cell != null && cell.isSpawnPoint()) {
                    switch (cell.getColor()) {
                        case RED:
                            if (powerUp.getAmmoColor().equals(AmmoCard.Color.RED)) {
                                getActualPlayer().setPosition(new Point(x, y));
                                return true;
                            }
                            break;
                        case YELLOW:
                            if (powerUp.getAmmoColor().equals(AmmoCard.Color.YELLOW)) {
                                getActualPlayer().setPosition(new Point(x, y));
                                return true;
                            }
                            break;
                        case BLUE:
                            if (powerUp.getAmmoColor().equals(AmmoCard.Color.BLUE)) {
                                getActualPlayer().setPosition(new Point(x, y));
                                return true;
                            }
                            break;
                    }
                }
            }
        }
        return false;
    }

    public boolean doAction(@NotNull Action action) {
        if (getActualPlayer().getPosition() == null) return false;
        switch (Opt.of(action.getActionType()).get(Action.Type.NOTHING)) {
            case MOVE:
                if (action.getDestination() == null) return false;
                return moveTo(action.getDestination());
            case GRAB_WEAPON:
                if (action.getWeapon() == null) return false;
                return grabWeapon(Opt.of(action.getDestination()).get(getActualPlayer().getPosition()),
                        action.getWeapon(), action.getDiscardedWeapon(), action.getPowerUpPayment());
            case GRAB_AMMOCARD:
                return grabAmmoCard(Opt.of(action.getDestination()).get(getActualPlayer().getPosition()));
            case FIRE:
                if (action.getWeapon() != null && getActualPlayer().hasWeapon(action.getWeapon()) &&
                        getActualPlayer().isALoadedGun(action.getWeapon())) return fireAction(action);
                return false;
            case USE_POWER_UP:
                if (action.getColor() == null || action.getPowerUpType() == null) return false;
                var powerUp = new PowerUp(action.getColor(), action.getPowerUpType());
                getPlayers().stream().filter(e -> e.getUuid().equals(action.getTarget())).forEach(powerUp::setTarget);
                powerUp.setTargetPoint(action.getDestination());
                if (powerUp.use(this)) {
                    getActualPlayer().removePowerUp(powerUp);
                    powerUpsDeck.discardCard(powerUp);
                    return true;
                }
                return false;
            case RELOAD:
                if (action.getWeapon() == null) return false;
                return getActualPlayer().hasWeapon(action.getWeapon()) &&
                        !getActualPlayer().isALoadedGun(action.getWeapon()) &&
                        canPayWeaponAndPay(action.getWeapon(), action.getPowerUpPayment());
            case NEXT_TURN:
                nextTurn();
                return true;
            case REBORN:
                return reborn(action);
        }
        return false;
    }

    public static class Creator {
        @Contract(pure = true)
        private Creator() {
        }

        @Contract("_ -> new")
        public static @NotNull GameImpl newGame(@NotNull Room room) {
            assert(room.getUsers().size() >= MIN_PLAYERS && room.getUsers().size() <= MAX_PLAYERS) : "Invalid number of players";
            var cells = new Cell[MAX_X][MAX_Y];
            switch (room.getGameType().getLeft()) {
                case "L5":
                    cells[0][0] = Cell.Creator.withBounds("_ |_").color(Cell.Color.BLUE).create();
                    cells[1][0] = Cell.Creator.withBounds("| __").color(Cell.Color.RED).spawnPoint().create();
                    cells[2][0] = null;
                    cells[0][1] = Cell.Creator.withBounds("_ _ ").color(Cell.Color.BLUE).create();
                    switch (room.getGameType().getRight()) {
                        case "R5":
                            cells[1][1] = Cell.Creator.withBounds("_ _ ").color(Cell.Color.RED).create();
                            cells[2][1] = Cell.Creator.withBounds("| __").color(Cell.Color.WHITE).create();
                            break;
                        case "R6":
                            cells[1][1] = Cell.Creator.withBounds("___ ").color(Cell.Color.RED).create();
                            break;
                    }
                    break;
                case "L6":
                    cells[0][0] = Cell.Creator.withBounds("_| _").color(Cell.Color.BLUE).create();
                    cells[1][0] = Cell.Creator.withBounds(" _|_").color(Cell.Color.RED).spawnPoint().create();
                    cells[2][0] = Cell.Creator.withBounds("| __").color(Cell.Color.WHITE).create();
                    cells[0][1] = Cell.Creator.withBounds("_ ||").color(Cell.Color.BLUE).create();
                    switch (room.getGameType().getRight()) {
                        case "R5":
                            cells[1][1] = Cell.Creator.withBounds("| |_").color(Cell.Color.PURPLE).create();
                            cells[2][1] = Cell.Creator.withBounds("| _ ").color(Cell.Color.WHITE).create();
                            break;
                        case "R6":
                            cells[1][1] = Cell.Creator.withBounds("|_|_").color(Cell.Color.PURPLE).create();
                            break;
                    }
                    break;
            }
            switch (room.getGameType().getRight()) {
                case "R5":
                    cells[0][2] = Cell.Creator.withBounds("__| ").color(Cell.Color.BLUE).spawnPoint().create();
                    cells[1][2] = Cell.Creator.withBounds("||_ ").color(Cell.Color.PURPLE).create();
                    cells[2][2] = Cell.Creator.withBounds("_|_ ").color(Cell.Color.WHITE).create();
                    cells[0][3] = null;
                    cells[1][3] = Cell.Creator.withBounds("__ |").color(Cell.Color.YELLOW).create();
                    cells[2][3] = Cell.Creator.withBounds(" __|").color(Cell.Color.YELLOW).spawnPoint().create();
                    break;
                case "R6":
                    cells[2][1] = Cell.Creator.withBounds("||__").color(Cell.Color.WHITE).create();
                    cells[0][2] = Cell.Creator.withBounds("_|| ").color(Cell.Color.BLUE).spawnPoint().create();
                    cells[1][2] = Cell.Creator.withBounds("|  _").color(Cell.Color.YELLOW).create();
                    cells[2][2] = Cell.Creator.withBounds("  _|").color(Cell.Color.YELLOW).create();
                    cells[0][3] = Cell.Creator.withBounds("__||").color(Cell.Color.GREEN).create();
                    cells[1][3] = Cell.Creator.withBounds("|_  ").color(Cell.Color.YELLOW).create();
                    cells[2][3] = Cell.Creator.withBounds(" __ ").color(Cell.Color.YELLOW).spawnPoint().create();
                    break;
            }
            return new GameImpl(room.getUuid(), room.getGameType(), cells, room.getUsers().stream().map(Player::new).collect(Collectors.toList()), room.getSkulls());
        }
    }
}