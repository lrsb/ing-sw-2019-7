package it.polimi.ingsw.common.models;

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

public class Player implements Serializable {
    private static final long serialVersionUID = 1;

    private @NotNull UUID uuid;
    private @NotNull String nickname;
    private @Nullable Point position;
    private @NotNull ArrayList<String> damagesTaken = new ArrayList<>();
    private @NotNull ArrayList<String> marksTaken = new ArrayList<>();
    private int deaths = 0;
    private int points = 0;
    private @NotNull int[] cubes = {3, 3, 3};
    private @NotNull ArrayList<PowerUp> powerUps = new ArrayList<>();
    private @NotNull HashMap<Weapon.Name, Boolean> weapons = new HashMap<>();
    private @NotNull ArrayList<AmmoCard> ammoCards = new ArrayList<>();
    private boolean isFirstMove = true;

    public Player(@NotNull User user) {
        this.uuid = user.getUuid();
        this.nickname = user.getNickname();
    }

    public @NotNull UUID getUuid() {
        return uuid;
    }

    public @NotNull String getNickname() {
        return nickname;
    }

    public @NotNull ArrayList<String> getDamagesTaken() {
        return damagesTaken;
    }

    public int getDeaths() {
        return deaths;
    }

    public void incrementDeaths() {
        deaths++;
    }

    public void addPoints(int pointsAdded) {
        points += pointsAdded;
    }

    public boolean hasWeapon(@Nullable Weapon.Name weapon) {
        return weapons.get(weapon) != null;
    }

    public boolean isALoadedGun(@Nullable Weapon.Name weapon) {
        return hasWeapon(weapon) && weapons.get(weapon);
    }

    public boolean unloadWeapon(@Nullable Weapon.Name weapon) {
        return hasWeapon(weapon) && weapons.put(weapon, false) != null;
    }

    //gives damages, convert marks to damages and finally gives marks
    public void takeHits(@NotNull Player shooter, int damages, int marks) {
        for (int i = 0; i < damages; i++) {
            if (damagesTaken.size() < 12) damagesTaken.add(shooter.nickname);
        }
        if (damages > 0) {
            for (String name : marksTaken) {
                if (shooter.nickname.equals(name)) {
                    marksTaken.remove(name);
                    if (damagesTaken.size() < 12) damagesTaken.add(shooter.nickname);
                }
            }
        }
        int oldMarks = 0;
        for (String name : marksTaken) {
            if (shooter.nickname.equals(name)) oldMarks++;
        }
        for (int i = 0; i < marks && oldMarks + i < 3; i++) {
            marksTaken.add(shooter.nickname);
        }
    }

    //use this at each other's player end turn
    public boolean amIDead() {
        return damagesTaken.size() >= 11;
    }

    //controllo se l'arma che vuole caricare sia in suo possesso e che non sia già carica
    public boolean reload(Weapon.Name weapon) {
        for (Weapon.Name w : weapons.keySet()) {
            if (w.getClass().equals(weapon.getWeaponClass()) && !(isReloaded[weapons.indexOf(w)])) {
                if (w.chargingOrGrabbing()) {
                    isReloaded[weapons.indexOf(w)] = true;
                    return true;
                }
            }
        }
        return false;
    }

    public void setPlayed() {
        isFirstMove = false;
    }

    public boolean isFirstMove() {
        return isFirstMove;
    }

    public int getColoredCubes(@NotNull AmmoCard.Color color) {
        return cubes[color.getIndex()];
    }

    //removes ammos when player has to pay a cost
    //!!!A PLAYER CAN PAY EVEN WITH POWERUPS!!!
    public void removeColoredCubes(@NotNull AmmoCard.Color color, int number) {
        cubes[color.getIndex()] -= number;
    }

    public void removePowerUp(@NotNull PowerUp powerUp) {
        powerUps.remove(powerUp);
        //TODO spostare powerUps nelle carte scartate
    }

    public void addPowerUp(PowerUp powerUp) {
        assert powerUps.size() < 3;
        powerUps.add(powerUp);
    }

    public void addWeapon(Weapon.Name weapon) {
        assert weapons.size() < 3;
        //TODO: e gia carica?
        weapons.put(weapon, false);
    }

