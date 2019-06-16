package it.polimi.ingsw.server.models;

import it.polimi.ingsw.common.models.*;
import it.polimi.ingsw.common.models.exceptions.ActionDeniedException;
import it.polimi.ingsw.common.models.wrappers.Opt;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameImpl extends Game implements Serializable {
    private static final long serialVersionUID = 1;

    private @NotNull HashMap<UUID, Integer> hashKillshotsTrack = new HashMap<>();

    private @NotNull Deck<AmmoCard> ammoDeck = Deck.Creator.newAmmoDeck();
    private @NotNull Deck<PowerUp> powerUpsDeck = Deck.Creator.newPowerUpsDeck();
    private @NotNull Deck<Weapon> weaponsDeck = Deck.Creator.newWeaponsDeck();

    private @NotNull ArrayList<PowerUp> exitedPowerUps = new ArrayList<>();

    private GameImpl(@NotNull UUID uuid, @NotNull Type type, @NotNull Cell[][] cells, @NotNull List<Player> players, int skulls) {
        super(uuid, type, cells, players, skulls);
        redWeapons = new ArrayList<>(weaponsDeck.exitCards(3));
        blueWeapons = new ArrayList<>(weaponsDeck.exitCards(3));
        yellowWeapons = new ArrayList<>(weaponsDeck.exitCards(3));
        Stream.of(cells).flatMap(Stream::of).filter(Objects::nonNull).filter(e -> !e.isSpawnPoint()).forEach(e -> e.setAmmoCard(ammoDeck.exitCard()));
    }

    List<PowerUp> getFirstMoveColors() {
        exitedPowerUps.addAll(powerUpsDeck.exitCards(2));
        return exitedPowerUps;
    }

    void completeFirstMove(PowerUp cardToThrow) {
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

    private boolean grabWeapon(@NotNull Point to, @NotNull Weapon weapon, @Nullable Weapon discardedWeaponName, @Nullable List<PowerUp> powerUpPayment) {
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

    private boolean canPayWeaponAndPay(@NotNull Weapon weapon, @Nullable List<PowerUp> powerUpPayment) {
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
        var weapon = WeaponImpl.Loader.build(action.getWeapon(), this, action.getAlternativeFire());
        if (action.getBasicTarget() != null) action.getBasicTarget().forEach(weapon::addBasicTarget);
        if (action.getBasicTargetPoint() != null) weapon.setBasicTargetsPoint(action.getBasicTargetPoint());
        if (action.getPowerUpPayment() != null) weapon.setAlternativePaymentToUse(action.getPowerUpPayment());
        if ((action.getOptions() & WeaponImpl.FIRST) == 1) {
            if (action.getFirstAdditionalTarget() != null)
                action.getFirstAdditionalTarget().forEach(weapon::addFirstAdditionalTarget);
            if (action.getFirstAdditionalTargetPoint() != null)
                weapon.setFirstAdditionalTargetsPoint(action.getFirstAdditionalTargetPoint());
        }
        if ((action.getOptions() & WeaponImpl.SECOND) == 2) {
            if (action.getSecondAdditionalTarget() != null)
                action.getSecondAdditionalTarget().forEach(weapon::addSecondAdditionalTarget);
            if (action.getSecondAdditionalTargetPoint() != null)
                weapon.setSecondAdditionalTargetsPoint(action.getSecondAdditionalTargetPoint());
        }
        if (weapon.fire(action.getOptions())) {
            weapon.getAlternativePaymentUsed().forEach(e -> powerUpsDeck.discardCard(e));
            return true;
        }
        return false;
    }

    private void nextTurn() {
        final int beforeSkulls = skulls;
        for (var cells : cells)
            for (var cell : cells) {
                if (!cell.isSpawnPoint() && cell.getAmmoCard() == null) cell.setAmmoCard(ammoDeck.exitCard());
                if (cell.isSpawnPoint() && getWeapons(cell.getColor()).size() < 3 && weaponsDeck.remainedCards() > 0)
                    addWeapon(cell.getColor(), weaponsDeck.exitCard());
            }
        addReborningPlayers();
        deathPointsRedistribution();
        if (beforeSkulls > 0 && skulls == 0) players.forEach(Player::setEasyBoard);
        if (skulls == 0 && seqPlay % (players.size() - 1) == 0) lastTurn = true;
        if (responsivePlayers.isEmpty()) seqPlay++;
    }

    private void deathPointsRedistribution() {
        ArrayList<Player> deadPlayers = new ArrayList<>();
        players.parallelStream().filter(e -> getDeadPlayers().contains(e.getUuid())).forEach(deadPlayers::add);
        getActualPlayer().addPoints(deadPlayers.size() > 1 ? 1 : 0);
        deadPlayers.forEach(e -> e.getSortedHitters().forEach(f -> getPlayers().parallelStream().filter(g -> g.getUuid() == f)
                .forEachOrdered(g -> {
                    g.addPoints(2 * e.getSortedHitters().indexOf(f) >= e.getMaximumPoints() ? 1 : e.getMaximumPoints() - 2 * e.getSortedHitters().indexOf(f));
                    g.addPoints(e.getSortedHitters().indexOf(f) == 0 ? 1 : 0);
                    if (e.getDamagesTaken().size() == 12 && f == e.getDamagesTaken().get(11)) {
                        e.addMark(g);
                        addToKillshotsTrack(f);
                    }
                    if (f == e.getDamagesTaken().get(10)) addToKillshotsTrack(f);
                })));
        deadPlayers.forEach(e -> {
            e.manageDeath();
            if (skulls > 0) skulls--;
        });
    }

    /**
     * when the game ends distributes the lasts points to players
     */
    void finalPointsRedistribution() {
        players.parallelStream().filter(e -> !e.getDamagesTaken().isEmpty()).forEachOrdered(e -> e.getSortedHitters().forEach(f -> getPlayers().parallelStream()
                .filter(g -> g.getUuid() == f).forEachOrdered(g -> {
                    g.addPoints(2 * e.getSortedHitters().indexOf(f) >= e.getMaximumPoints() ? 1 : e.getMaximumPoints() - 2 * e.getSortedHitters().indexOf(f));
                    g.addPoints(e.getSortedHitters().indexOf(f) == 0 ? 1 : 0);
                })));
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
     * It builds the ranking as an ArrayList<ArrayList<UUID>>
     * <p>
     * Example if two players are tied:
     * 1. firstPlayer, otherFirstPlayer;
     * 2. none;
     * 3. thirdPlayer;
     * 4. fourthPlayer;
     * 5. fifthPlayer;
     *
     * @return the HashMap
     */
    @NotNull ArrayList<ArrayList<UUID>> getRanking() {
        class PlayerPoint implements Comparable<PlayerPoint> {
            private @NotNull UUID playerUuid;
            private int points;
            private int killshots;
            private int firstKillshotPosition;

            private PlayerPoint(@NotNull UUID playerUuid, int points) {
                this.playerUuid = playerUuid;
                this.points = points;
                this.killshots = getPlayerKillshots(playerUuid);
                this.firstKillshotPosition = arrayKillshotsTrack.indexOf(playerUuid);
            }

            @Override
            public int compareTo(@NotNull final PlayerPoint other) {
                if (this.points != other.points) return Integer.compare(other.points, this.points);
                else if (this.killshots != other.killshots) return Integer.compare(other.killshots, this.killshots);
                else if (this.killshots != 0)
                    return Integer.compare(this.firstKillshotPosition, other.firstKillshotPosition);
                else return 0;
            }
        }

        ArrayList<ArrayList<UUID>> ranking = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) ranking.add(new ArrayList<>());
        ArrayList<PlayerPoint> tmpRanking = new ArrayList<>();
        players.forEach(e -> tmpRanking.add(new PlayerPoint(e.getUuid(), e.getPoints())));
        Collections.sort(tmpRanking);
        int pos = 0;
        for (int from = 0; from < tmpRanking.size(); from++) {
            int to = from + 1;
            while (to < tmpRanking.size() && tmpRanking.get(from).points == tmpRanking.get(to).points &&
                    tmpRanking.get(from).killshots == 0 && tmpRanking.get(to).killshots == 0) to++;
            for (; from < to; from++) ranking.get(pos).add(tmpRanking.get(from).playerUuid);
            pos += ranking.get(pos).size();
        }
        return ranking;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    private boolean reborn(@NotNull Action action) {
        if (action.getColor() == null || action.getPowerUpType() == null) return false;
        var powerUp = new PowerUp(action.getColor(), action.getPowerUpType());
        if (getActualPlayer().getPowerUps().contains(powerUp)) {
            powerUpsDeck.discardCard(powerUp);
            getActualPlayer().getPowerUps().remove(powerUp);
            for (int x = 0; x < 4; x++)
                for (int y = 0; y < 3; y++) {
                    var cell = getCell(new Point(x, y));
                    if (cell != null && cell.isSpawnPoint()) {
                        switch (cell.getColor()) {
                            case RED:
                                if (powerUp.getAmmoColor().equals(AmmoCard.Color.RED)) {
                                    getActualPlayer().setPosition(new Point(x, y));
                                    responsivePlayers.remove(0);
                                    return true;
                                }
                                break;
                            case YELLOW:
                                if (powerUp.getAmmoColor().equals(AmmoCard.Color.YELLOW)) {
                                    getActualPlayer().setPosition(new Point(x, y));
                                    responsivePlayers.remove(0);
                                    return true;
                                }
                                break;
                            case BLUE:
                                if (powerUp.getAmmoColor().equals(AmmoCard.Color.BLUE)) {
                                    getActualPlayer().setPosition(new Point(x, y));
                                    responsivePlayers.remove(0);
                                    return true;
                                }
                                break;
                            default:
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
                if (!responsivePlayers.isEmpty()) throw new ActionDeniedException();
                if (action.getDestination() == null) return false;
                return moveTo(action.getDestination());
            case GRAB_WEAPON:
                if (!responsivePlayers.isEmpty()) throw new ActionDeniedException();
                if (action.getWeapon() == null) return false;
                return grabWeapon(Opt.of(action.getDestination()).get(getActualPlayer().getPosition()),
                        action.getWeapon(), action.getDiscardedWeapon(), action.getPowerUpPayment());
            case GRAB_AMMOCARD:
                if (!responsivePlayers.isEmpty()) throw new ActionDeniedException();
                return grabAmmoCard(Opt.of(action.getDestination()).get(getActualPlayer().getPosition()));
            case FIRE:
                Point mockPosition = new Point(getActualPlayer().getPosition());
                if (!responsivePlayers.isEmpty()) throw new ActionDeniedException();
                if (action.getWeapon() != null && getActualPlayer().hasWeapon(action.getWeapon()) &&
                        (getActualPlayer().isALoadedGun(action.getWeapon()) || skulls == 0)) {
                    if (action.getDestination() != null && (getActualPlayer().getDamagesTaken().size() >= 6 &&
                            canMove(getActualPlayer().getPosition(), action.getDestination(), 1)) ||
                            (skulls == 0 && canMove(getActualPlayer().getPosition(), action.getDestination(), 1)) ||
                            (lastTurn && canMove(getActualPlayer().getPosition(), action.getDestination(), 2)))
                        getActualPlayer().setPosition(action.getDestination());
                    if (fireAction(action)) {
                        addTagbackPlayers();
                        return true;
                    } else getActualPlayer().setPosition(mockPosition);
                }
                return false;
            case USE_POWER_UP:
                if (action.getColor() == null || action.getPowerUpType() == null) return false;
                if (!action.getPowerUpType().equals(PowerUp.Type.TAGBACK_GRENADE) && !responsivePlayers.isEmpty())
                    throw new ActionDeniedException();
                var powerUp = new PowerUp(action.getColor(), action.getPowerUpType());
                getPlayers().stream().filter(e -> e.getUuid().equals(action.getTarget())).forEach(powerUp::setTarget);
                powerUp.setTargetPoint(action.getDestination());
                if (powerUp.use(this)) {
                    getActualPlayer().removePowerUp(powerUp);
                    powerUpsDeck.discardCard(powerUp);
                    if (powerUp.getType().equals(PowerUp.Type.TAGBACK_GRENADE)) responsivePlayers.remove(0);
                    return true;
                }
                return false;
            case RELOAD:
                if (!responsivePlayers.isEmpty()) throw new ActionDeniedException();
                if (action.getWeapon() == null) return false;
                if (getActualPlayer().hasWeapon(action.getWeapon()) &&
                        !getActualPlayer().isALoadedGun(action.getWeapon()) &&
                        canPayWeaponAndPay(action.getWeapon(), action.getPowerUpPayment())) {
                    getActualPlayer().reloadWeapon(action.getWeapon());
                    return true;
                }
                return false;
            case NEXT_TURN:
                if (!responsivePlayers.isEmpty()) throw new ActionDeniedException();
                nextTurn();
                return true;
            case REBORN:
                if (!isAReborn()) throw new ActionDeniedException();
                return reborn(action);
            default:
                break;
        }
        return false;
    }

    private void addTagbackPlayers() {
        responsivePlayers.addAll(getTagbackPlayers());
    }

    private void addReborningPlayers() {
        responsivePlayers.addAll(getDeadPlayers());
    }

    private ArrayList<UUID> getDeadPlayers() {
        ArrayList<UUID> deadPlayers = new ArrayList<>();
        getPlayers().parallelStream().filter(e -> e.getDamagesTaken().size() >= 11).map(Player::getUuid).forEach(deadPlayers::add);
        return deadPlayers;
    }

    private void addToKillshotsTrack(UUID uuid) {
        if (!hashKillshotsTrack.containsKey(uuid)) {
            hashKillshotsTrack.put(uuid, 1);
            arrayKillshotsTrack.add(uuid);
        } else hashKillshotsTrack.put(uuid, hashKillshotsTrack.get(uuid) + 1);
    }

    private @NotNull ArrayList<UUID> getSortedKillshooters() {
        for (int i = 0; i < arrayKillshotsTrack.size() - 1; i++) {
            for (int j = i + 1; j < arrayKillshotsTrack.size(); j++) {
                if (hashKillshotsTrack.get(arrayKillshotsTrack.get(i)) < hashKillshotsTrack.get(arrayKillshotsTrack.get(j))) {
                    var tmp = arrayKillshotsTrack.get(i);
                    arrayKillshotsTrack.set(i, arrayKillshotsTrack.get(j));
                    arrayKillshotsTrack.set(j, tmp);
                }
            }
        }
        return arrayKillshotsTrack;
    }

    private int getPlayerKillshots(@NotNull UUID uuid) {
        return hashKillshotsTrack.get(uuid);
    }


    private void addWeapon(@NotNull Cell.Color color, @NotNull Weapon weapon) {
        switch (color) {
            case BLUE:
                if (blueWeapons.size() < 3) blueWeapons.add(weapon);
                break;
            case RED:
                if (redWeapons.size() < 3) redWeapons.add(weapon);
                break;
            case YELLOW:
                if (yellowWeapons.size() < 3) yellowWeapons.add(weapon);
                break;
            default:
                break;
        }
    }

    private void removeWeapon(@NotNull Cell.Color color, @NotNull Weapon weapon) {
        switch (color) {
            case BLUE:
                blueWeapons.remove(weapon);
                break;
            case RED:
                redWeapons.remove(weapon);
                break;
            case YELLOW:
                yellowWeapons.remove(weapon);
                break;
            default:
                break;
        }
    }

    public static class Creator {
        @Contract(pure = true)
        private Creator() {
        }

        //TODO: impl
        @Contract("_ -> new")
        public static @NotNull GameImpl newGame(@NotNull Room room) {
            var cells = new Cell[MAX_X][MAX_Y];
            switch (room.getGameType().getLeft()) {
                case "L5":
                    cells[0][0] = Cell.Creator.withBounds("_ |_").color(Cell.Color.BLUE).create();
                    cells[0][1] = Cell.Creator.withBounds("| __").color(Cell.Color.RED).spawnPoint().create();
                    cells[0][2] = null;
                    cells[1][0] = Cell.Creator.withBounds("_ _ ").color(Cell.Color.BLUE).create();
                    switch (room.getGameType().getRight()) {
                        case "R5":
                            cells[1][1] = Cell.Creator.withBounds("_ _ ").color(Cell.Color.RED).create();
                            cells[1][2] = Cell.Creator.withBounds("| __").color(Cell.Color.WHITE).create();
                            break;
                        case "R6":
                            cells[1][1] = Cell.Creator.withBounds("___ ").color(Cell.Color.RED).create();
                            break;
                    }
                    break;
                case "L6":
                    cells[0][0] = Cell.Creator.withBounds("_| _").color(Cell.Color.BLUE).create();
                    cells[0][1] = Cell.Creator.withBounds(" _|_").color(Cell.Color.RED).spawnPoint().create();
                    cells[0][2] = Cell.Creator.withBounds("| __").color(Cell.Color.WHITE).create();
                    cells[1][0] = Cell.Creator.withBounds("_ ||").color(Cell.Color.BLUE).create();
                    switch (room.getGameType().getRight()) {
                        case "R5":
                            cells[1][1] = Cell.Creator.withBounds("| |_").color(Cell.Color.PURPLE).create();
                            cells[1][2] = Cell.Creator.withBounds("| _ ").color(Cell.Color.WHITE).create();
                            break;
                        case "R6":
                            cells[1][1] = Cell.Creator.withBounds("|_|_").color(Cell.Color.PURPLE).create();
                            break;
                    }
                    break;
            }
            switch (room.getGameType().getRight()) {
                case "R5":
                    cells[2][0] = Cell.Creator.withBounds("__| ").color(Cell.Color.BLUE).spawnPoint().create();
                    cells[2][1] = Cell.Creator.withBounds("||_ ").color(Cell.Color.PURPLE).create();
                    cells[2][2] = Cell.Creator.withBounds("_|_ ").color(Cell.Color.WHITE).create();
                    cells[3][0] = null;
                    cells[3][1] = Cell.Creator.withBounds("__ |").color(Cell.Color.YELLOW).create();
                    cells[3][2] = Cell.Creator.withBounds(" __|").color(Cell.Color.YELLOW).spawnPoint().create();
                    break;
                case "R6":
                    cells[1][2] = Cell.Creator.withBounds("||__").color(Cell.Color.WHITE).create();
                    cells[2][0] = Cell.Creator.withBounds("_|| ").color(Cell.Color.BLUE).spawnPoint().create();
                    cells[2][1] = Cell.Creator.withBounds("|  _").color(Cell.Color.YELLOW).create();
                    cells[2][2] = Cell.Creator.withBounds("  _|").color(Cell.Color.YELLOW).create();
                    cells[3][0] = Cell.Creator.withBounds("__||").color(Cell.Color.GREEN).create();
                    cells[3][1] = Cell.Creator.withBounds("|_  ").color(Cell.Color.YELLOW).create();
                    cells[3][2] = Cell.Creator.withBounds(" __ ").color(Cell.Color.YELLOW).spawnPoint().create();
                    break;
            }
            var random = new SecureRandom();
            return new GameImpl(room.getUuid(), room.getGameType(), cells, room.getUsers().stream()
                    .map(e -> new Player(e, Player.BoardType.values()[random.nextInt(Player.BoardType.values().length)]))
                    .collect(Collectors.toList()), room.getSkulls());
        }
    }
}