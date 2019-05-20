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
import java.util.stream.Stream;

public class Player implements Serializable {
    private static final long serialVersionUID = 1;

    private @NotNull UUID uuid;
    private @NotNull String nickname;
    private @Nullable Point position;
    private @NotNull ArrayList<UUID> damagesTaken = new ArrayList<>();
    private @NotNull ArrayList<UUID> marksTaken = new ArrayList<>();
    private int deaths = 0;
    private int points = 0;
    private @NotNull int[] cubes = {3, 3, 3};
    private @NotNull ArrayList<PowerUp> powerUps = new ArrayList<>();
    private @NotNull HashMap<Weapon.Name, Boolean> weapons = new HashMap<>();
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

    public @NotNull ArrayList<UUID> getDamagesTaken() {
        return damagesTaken;
    }

    public @NotNull ArrayList<UUID> getSortedHitters() {
        ArrayList<UUID> sortedHitters = new ArrayList<>();
        for (var uuid : damagesTaken) if (!sortedHitters.contains(uuid)) sortedHitters.add(uuid);
        if (!sortedHitters.isEmpty()) {
            for (int i = 0; i < sortedHitters.size() - 1; i++) {
                int finalI = i;
                int iHits = (int) damagesTaken.parallelStream().filter(e -> e == sortedHitters.get(finalI)).count();
                for (int j = i + 1; j < sortedHitters.size(); j++) {
                    int finalJ = j;
                    int jHits = (int) damagesTaken.parallelStream().filter(e -> e == sortedHitters.get(finalJ)).count();
                    if (jHits > iHits || jHits == iHits &&
                            damagesTaken.indexOf(sortedHitters.get(j)) < damagesTaken.indexOf(sortedHitters.get(i))) {
                        UUID tmp = sortedHitters.get(i);
                        sortedHitters.set(i, sortedHitters.get(j));
                        sortedHitters.set(j, tmp);
                        iHits = jHits;
                    }
                }
            }
        }
        return sortedHitters;
    }

    public @NotNull ArrayList<UUID> getMarksTaken() {
        return marksTaken;
    }

    public int getMaximumPoints() {
        return 2 * deaths >= 8 ? 1 : 8 - 2 * deaths;
    }

    public void incrementDeaths() {
        deaths++;
    }

    public void addPoints(int pointsAdded) {
        points += pointsAdded;
    }

    public boolean hasPowerUp(@NotNull PowerUp powerUp) {
        return powerUps.contains(powerUp);
    }

    public boolean hasWeapon(@Nullable Weapon.Name weapon) {
        return weapons.get(weapon) != null;
    }

    public boolean isALoadedGun(@Nullable Weapon.Name weapon) {
        return hasWeapon(weapon) && weapons.get(weapon);
    }

    public void unloadWeapon(@Nullable Weapon.Name weapon) {
        weapons.put(weapon, false);
    }

    //gives damages, convert marks to damages and finally gives marks
    public void takeHits(@NotNull Game game, int damages, int marks) {
        for (int i = 0; i < damages; i++) {
            if (damagesTaken.size() < 12) damagesTaken.add(game.getActualPlayer().uuid);
        }
        if (damages > 0) {
            for (UUID hitterUuid : marksTaken) {
                if (game.getActualPlayer().uuid.equals(hitterUuid)) {
                    marksTaken.remove(hitterUuid);
                    if (damagesTaken.size() < 12) {
                        damagesTaken.add(game.getActualPlayer().uuid);
                        if (!game.lastsDamaged.contains(this)) game.lastsDamaged.add(this);
                    }
                }
            }
        }
        int oldMarks = 0;
        for (UUID hitterUuid : marksTaken) {
            if (game.getActualPlayer().uuid.equals(hitterUuid)) oldMarks++;
        }
        for (int i = 0; i < marks && oldMarks + i < 3; i++) {
            marksTaken.add(game.getActualPlayer().uuid);
        }
    }

    public void addDamage(@NotNull Player hitter) {
        if (damagesTaken.size() < 12) damagesTaken.add(hitter.getUuid());
    }

    public void addMark(@NotNull Player avenger) {
        if (marksTaken.parallelStream().filter(e -> e == avenger.getUuid()).count() < 3)
            marksTaken.add(avenger.getUuid());
    }

    public void setPlayed() {
        isFirstMove = false;
    }

    public boolean isFirstMove() {
        return isFirstMove;
    }

    private void addCube(@NotNull AmmoCard.Color color) {
        if (cubes[color.getIndex()] < 3) cubes[color.getIndex()]++;
    }

    public int getColoredCubes(@NotNull AmmoCard.Color color) {
        return cubes[color.getIndex()];
    }

    public void removeColoredCubes(@NotNull AmmoCard.Color color, int number) {
        cubes[color.getIndex()] -= number;
    }

    public void removePowerUp(@NotNull PowerUp powerUp) {
        powerUps.remove(powerUp);
        //TODO spostare powerUps nelle carte scartate
    }

    public void addPowerUp(@NotNull PowerUp powerUp) {
        powerUps.add(powerUp);
    }

    public void addWeapon(Weapon.Name weapon) {
        weapons.put(weapon, true);
    }

    public void removeWeapon(Weapon.Name weapon) {
        weapons.remove(weapon);
    }

    public int getWeaponsSize() {
        return weapons.size();
    }

    public void ammoCardRecharging(@NotNull AmmoCard ammoCard, @Nullable PowerUp powerUp) {
        switch (ammoCard.getType()) {
            case POWER_UP:
                addPowerUp(powerUp);
                break;
            case RED:
                addCube(AmmoCard.Color.RED);
                break;
            case YELLOW:
                addCube(AmmoCard.Color.YELLOW);
                break;
            case BLUE:
                addCube(AmmoCard.Color.BLUE);
                break;
        }
        addCube(ammoCard.getRight());
        addCube(ammoCard.getLeft());
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

    public boolean isPointAtMaxDistanceInDirection(@NotNull Point point, @NotNull Cell[][] cells, int maxDistance, @NotNull Bounds.Direction direction) {
        int countDistance = 0, x = position.x, y = position.y;
        while (countDistance < maxDistance) {
            if (cells[x][y].getBounds().getType(direction) == Bounds.Type.WALL) return false;
            countDistance++;
            x += direction.getdX();
            y += direction.getdY();
            if (x == point.getX() && y == point.getY()) return true;
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

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Player && ((Player) obj).uuid.equals(uuid);
    }
}