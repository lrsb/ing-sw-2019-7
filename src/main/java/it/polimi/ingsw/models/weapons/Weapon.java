package it.polimi.ingsw.models.weapons;

import it.polimi.ingsw.models.cards.AmmoCard;
import it.polimi.ingsw.models.interfaces.Card;

import java.util.ArrayList;
import java.util.List;

public class Weapon implements Card {
    private Name name;
    private ArrayList<AmmoCard.Color> ammoConsumption = new ArrayList<>();
    private ArrayList<AmmoCard.Color> firstOptionalAmmoConsumption = new ArrayList<>();
    private ArrayList<AmmoCard.Color> secondOptionalAmmoConsumption = new ArrayList<>();
    private ArrayList<AmmoCard.Color> alternateAmmoConsumption = new ArrayList<>();

    public Weapon(Name name, List<AmmoCard.Color> ammoConsumption, List<AmmoCard.Color> firstOptionalAmmoConsumption, List<AmmoCard.Color> secondOptionalAmmoConsumption, List<AmmoCard.Color> alternateAmmoConsumption) {
        this.name = name;
        this.ammoConsumption.addAll(ammoConsumption);
        this.firstOptionalAmmoConsumption.addAll(firstOptionalAmmoConsumption);
        this.secondOptionalAmmoConsumption.addAll(secondOptionalAmmoConsumption);
        this.alternateAmmoConsumption.addAll(alternateAmmoConsumption);
    }

    public Name getName() {
        return name;
    }

    public List<AmmoCard.Color> getAmmoConsumption() {
        return ammoConsumption;
    }

    public List<AmmoCard.Color> getFirstOptionalAmmoConsumption() {
        return firstOptionalAmmoConsumption;
    }

    public List<AmmoCard.Color> getSecondOptionalAmmoConsumption() {
        return secondOptionalAmmoConsumption;
    }

    public List<AmmoCard.Color> getAlternateAmmoConsumption() {
        return alternateAmmoConsumption;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public enum Name {
        LOCK_RIFLE, MACHINE_GUN, THOR, PLASMA_GUN, WHISPER, ELECTROSCYTHE, TRACTOR_BEAM,
        VORTEX_CANNON, FURNACE, HEATSEEKER, HELLION, FLAMETHROWER, GRENADE_LAUNCHER, ROCKET_LAUNCHER,
        RAILGUN, CYBERBLADE, ZX2, SHOTGUN, POWER_GLOVE, SHOCKWAVE, SLEDGEHAMMER
    }
}