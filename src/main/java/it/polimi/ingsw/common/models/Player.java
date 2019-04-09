package it.polimi.ingsw.common.models;

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

    public void addPowerUp(PowerUp powerUp) {
        assert powerUps.size() < 3;
        powerUps.add(powerUp);
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
}