package it.polimi.ingsw.models.weapons;

import it.polimi.ingsw.models.cards.AmmoCard;
import it.polimi.ingsw.models.cards.Card;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Weapon implements Card {
    private Type type;
    private boolean isCharged = true;
    private ArrayList<AmmoCard.Color> color; //serve perchè un cubo "color" si aggiunge al costo per "pickUp" al fine di caricare l'arma
    private ArrayList<AmmoCard.Color> reloadAmmoConsumption;  //esiste un costo per tirarla su, ma non uno per la mod basic
    private ArrayList<AmmoCard.Color> firstOptionalAmmoConsumption;
    private ArrayList<AmmoCard.Color> secondOptionalAmmoConsumption;
    private ArrayList<AmmoCard.Color> alternateAmmoConsumption;

    public Weapon(Type type, ArrayList<AmmoCard.Color> color, @NotNull ArrayList<AmmoCard.Color> reloadAmmoConsumption, ArrayList<AmmoCard.Color> firstOptionalAmmoConsumption, ArrayList<AmmoCard.Color> secondOptionalAmmoConsumption, ArrayList<AmmoCard.Color> alternateAmmoConsumption) {
        this.type = type;
        this.color = color;
        this.reloadAmmoConsumption = reloadAmmoConsumption;
        this.firstOptionalAmmoConsumption = firstOptionalAmmoConsumption;
        this.secondOptionalAmmoConsumption = secondOptionalAmmoConsumption;
        this.alternateAmmoConsumption = alternateAmmoConsumption;
    }

    public Type getType() {
        return this.type;
    }

    public boolean isCharged(){ return this.isCharged; }

    public ArrayList<AmmoCard.Color> getColor(){ return this.color; }

    public ArrayList<AmmoCard.Color> getReloadAmmoConsumption() {
        return reloadAmmoConsumption;
    }

    public ArrayList<AmmoCard.Color> getFirstOptionalAmmoConsumption() {
        return firstOptionalAmmoConsumption;
    }

    public ArrayList<AmmoCard.Color> getSecondOptionalAmmoConsumption() {
        return secondOptionalAmmoConsumption;
    }

    public ArrayList<AmmoCard.Color> getAlternateAmmoConsumption() {
        return alternateAmmoConsumption;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public enum Type {
        LOCK_RIFLE, MACHINE_GUN, THOR, PLASMA_GUN, WHISPER, ELECTROSCYTHE, TRACTOR_BEAM,
        VORTEX_CANNON, FURNACE, HEATSEEKER, HELLION, FLAMETHROWER, GRENADE_LAUNCHER, ROCKET_LAUNCHER,
        RAILGUN, CYBERBLADE, ZX2, SHOTGUN, POWER_GLOVE, SHOCKWAVE, SLEDGEHAMMER
    }
}