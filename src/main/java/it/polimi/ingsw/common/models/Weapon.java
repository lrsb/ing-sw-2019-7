package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public abstract class Weapon {
    private @NotNull Cell[][] cells;
    private @NotNull ArrayList<Player> players;
    private @NotNull Player shooter;
    private boolean alternativeFire;

    private ArrayList<Player> basicTarget = new ArrayList<>();
    private ArrayList<Point> basicTargetPoint = new ArrayList<>();
    private ArrayList<Player> firstAdditionalTarget = new ArrayList<>();
    private ArrayList<Point> firstAdditionalTargetPoint = new ArrayList<>();
    private ArrayList<Player> secondAdditionalTarget = new ArrayList<>();
    private ArrayList<Point> secondAdditionalTargetPoint = new ArrayList<>();

    @Contract(pure = true)
    public Weapon(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter, boolean alternativeFire) {
        this.cells = cells;
        this.players = players;
        this.shooter = shooter;
        this.alternativeFire = alternativeFire;
    }

    public void addBasicTarget(@Nullable Player target, @Nullable Point point) {
        basicTarget.add(target);
        basicTargetPoint.add(point);
    }

    public abstract boolean basicFire();

    public void addFirstAdditionalTarget(@Nullable Player target, @Nullable Point point) {
        firstAdditionalTarget.add(target);
        firstAdditionalTargetPoint.add(point);
    }

    public abstract boolean firstAdditionalFire();

    public void addSecondAdditionalTarget(@Nullable Player target, @Nullable Point point) {
        secondAdditionalTarget.add(target);
        secondAdditionalTargetPoint.add(point);
    }

    public abstract boolean secondAdditionalFire();

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj) {
        return obj != null && getClass().equals(obj.getClass());
    }

    protected boolean canFire() {
        return false;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public enum Name {
        LOCK_RIFLE(LockRifle.class), MACHINE_GUN(MachineGun.class), THOR(THOR.class), PLASMA_GUN(PlasmaGun.class)/*, WHISPER, ELECTROSCYTHE, TRACTOR_BEAM,
        VORTEX_CANNON, FURNACE, HEATSEEKER, HELLION, FLAMETHROWER, GRENADE_LAUNCHER, ROCKET_LAUNCHER,
        RAILGUN, CYBERBLADE, ZX2, SHOTGUN, POWER_GLOVE, SHOCKWAVE, SLEDGEHAMMER*/;

        private final Class<? extends Weapon> weaponClass;

        @Contract(pure = true)
        Name(Class<? extends Weapon> weaponClass) {
            this.weaponClass = weaponClass;
        }

        @Contract(pure = true)
        public Class<? extends Weapon> getWeaponClass() {
            return weaponClass;
        }
    }

    public class LockRifle extends Weapon {
        public LockRifle(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter, boolean alternativeFire) {
            super(cells, players, shooter, alternativeFire);
        }

        @Override
        public boolean basicFire() {
            return shooter.canSee(players.get(0), cells);
        }

        @Override
        public boolean firstAdditionalFire() {
            return (basicFire() &&
                    shooter.canSee(players.get(1), cells) &&
                    shooter.getColoredCubes(0)>=1);
        }

        @Override
        public boolean secondAdditionalFire() {
            return false;
        }
    }

    public class MachineGun extends Weapon {
        public MachineGun(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter, boolean alternativeFire) {
            super(cells, players, shooter, alternativeFire);
        }

        //per vedere se funziona basta che un giocatore sia visibile, ma potrebbe anche colpirne un secondo che vede
        @Override
        public boolean basicFire() {
            return (shooter.canSee(players.get(0), cells));
        }

        @Override
        public boolean firstAdditionalFire() {
            return (basicFire() && shooter.getColoredCubes(1)>=1);
        }

        //qui controllo che sia colpibile anche un secondo bersaglio
        @Override
        public boolean secondAdditionalFire() {
            return firstAdditionalFire() &&
                    shooter.canSee(players.get(1), cells) &&
                    shooter.canSee(players.get(2), cells) &&
                    shooter.getColoredCubes(2)>=1;
        }
    }

    public class THOR extends Weapon {
        public THOR(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter, boolean alternativeFire){
            super(cells, players, shooter, alternativeFire);
        }

        @Override
        public boolean basicFire() {
            return shooter.canSee(players.get(0), cells);
        }

        @Override
        public boolean firstAdditionalFire() { return players.get(0).canSee(players.get(1), cells); }

        @Override
        public boolean secondAdditionalFire() { return players.get(1).canSee(players.get(2), cells); }
    }

    public class PlasmaGun extends Weapon {
        public PlasmaGun(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter, boolean alternativeFire){
            super(cells, players, shooter, alternativeFire);
        }

        @Override
        public boolean basicFire() {
            return false;
        }

        @Override
        public boolean firstAdditionalFire() {
            return false;
        }

        @Override
        public boolean secondAdditionalFire() {
            return false;
        }
    }
}

class dd {
    public void ff() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        var dd = (Weapon) Weapon.Name.LOCK_RIFLE.getWeaponClass().getConstructors()[0].newInstance(null, null, null, false);
        dd.basicFire();
    }
}