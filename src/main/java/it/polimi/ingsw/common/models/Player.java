package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Player implements Serializable {
    private static final long serialVersionUID = 1;

    private UUID uuid;
    private String nickname;
    private Point position;
    private ArrayList<String> damagesTaken = new ArrayList<>();
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

    public UUID getUuid() {
        return uuid;
    }

    public String getNickname() {
        return nickname;
    }

    public boolean hasWeapon(@NotNull Weapon weapon) { return weapons.contains(weapon); }

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
            damagesTaken.clear();
            marksTaken.clear();
            deaths++;
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
        for (Bounds.Direction d : Bounds.Direction.values()) {
            if (cells[position.x][position.y].getBounds().getType(d) != Bounds.Type.WALL &&
                    player.getPosition().x == position.x + d.getdX() && player.getPosition().y == position.y + d.getdY())
                return true;
        }
        return false;
    }

    public boolean isPlayerNear2(@NotNull Player player, @NotNull Cell[][] cells) {
        for (Bounds.Direction d : Bounds.Direction.values()) {
            if (cells[position.x][position.y].getBounds().getType(d) != Bounds.Type.WALL) {
                if (cells[position.x + d.getdX()][position.y + d.getdY()].getBounds().getType(d) != Bounds.Type.WALL &&
                        player.getPosition().x == position.x + 2*d.getdX() &&
                        player.getPosition().y == position.y + 2*d.getdY()) return true;
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
                        point.x == position.x + 2*d.getdX() &&
                        point.y == position.y + 2*d.getdY()) return true;
            }
        }
        return false;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Player && ((Player) obj).nickname.equals(nickname);
    }
}