package it.polimi.ingsw.common.models;

import it.polimi.ingsw.common.others.Displayable;
import it.polimi.ingsw.common.others.Utils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * In this class all the information about the state of a player, like name, position, items owned etc...
 */
public class Player implements Displayable, Serializable {
    private static final long serialVersionUID = 1;

    private @NotNull UUID uuid;
    private @NotNull String nickname;
    private @Nullable Point position;
    private @NotNull BoardType boardType;
    private @NotNull ArrayList<UUID> damagesTaken = new ArrayList<>();
    private @NotNull ArrayList<UUID> marksTaken = new ArrayList<>();
    private int deaths = 0;
    private int points = 0;
    private @NotNull int[] cubes = {1, 1, 1};
    private @NotNull ArrayList<PowerUp> powerUps = new ArrayList<>();
    private @NotNull HashMap<Weapon, Boolean> weapons = new HashMap<>();
    private boolean easyBoard = false;

    /**
     * @param user      the user
     * @param boardType the player board
     */
    public Player(@NotNull User user, @NotNull BoardType boardType) {
        this.uuid = user.getUuid();
        this.nickname = user.getNickname();
        this.boardType = boardType;
    }

    /**
     * @return the board of the player
     */
    public @NotNull BoardType getBoardType() {
        return boardType;
    }

    /**
     * @return the identifier of the player
     */
    public @NotNull UUID getUuid() {
        return uuid;
    }

    /**
     * @return the nickname of the player
     */
    public @NotNull String getNickname() {
        return nickname;
    }

    /**
     * @return a list of the weapons owned by the player
     */
    public @NotNull List<Weapon> getWeapons() {
        return new ArrayList<>(weapons.keySet());
    }

    /**
     * @return a list of identifiers of players that damaged this player
     */
    public @NotNull List<UUID> getDamagesTaken() {
        return damagesTaken;
    }

    /**
     * @return the players that hit this player ordered from the player who deserves the higher reward to the one
     * who deserves the lower
     */
    public @NotNull List<UUID> getSortedHitters() {
        return damagesTaken.parallelStream().distinct().sorted((e, f) -> {
            var diff = damagesTaken.parallelStream().filter(f::equals).count() -
                    damagesTaken.parallelStream().filter(e::equals).count();
            return diff == 0 ? damagesTaken.indexOf(e) - damagesTaken.indexOf(f) : (int) diff;
        }).collect(Collectors.toList());
    }

    /**
     * @return a list of identifiers of the players that gave a mark to this player
     */
    public @NotNull List<UUID> getMarksTaken() {
        return marksTaken;
    }

    /**
     * @return number of deaths of this player till now
     */
    public int getDeaths() {
        return deaths;
    }

    /**
     * set the board of a player to the one with lower rewards
     */
    public void setEasyBoard() {
        if (getDamagesTaken().isEmpty()) easyBoard = true;
    }

    /**
     * @return true if he has the "easy board", false otherwise
     */
    public boolean isEasyBoard() {
        return easyBoard;
    }

    /**
     * @return the number of points that deserves the player who did the best scoring to this player
     */
    public int getMaximumPoints() {
        if (isEasyBoard()) return 2;
        else return 2 * deaths >= 8 ? 1 : 8 - 2 * deaths;
    }

    /**
     * increments of 1 the number of deaths and clear the damages taken
     */
    public void manageDeath() {
        deaths++;
        damagesTaken.clear();
    }

    /**
     * add points to this player
     *
     * @param pointsAdded the number of points to add
     */
    public void addPoints(int pointsAdded) {
        points += pointsAdded;
    }

    /**
     * @return points achieved by this player till now
     */
    public int getPoints() {
        return points;
    }

    /**
     * @param powerUp a generic power up
     * @return true if this player has @powerUp, false otherwise
     */
    public boolean hasPowerUp(@NotNull PowerUp powerUp) {
        return powerUps.contains(powerUp);
    }

    /**
     * @param weapon a generic weapon
     * @return true if this player has @weapon, false otherwise
     */
    public boolean hasWeapon(@Nullable Weapon weapon) {
        return weapons.get(weapon) != null;
    }

