package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
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

    public Player(String name){
        this.name = name;
    }

    public String getName(){ return name; }

    public void addShooterHits(@NotNull Player shooter, int hits){
        for(int c=0; c<hits; c++){
            if(hitsTaken.size()<12) this.hitsTaken.add(shooter.name);
        }
    }

    //use this at each other's player end turn
    public boolean amIDead() {
        return hitsTaken.size()>=11;
    }

    //controllo se l'arma che vuole caricare sia in suo possesso e che non sia già carica
    public boolean reload(Weapon.Name weapon){
        for (Weapon w : weapons) {
            if(w.getClass().equals(weapon.getWeaponClass()) && !(isReloaded[weapons.indexOf(w)])){
                if(w.charging()){
                    isReloaded[weapons.indexOf(w)] = true;
                    return true;
                }
            }
        }
        return false;
    }

    //distributes points to other players
    public void death(){
        if(amIDead()){
            //TODO redistributing points to players
            hitsTaken.clear();
            marksTaken.clear();
            deaths++;
        }
    }

    public void addShooterMarks(@NotNull Player shooter, int marks) {
        for(int c=0; c<marks; c++) {
            this.marksTaken.add(shooter.name);
        }
    }

    //when a hit is taken from another player converts all shooter's marks in hits
    public void convertShooterMarks(@NotNull Player shooter) {
        for (String s : this.marksTaken) {
            if(s.equals(shooter.name)){
                this.marksTaken.remove(s);
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

    public int getColoredCubes(@NotNull AmmoCard.Color color){
        return cubes[color.getColorNumber()];
    }

    //removes ammos when player has to pay a cost
    //!!!A PLAYER CAN PAY EVEN WITH POWERUPS!!!
    public void removeColoredCubes(@NotNull AmmoCard.Color color, int number){
        cubes[color.getColorNumber()] -= number;
    }

    public void removePowerUp(@NotNull PowerUp powerUp){
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
        if(this.name.equals(player.name)) return false;
        if (cells[position.x][position.y].getColor() == cells[player.position.x][player.position.y].getColor())
            return true;
        for (var direction : Bounds.Direction.values())
            if (cells[position.x][position.y].getBounds().getType(direction) == Bounds.Type.DOOR &&
                    cells[position.x + direction.getX()][position.y + direction.getY()].getColor()
                            == cells[player.position.x][player.position.y].getColor()) return true;
        return false;
    }

    public boolean isNear(@NotNull Player player){
        if ((this.position.x == player.getPosition().x
                && this.position.y - player.position.y < 2
                && this.position.y - player.position.y > -2)
                || (this.position.y == player.position.y
                && this.position.x - player.position.x < 2
                && this.position.x - player.position.x > -2)) return true;
        return false;
    }

    public boolean canSeeCell(@NotNull Point point, @NotNull Cell[][] cells) {
        if (cells[position.x][position.y].getColor() == cells[point.x][point.y].getColor()) return true;
        for (var direction : Bounds.Direction.values())
            if (cells[position.x][position.y].getBounds().getType(direction) == Bounds.Type.DOOR &&
                    cells[position.x + direction.getX()][position.y + direction.getY()].getColor()
                            == cells[point.x][point.y].getColor()) return true;
        return false;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj){
        return obj != null && this.name.equals(obj.getClass().getName());
    }
}