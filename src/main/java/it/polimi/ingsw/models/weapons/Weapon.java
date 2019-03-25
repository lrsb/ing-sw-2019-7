package it.polimi.ingsw.models.weapons;

import it.polimi.ingsw.models.cards.Card;
import org.jetbrains.annotations.NotNull;

public class Weapon implements Card {
    private Type type;
    private ColorRDY color; //serve perchè un cubo "color" si aggiunge al costo per "pickUp" al fine di caricare l'arma
    private AmmoConsumption pickUpAmmoConsumption;  //esiste un costo per tirarla su, ma non uno per la mod basic
    private AmmoConsumption firstOptionalAmmoConsumption;
    private AmmoConsumption secondOptionalAmmoConsumption;
    private AmmoConsumption alternateAmmoConsumption;

    public Weapon(Type type, @NotNull AmmoConsumption pickUpAmmoConsumption, AmmoConsumption firstOptionalAmmoConsumption, AmmoConsumption secondOptionalAmmoConsumption, AmmoConsumption alternateAmmoConsumption) {
        this.type = type;
        this.pickUpAmmoConsumption = pickUpAmmoConsumption;
        this.firstOptionalAmmoConsumption = firstOptionalAmmoConsumption;
        this.secondOptionalAmmoConsumption = secondOptionalAmmoConsumption;
        this.alternateAmmoConsumption = alternateAmmoConsumption;
    }

    public Type getType() {
        return type;
    }

    public AmmoConsumption pickUpAmmoConsumption() {
        return pickUpAmmoConsumption;
    }

    public AmmoConsumption getFirstOptionalAmmoConsumption() {
        return firstOptionalAmmoConsumption;
    }

    public AmmoConsumption getSecondOptionalAmmoConsumption() {
        return secondOptionalAmmoConsumption;
    }

    public AmmoConsumption getAlternateAmmoConsumption() {
        return alternateAmmoConsumption;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public enum Type {
        LOCK_RIFLE, MACHINE_GUN, THOR, PLASMA_GUN, WHISPER, ELECTROSCYTHE, TRACTOR_BEAM,
        VORTEX_CANNON, FURNACE, HEATSEEKER, HELLION, FLAMETHROWER, GRENADE_LAUNCHER, ROCKET_LAUNCHER,
        RAILGUN, CYBERBLADE, ZX2, SHOTGUN, POWER_GLOVE, SHOCKWAVE, SLEDGEHAMMER
    }

    public enum ColorRDY {
        BLUE, RED, YELLOW
    }
}