    /**
     * @param weapon a generic weapon
     * @return true if this player has this @weapon and it's reloaded
     */
    public boolean isALoadedGun(@Nullable Weapon weapon) {
        return hasWeapon(weapon) && weapons.get(weapon);
    }

    /**
     * unload @weapon, if owned by the player, nothing otherwise
     *
     * @param weapon a generic weapon
     */
    public void unloadWeapon(@Nullable Weapon weapon) {
        if (hasWeapon(weapon)) weapons.put(weapon, false);
    }

    /**
     * reload @weapon, if owned by the player, nothing otherwise
     *
     * @param weapon a generic weapon
     */
    public void reloadWeapon(@Nullable Weapon weapon) {
        if (hasWeapon(weapon)) weapons.put(weapon, true);
    }

    /**
     * Manage the hit suffered from the player, adding the player who did the damage to an appropriated list
     *
     * @param game    the game
     * @param damages number of damages, determined by the weapon used
     * @param marks   number of marks, determined by the weapon used
     */
    public void takeHits(@NotNull Game game, int damages, int marks) {
        for (int i = 0; i < damages; i++) {
            if (damagesTaken.size() < 12) damagesTaken.add(game.getActualPlayer().uuid);
        }
        if (damages > 0) {
            game.addToLastsDamaged(this);
            for (int i = 0; i < marksTaken.size(); i++) {
                if (game.getActualPlayer().uuid.equals(marksTaken.get(i))) {
                    marksTaken.remove(i);
                    i--;
                    if (damagesTaken.size() < 12) damagesTaken.add(game.getActualPlayer().uuid);
                }
            }
        }
        int oldMarks = 0;
        for (UUID value : marksTaken) {
            if (game.getActualPlayer().uuid.equals(value)) oldMarks++;
        }
        for (int i = 0; i < marks && oldMarks + i < 3; i++) {
            marksTaken.add(game.getActualPlayer().uuid);
        }
    }

    void addDamage(@NotNull Player hitter) {
        if (damagesTaken.size() < 12) damagesTaken.add(hitter.getUuid());
    }

    /**
     * give a mark of @avenger to this player
     *
     * @param avenger a player
     */
    public void addMark(@NotNull Player avenger) {
        if (marksTaken.parallelStream().filter(e -> e.equals(avenger.getUuid())).count() < 3)
            marksTaken.add(avenger.getUuid());
    }

    private void addCube(@NotNull AmmoCard.Color color) {
        if (cubes[color.getIndex()] < 3) cubes[color.getIndex()]++;
    }

    /**
     * @param color one of the cubes color
     * @return the number of cubes fo the color @color owned by the player
     */
    public int getColoredCubes(@NotNull AmmoCard.Color color) {
        return cubes[color.getIndex()];
    }

    /**
     * removes @number cubes of color @color from player
     *
     * @param color  one of the cubes color
     * @param number a integer
     */
    public void removeColoredCubes(@NotNull AmmoCard.Color color, int number) {
        if (number <= cubes[color.getIndex()]) cubes[color.getIndex()] -= number;
        else cubes[color.getIndex()] = 0;
    }

    /**
     * if this player own this @powerUp, removes it
     *
     * @param powerUp a power up
     */
    public void removePowerUp(@NotNull PowerUp powerUp) {
        powerUps.remove(powerUp);
    }

    /**
     * add @powerUp to this player
     *
     * @param powerUp a power up
     */
    public void addPowerUp(@NotNull PowerUp powerUp) {
        powerUps.add(powerUp);
    }

    /**
     * add @weapon to this player
     *
     * @param weapon a weapon
     */
    public void addWeapon(Weapon weapon) {
        weapons.put(weapon, true);
    }

    /**
     * takes away @weapon from this player
     *
     * @param weapon a weapon
     */
    public void removeWeapon(Weapon weapon) {
        weapons.remove(weapon);
    }

