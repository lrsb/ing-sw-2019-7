package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private Point position;
    private ArrayList<Player> hitsTaken = new ArrayList<>();
    private int deaths = 0;
    private int points = 0;
    private int[] cubes = {3, 3, 3}; //r, y, b
    private ArrayList<PowerUp> powerUps = new ArrayList<>();//3 al massimo e consumabili
    private ArrayList<Weapon> weapons = new ArrayList<>();//3 al massimo e scambiabili
    private ArrayList<AmmoCard> ammoCards = new ArrayList<>();
    private boolean isFirstMove = true;

    public Player(String name){
        this.name = name;
    }

    public void setPlayed() {
        isFirstMove = false;
    }

    public boolean isFirstMove() {
        return isFirstMove;
    }

    public int getColoredCubes(int index){
        return cubes[index];
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
        if (cells[position.x][position.y].getColor() == cells[player.position.x][player.position.y].getColor())
            return true;
        for (var direction : Bounds.Direction.values())
            if (cells[position.x][position.y].getBounds().getType(direction) == Bounds.Type.DOOR &&
                    cells[position.x + direction.getX()][position.y + direction.getY()].getColor() == cells[player.position.x][player.position.y].getColor()) return true;
        return false;
    }
}