package it.polimi.ingsw.models.game;

import it.polimi.ingsw.models.cards.PowerUp;
import it.polimi.ingsw.models.weapons.Weapon;

import java.util.ArrayList;

public class Player {
    private String name;
    private ArrayList<Player> hitsTaken;//array di giocatori
    private int deaths=0;
    private int points=0;
    private int[] cubes = {3, 3, 3}; //r, y, b
    private ArrayList<PowerUp> powerups = new ArrayList<>();//3 al massimo e consumabili
    private ArrayList<Weapon> weapons = new ArrayList<>();//3 al massimo e scambiabili

    public Player(String name){
        this.name = name;
    }

}