    /**
     * recharge this player with elements of the tile @ammoCard
     *
     * @param ammoCard a tile
     * @param powerUp  a power up, if tile contains it and player does not have 3,
     *                 null otherwise
     */
    public void ammoCardRecharging(@NotNull AmmoCard ammoCard, @Nullable PowerUp powerUp) {
        switch (ammoCard.getType()) {
            case POWER_UP:
                if (powerUp != null) addPowerUp(powerUp);
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

    /**
     * @return a list of power ups owned by this player
     */
    public @NotNull List<PowerUp> getPowerUps() {
        return powerUps;
    }

    /**
     * @return coordinates of this player in the board, null if he does not have played his first turn
     */
    public @Nullable Point getPosition() {
        return position;
    }

    /**
     * moves player in @position
     *
     * @param position coordinates in the board
     */
    public void setPosition(@Nullable Point position) {
        this.position = position;
    }

    /**
     * Says if this player can see a selected player from his own position
     *
     * @param player another player
     * @param cells  the map
     * @return true if this player can see @param player, false otherwise
     */
    @Contract(pure = true)
    public boolean canSeeNotSame(@NotNull Player player, @NotNull Cell[][] cells) {
        if (equals(player) || position == null || player.position == null) return false;
        if (cells[position.x][position.y].getColor() == cells[player.position.x][player.position.y].getColor())
            return true;
        return Stream.of(Bounds.Direction.values()).filter(e -> cells[position.x][position.y].getBounds().getType(e) == Bounds.Type.DOOR)
                .anyMatch(e -> cells[position.x + e.getdX()][position.y + e.getdY()].getColor() ==
                        cells[player.position.x][player.position.y].getColor());
    }

    /**
     * Determines if a point of the map is at a certain distance from this player in a certain direction
     *
     * @param point       the point
     * @param cells       the map
     * @param maxDistance the distance
     * @param direction   the direction
     * @return true if the point is attainable from the position of this player with at most "maxDistance" steps,
     * in "direction" direction, false otherwise
     */
    public boolean isPointAtMaxDistanceInDirection(@Nullable Point point, @NotNull Cell[][] cells, int maxDistance, @NotNull Bounds.Direction direction) {
        if (position == null) return false;
        int countDistance = 0, x = (int) position.getX(), y = (int) position.getY();
        while (countDistance < maxDistance) {
            if (cells[x][y].getBounds().getType(direction).equals(Bounds.Type.WALL)) return false;
            countDistance++;
            x += direction.getdX();
            y += direction.getdY();
            if (x == (int) point.getX() && y == (int) point.getY()) return true;
        }
        return false;
    }

    /**
     * Determines if this player can see a cell from his own position
     *
     * @param point a point on the board
     * @param cells the map
     * @return true if player can see "point", false otherwise
     */
    public boolean canSeeCell(@NotNull Point point, @NotNull Cell[][] cells) {
        if (position == null || cells[point.x][point.y] == null) return false;
        if (cells[position.x][position.y].getColor().equals(cells[point.x][point.y].getColor())) return true;
        for (var direction : Bounds.Direction.values())
            if (cells[position.x][position.y].getBounds().getType(direction).equals(Bounds.Type.DOOR) &&
                    cells[position.x + direction.getdX()][position.y + direction.getdY()].getColor()
                            == cells[point.x][point.y].getColor()) return true;
        return false;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Player && ((Player) obj).uuid.equals(uuid);
    }

    @Override
    public @NotNull BufferedImage getFrontImage() throws IOException {
        return Utils.readPngImage(Player.class, boardType.name().substring(0, 3) + "P");
    }

    @Override
    public @NotNull BufferedImage getBackImage() throws IOException {
        return Utils.readJpgImage(Player.class, boardType.name().substring(0, 3) + (isEasyBoard() ? "F" : ""));
    }

    public enum BoardType {
        BANSHEE(Color.BLUE, "\u001b[38;5;34m"), D_STRUCT(Color.YELLOW, "\033[0;33m"), DOZER(Color.GRAY, "\u001b[38;5;242m"), SPROG(Color.GREEN, "\u001B[32m"), VIOLET(Color.MAGENTA, "\u001B[35m");


        private @NotNull Color color;

        private @NotNull String escape;

        @Contract(pure = true)
        BoardType(@NotNull Color color, @NotNull String escape) {
            this.color = color;
            this.escape = escape;
        }

        /**
         * @return the color of the player board
         */
        @Contract(pure = true)
        public @NotNull Color getColor() {
            return color;
        }

        /**
         * @return string that represents the color of the player board
         */
        @Contract(pure = true)
        public @NotNull String escape() {
            return escape;
        }
    }
}