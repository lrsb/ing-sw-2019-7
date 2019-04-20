package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Player implements Serializable {
    private UUID uuid;
    private String nickname;
    private Point position;
    private ArrayList<String> hitsTaken = new ArrayList<>();
    private ArrayList<String> marksTaken = new ArrayList<>();
    private int deaths = 0;
    private int points = 0;
    private int[] cubes = {3, 3, 3}; //r, y, b
    private ArrayList<PowerUp> powerUps = new ArrayList<>();//3 al massimo e consumabili
    private ArrayList<Weapon> weapons = new ArrayList<>();//3 al massimo e scambiabili
    private boolean[] isReloaded = {true, true, true};
    private ArrayList<AmmoCard> ammoCards = new ArrayList<>();
    private boolean isFirstMove = true;

    public Player(@NotNull User user) {
        this.uuid = user.getUuid();
        this.nickname = user.getNickname();
    }

    public String getNickname() {
        return nickname;
    }

    public void addShooterHits(@NotNull Player shooter, int hits) {
        for (int c = 0; c < hits; c++) {
            if (hitsTaken.size() < 12) hitsTaken.add(shooter.nickname);
        }
    }

    //use this at each other's player end turn
    public boolean amIDead() {
        return hitsTaken.size() >= 11;
    }

    //controllo se l'arma che vuole caricare sia in suo possesso e che non sia già carica
    public boolean reload(Weapon.Name weapon) {
        for (Weapon w : weapons) {
            if (w.getClass().equals(weapon.getWeaponClass()) && !(isReloaded[weapons.indexOf(w)])) {
                if (w.charging()) {
                    isReloaded[weapons.indexOf(w)] = true;
                    return true;
                }
            }
        }
        return false;
    }

    //distributes points to other players
    public void death() {
        if (amIDead()) {
            //è game che si preoccupa di distribuire i punti
            //TODO redistributing points to players
            hitsTaken.clear();
            marksTaken.clear();
            deaths++;
        }
    }

    public void addShooterMarks(@NotNull Player shooter, int marks) {
        for (int c = 0; c < marks; c++) {
            marksTaken.add(shooter.nickname);
        }
    }

    //when a hit is taken from another player converts all shooter's marks in hits
    public void convertShooterMarks(@NotNull Player shooter) {
        for (String s : marksTaken) {
            if (s.equals(shooter.nickname)) {
                marksTaken.remove(s);
                addShooterHits(shooter, 1);
            }
        }
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

    public void addWeapon(Weapon weapon) {
        assert weapons.size() < 3;
        weapons.add(weapon);
    }

    public void addAmmoCard(AmmoCard ammoCard) {
        assert ammoCards.size() < 3;
        ammoCards.add(ammoCard);
    }

    public List<PowerUp> getPowerUps() {
        return powerUps;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    @Contract(pure = true)
    public boolean canSee(@NotNull Player player, @NotNull Cell[][] cells) {
        if (nickname.equals(player.nickname)) return false;
        if (cells[position.x][position.y].getColor() == cells[player.position.x][player.position.y].getColor())
            return true;
        for (var direction : Bounds.Direction.values())
            if (cells[position.x][position.y].getBounds().getType(direction) == Bounds.Type.DOOR &&
                    cells[position.x + direction.getdX()][position.y + direction.getdY()].getColor()
                            == cells[player.position.x][player.position.y].getColor()) return true;
        return false;
    }

    public boolean isPlayerNear(@NotNull Player player, @NotNull Cell[][] cells) {
        return canSee(player, cells) && (position.x == player.getPosition().x
                && position.y - player.position.y < 2
                && position.y - player.position.y > -2)
                || (position.y == player.position.y
                && position.x - player.position.x < 2
                && position.x - player.position.x > -2);
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
        return canSeeCell(point, cells) && (position.x == point.x
                && position.y - point.y < 2
                && position.y - point.y > -2)
                || (position.y == point.y
                && position.x - point.x < 2
                && position.x - point.x > -2);
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Player && ((Player) obj).nickname.equals(nickname);
    }
}