    public void addAmmoCard(AmmoCard ammoCard) {
        assert ammoCards.size() < 3;
        ammoCards.add(ammoCard);
    }

    public @NotNull List<PowerUp> getPowerUps() {
        return powerUps;
    }

    public @Nullable Point getPosition() {
        return position;
    }

    public void setPosition(@Nullable Point position) {
        this.position = position;
    }

    @Contract(pure = true)
    public boolean canSeeNotSame(@NotNull Player player, @NotNull Cell[][] cells) {
        if (equals(player) || position == null || player.position == null) return false;
        if (cells[position.x][position.y].getColor() == cells[player.position.x][player.position.y].getColor())
            return true;
        return Stream.of(Bounds.Direction.values()).filter(e -> cells[position.x][position.y].getBounds().getType(e) == Bounds.Type.DOOR)
                .anyMatch(e -> cells[position.x + e.getdX()][position.y + e.getdY()].getColor() ==
                        cells[player.position.x][player.position.y].getColor());
    }

    public @NotNull List<Player> getVisiblePlayers(@NotNull Game game) {
        return game.getPlayers().parallelStream()
                .filter(e -> !game.getActualPlayer().equals(e) && game.getActualPlayer().canSeeNotSame(e, game.getCells())).collect(Collectors.toList());
    }

    public boolean canBeSeenFrom(@NotNull Point point, @NotNull Cell[][] cells) {
        if (cells[point.x][point.y].getColor() == cells[position.x][position.y].getColor()) return true;
        for (Bounds.Direction d : Bounds.Direction.values()) {
            if (cells[point.x][point.y].getBounds().getType(d) != Bounds.Type.WALL &&
                    cells[position.x][position.y].getColor() !=
                            cells[point.x + d.getdX()][point.y + d.getdY()].getColor()) return true;
        }
        return false;
    }

    public boolean isPlayerNear(@NotNull Player player, @NotNull Cell[][] cells) {
        for (Bounds.Direction d : Bounds.Direction.values()) {
            if (cells[position.x][position.y].getBounds().getType(d) != Bounds.Type.WALL &&
                    player.position.x == position.x + d.getdX() && player.position.y == position.y + d.getdY())
                return true;
        }
        return false;
    }

    public boolean isPlayerNear2(@NotNull Player player, @NotNull Cell[][] cells) {
        for (Bounds.Direction d : Bounds.Direction.values()) {
            if (cells[position.x][position.y].getBounds().getType(d) != Bounds.Type.WALL) {
                if (cells[position.x + d.getdX()][position.y + d.getdY()].getBounds().getType(d) != Bounds.Type.WALL &&
                        player.position.x == position.x + 2 * d.getdX() &&
                        player.position.y == position.y + 2 * d.getdY()) return true;
            }
        }
        return false;
    }

    public boolean canSeeCell(@NotNull Point point, @NotNull Cell[][] cells) {
        if (cells[position.x][position.y].getColor() == cells[point.x][point.y].getColor()) return true;
        for (var direction : Bounds.Direction.values())
            if (cells[position.x][position.y].getBounds().getType(direction) == Bounds.Type.DOOR &&
                    cells[position.x + direction.getdX()][position.y + direction.getdY()].getColor()
                            == cells[point.x][point.y].getColor()) return true;
        return false;
    }

    public boolean isCellNear(@NotNull Point point, @NotNull Cell[][] cells) {
        for (Bounds.Direction d : Bounds.Direction.values()) {
            if (cells[position.x][position.y].getBounds().getType(d) != Bounds.Type.WALL &&
                    point.x == position.x + d.getdX() && point.y == position.y + d.getdY())
                return true;
        }
        return false;
    }

    public boolean isCellNear2Straight(@NotNull Point point, @NotNull Cell[][] cells) {
        for (Bounds.Direction d : Bounds.Direction.values()) {
            if (cells[position.x][position.y].getBounds().getType(d) != Bounds.Type.WALL) {
                if (cells[position.x + d.getdX()][position.y + d.getdY()].getBounds().getType(d) != Bounds.Type.WALL &&
                        point.x == position.x + 2 * d.getdX() &&
                        point.y == position.y + 2 * d.getdY()) return true;
            }
        }
        return false;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Player && ((Player) obj).uuid.equals(uuid);
    }
}