package it.polimi.ingsw.models.game;

import it.polimi.ingsw.models.cards.PowerUp;
import it.polimi.ingsw.models.weapons.Weapon;

import java.util.ArrayList;

public class Player {
    private String name;
    private String[12] hitsTaken;//array di giocatori
    private int deaths;
    private int points;
    private int[] cubes = {0, 0, 0}; //r, y, b
    private ArrayList<PowerUp> powerups = new ArrayList<>();//3 al massimo e consumabili
    private ArrayList<Weapon> weapons = new ArrayList<>();//3 al massimo e scambiabili


}