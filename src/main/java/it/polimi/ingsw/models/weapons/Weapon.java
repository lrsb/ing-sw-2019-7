package it.polimi.ingsw.models.weapons;

import it.polimi.ingsw.models.cards.Card;
import org.jetbrains.annotations.NotNull;

public class Weapon implements Card {
    private Type type;
    private AmmoConsumption basicAmmoConsumption;
    private AmmoConsumption firstOptionalAmmoConsumption;
    private AmmoConsumption secondOptionalAmmoConsumption;
    private AmmoConsumption alternateAmmoConsumption;

    public Weapon(Type type, @NotNull AmmoConsumption basicAmmoConsumption, AmmoConsumption firstOptionalAmmoConsumption, AmmoConsumption secondOptionalAmmoConsumption, AmmoConsumption alternateAmmoConsumption) {
        this.type = type;
        this.basicAmmoConsumption = basicAmmoConsumption;
        this.firstOptionalAmmoConsumption = firstOptionalAmmoConsumption;
        this.secondOptionalAmmoConsumption = secondOptionalAmmoConsumption;
        this.alternateAmmoConsumption = alternateAmmoConsumption;
    }

    public Type getType() {
        return type;
    }

    public AmmoConsumption getBasicAmmoConsumption() {
        return basicAmmoConsumption;
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
}
