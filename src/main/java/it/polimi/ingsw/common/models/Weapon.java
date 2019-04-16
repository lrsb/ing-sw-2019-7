package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import static it.polimi.ingsw.common.models.AmmoCard.Color.*;

public abstract class Weapon {
    private @NotNull Cell[][] cells;
    private @NotNull ArrayList<Player> players;
    private @NotNull Player shooter;
    private boolean alternativeFire;
    //siccome si possono usare anche le powerups come ammo, se le metti, queste hanno la precedenza sul pagamento
    private @NotNull ArrayList<PowerUp> alternativePayment;
    private @NotNull ArrayList<AmmoCard.Color> basicPayment;


    private ArrayList<Player> possibleTarget = new ArrayList<>();
    private ArrayList<Point> possibleTargetPoint = new ArrayList<>();
    private @NotNull ArrayList<Player> basicTarget = new ArrayList<>();
    private @NotNull ArrayList<Point> basicTargetPoint = new ArrayList<>();
    private @NotNull ArrayList<Player> firstAdditionalTarget = new ArrayList<>();
    private @NotNull ArrayList<Point> firstAdditionalTargetPoint = new ArrayList<>();
    private @NotNull ArrayList<Player> secondAdditionalTarget = new ArrayList<>();
    private @NotNull ArrayList<Point> secondAdditionalTargetPoint = new ArrayList<>();

    @Contract(pure = true)
    public Weapon(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                  boolean alternativeFire, @NotNull ArrayList<PowerUp> alternativePayment) {
        this.cells = cells;
        this.players = players;
        this.shooter = shooter;
        this.alternativeFire = alternativeFire;
        this.alternativePayment = alternativePayment;
    }

    public boolean charging() {
        //red, blue e yellow avranno il valore del costo totale
        //altNomeColore hanno il valore dei cubi che vengono invece pagati tramite PowerUp
        int red = 0, yellow = 0, blue = 0, altRed = 0, altYellow = 0, altBlue = 0;
        for (AmmoCard.Color c : basicPayment) {
            switch (c) {
                case RED:
                    red++;
                    break;
                case YELLOW:
                    yellow++;
                    break;
                case BLUE:
                    blue++;
                    break;
            }
        }
        for (PowerUp p : alternativePayment) {
            switch (p.getAmmoColor()) {
                case RED:
                    if (red > altRed) altRed++;
                    break;
                case YELLOW:
                    if (yellow > altYellow) altYellow++;
                    break;
                case BLUE:
                    if (blue > altBlue) altBlue++;
                    break;
            }
        }
        if (shooter.getColoredCubes(RED) >= red - altRed
                && shooter.getColoredCubes(YELLOW) >= yellow - altYellow
                && shooter.getColoredCubes(BLUE) >= blue - altBlue) {
            for (PowerUp p : alternativePayment) {
                switch (p.getAmmoColor()) {
                    case RED:
                        if (altRed > 0) {
                            shooter.removePowerUp(p);
                            altRed--;
                            red--;
                        }
                        break;
                    case YELLOW:
                        if (altYellow > 0) {
                            shooter.removePowerUp(p);
                            altYellow--;
                            yellow--;
                        }
                        break;
                    case BLUE:
                        if (altBlue > 0) {
                            shooter.removePowerUp(p);
                            altBlue--;
                            blue--;
                        }
                        break;
                }
            }
            shooter.removeColoredCubes(RED, red);
            shooter.removeColoredCubes(YELLOW, yellow);
            shooter.removeColoredCubes(BLUE, blue);
            return true;
        }
        return false;
    }

    public void addVisibleTarget() {
        possibleTarget.clear();
        for (Player p : players) {
            if (shooter.canSee(p, cells)) possibleTarget.add(p);
        }
    }

    public void addNonVisibleTarget() {
        possibleTarget.clear();
        for (Player p : players) {
            if (!(shooter.canSee(p, cells)) && !(shooter.getName().equals(p.getName()))) possibleTarget.add(p);
        }
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
        LOCK_RIFLE(Weapons.LockRifle.class), MACHINE_GUN(Weapons.MachineGun.class), THOR(Weapons.Thor.class), PLASMA_GUN(Weapons.PlasmaGun.class),
        WHISPER(Weapons.Whisper.class), ELECTROSCYTHE(Weapons.Electroscythe.class), TRACTOR_BEAM(Weapons.TractorBeam.class),
        VORTEX_CANNON(Weapons.VortexCannon.class)/*, FURNACE(Furnace.class), HEATSEEKER(Heatseeker.class),
        HELLION(Hellion.class), FLAMETHROWER(Flamethrower.class), GRENADE_LAUNCHER(GrenadeLauncher.class),
        ROCKET_LAUNCHER(RocketLauncher.class), RAILGUN(Railgun.class), CYBERBLADE(Cyberblade.class), ZX2(ZX2.class),
        SHOTGUN(Shotgun.class), POWER_GLOVE(PowerGlove.class), SHOCKWAVE(Shockwave.class),
        SLEDGEHAMMER(Sledgehammer.class)*/;

        private final Class<? extends Weapon> weaponClass;

        @Contract(pure = true)
        Name(Class<? extends Weapon> weaponClass) {
            this.weaponClass = weaponClass;
        }

        @Contract(pure = true)
        public Class<? extends Weapon> getWeaponClass() {
            return weaponClass;
        }

        //FIXME: aggiornare firma
        public @Nullable <T extends Weapon> T build(@NotNull Game game, boolean alternativeFire) {
            try {
                //noinspection unchecked
                return (T) getWeaponClass().getDeclaredConstructors()[0].newInstance(game.getCells(), game.getPlayers(), game.getActualPlayer(), alternativeFire);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private class Weapons {
        private class LockRifle extends Weapon {
            private LockRifle(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter, boolean alternativeFire, @NotNull ArrayList<PowerUp> alternativePayment) {
                super(cells, players, shooter, alternativeFire, alternativePayment);
                basicPayment.add(BLUE);
                basicPayment.add(BLUE);
            }

            @Override
            public boolean basicFire() {
                possibleTarget.clear();
                addVisibleTarget();
                if (possibleTarget.size() > 0) {
                    basicTarget.clear();
                    //TODO: insert ONLY 1 Target from possibleTarget to basicTarget
                    if (basicTarget.size() != 1) return false;
                    basicTarget.get(0).addShooterHits(shooter, 2);
                    basicTarget.get(0).convertShooterMarks(shooter);
                    basicTarget.get(0).addShooterMarks(shooter, 1);
                    return true;
                }
                return false;
            }

            @Override
            public boolean firstAdditionalFire() {
                int red = 1;
                for (Player p : possibleTarget) {
                    if (p.getName().equals(basicTarget.get(0).getName())) possibleTarget.remove(p);
                }
                if (possibleTarget.size() > 0) {
                    if (alternativePayment.get(0).getAmmoColor() == RED) red = 0;
                    if (shooter.getColoredCubes(RED) >= red) {
                        firstAdditionalTarget.clear();
                        //TODO: insert ONLY 1 Target from possibleTarget to firstAdditionalTarget
                        if (firstAdditionalTarget.size() != 1) return false;
                        if (red == 0) {
                            shooter.removePowerUp(alternativePayment.get(0));
                        } else {
                            shooter.removeColoredCubes(RED, red);
                        }
                        firstAdditionalTarget.get(0).addShooterMarks(shooter, 1);
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean secondAdditionalFire() {
                return false;
            }
        }

        private class MachineGun extends Weapon {
            private MachineGun(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter, boolean alternativeFire, @NotNull ArrayList<PowerUp> alternativePayment) {
                super(cells, players, shooter, alternativeFire, alternativePayment);
                basicPayment.add(BLUE);
                basicPayment.add(RED);
            }

            @Override
            public boolean basicFire() {
                possibleTarget.clear();
                addVisibleTarget();
                if (possibleTarget.size() > 0) {
                    basicTarget.clear();
                    //TODO: insert 1 or 2 Target from possibleTarget to basicTarget
                    if (basicTarget.size() < 1 || basicTarget.size() > 2) return false;
                    for (Player p : basicTarget) {
                        p.addShooterHits(shooter, 1);
                        p.convertShooterMarks(shooter);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean firstAdditionalFire() {
                int yellow = 1;
                if (alternativePayment.get(0).getAmmoColor() == YELLOW) {
                    yellow = 0;
                }
                if (shooter.getColoredCubes(YELLOW) >= yellow) {
                    firstAdditionalTarget.clear();
                    //TODO: choose 1 target from basicTarget to firstAdditionalTarget
                    if (firstAdditionalTarget.size() != 1) return false;
                    if (yellow == 0) shooter.removePowerUp(alternativePayment.get(0));
                    shooter.removeColoredCubes(YELLOW, yellow);
                    firstAdditionalTarget.get(0).addShooterHits(shooter, 1);
                    return true;
                }
                return false;
            }

            //qui controllo che sia colpibile anche un secondo bersaglio
            @Override
            public boolean secondAdditionalFire() {
                int blue = 1;
                if (alternativePayment.get(0).getAmmoColor() == BLUE || alternativePayment.get(1).getAmmoColor() == BLUE) {
                    blue = 0;
                }
                if (shooter.getColoredCubes(BLUE) >= blue) {
                    secondAdditionalTarget.clear();
                    //TODO choose 1 or 2 targets adding to secondAdditionalTarget
                    if (secondAdditionalTarget.size() < 1 || secondAdditionalTarget.size() > 2) return false;
                    if (blue == 0) {
                        if (alternativePayment.get(0).getAmmoColor() == BLUE) {
                            shooter.removePowerUp(alternativePayment.get(0));
                        } else {
                            shooter.removePowerUp(alternativePayment.get(1));
                        }
                    }
                    shooter.removeColoredCubes(BLUE, blue);
                    for (Player p : secondAdditionalTarget) {
                        if (basicTarget.contains(p)) {
                            p.addShooterHits(shooter, 1);
                        } else {
                            p.addShooterHits(shooter, 1);
                            p.convertShooterMarks(shooter);
                        }
                    }
                    return true;
                }
                return false;
            }
        }

        private class Thor extends Weapon {
            private Thor(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter, boolean alternativeFire, @NotNull ArrayList<PowerUp> alternativePayment) {
                super(cells, players, shooter, alternativeFire, alternativePayment);
                basicPayment.add(BLUE);
                basicPayment.add(RED);
            }

            public void addVisibleTarget(Player player) {
                for (Player p : players) {
                    if (player.canSee(p, cells) && !(p.getName().equals(shooter.getName()))) possibleTarget.add(p);
                }
            }

            @Override
            public boolean basicFire() {
                possibleTarget.clear();
                addVisibleTarget();
                if (possibleTarget.size() > 0) {
                    basicTarget.clear();
                    //TODO: choose ONLY 1 target from possibleTarget to basicTarget
                    if (basicTarget.size() != 1) return false;
                    basicTarget.get(0).addShooterHits(shooter, 2);
                    basicTarget.get(0).convertShooterMarks(shooter);
                    return true;
                }
                return false;
            }

            @Override
            public boolean firstAdditionalFire() {
                int blue = 1;
                possibleTarget.clear();
                addVisibleTarget(basicTarget.get(0));
                if (alternativePayment.get(0).getAmmoColor() == BLUE) blue = 0;
                if (possibleTarget.size() > 0 && shooter.getColoredCubes(BLUE) >= blue) {
                    firstAdditionalTarget.clear();
                    //TODO: choose ONLY 1 target from possibleTarget to firstAdditionalTarget
                    if (firstAdditionalTarget.size() != 1) return false;
                    if (blue == 0) shooter.removePowerUp(alternativePayment.get(0));
                    shooter.removeColoredCubes(BLUE, blue);
                    firstAdditionalTarget.get(0).addShooterHits(shooter, 1);
                    firstAdditionalTarget.get(0).convertShooterMarks(shooter);
                    return true;
                }
                return false;
            }

            @Override
            public boolean secondAdditionalFire() {
                int blue = 1;
                possibleTarget.clear();
                addVisibleTarget(firstAdditionalTarget.get(0));
                for (Player p : possibleTarget) {
                    if (p.getName().equals(shooter.getName())
                            || p.getName().equals(firstAdditionalTarget.get(0).getName())) possibleTarget.remove(p);
                }
                if (alternativePayment.get(1).getAmmoColor() == BLUE) blue = 0;
                if (possibleTarget.size() > 0 && shooter.getColoredCubes(BLUE) >= blue) {
                    secondAdditionalTarget.clear();
                    //TODO: choose ONLY 1 target from possibleTarget to secondAdditionalTarget
                    if (secondAdditionalTarget.size() != 1) return false;
                    if (blue == 0) shooter.removePowerUp(alternativePayment.get(1));
                    shooter.removeColoredCubes(BLUE, blue);
                    secondAdditionalTarget.get(0).addShooterHits(shooter, 2);
                    secondAdditionalTarget.get(0).convertShooterMarks(shooter);
                    return true;
                }
                return false;
            }
        }

        private class PlasmaGun extends Weapon {
            private PlasmaGun(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter, boolean alternativeFire, @NotNull ArrayList<PowerUp> alternativePayment) {
                super(cells, players, shooter, alternativeFire, alternativePayment);
                basicPayment.add(BLUE);
                basicPayment.add(YELLOW);
            }

            private boolean justMoved = false;

            @Override
            public boolean basicFire() {
                //TODO: shooter chooses Target and decides if to move him
                //TODO: control if target can be moved to decided point and if shooter can see this point
                if (basicTarget.size() != 1) return false;
                if (justMoved && shooter.canSeeCell(basicTargetPoint.get(0), cells)) {
                    //TODO: effectively move target
                    basicTarget.get(0).addShooterHits(shooter, 2);
                    basicTarget.get(0).convertShooterMarks(shooter);
                    return true;
                } else if (!justMoved && shooter.canSee(basicTarget.get(0), cells)) {
                    basicTarget.get(0).addShooterHits(shooter, 2);
                    basicTarget.get(0).convertShooterMarks(shooter);
                    //TODO: ask if want to move target
                    return true;
                }
                return false;
            }

            @Override
            public boolean firstAdditionalFire() {
                //TODO: use this method to move?
                return false;
            }

            @Override
            public boolean secondAdditionalFire() {
                int blue = 1;
                if (alternativePayment.get(0).getAmmoColor() == BLUE) blue = 0;
                if (shooter.getColoredCubes(BLUE) >= blue) {
                    if (blue == 0) shooter.removePowerUp(alternativePayment.get(0));
                    shooter.removeColoredCubes(BLUE, blue);
                    basicTarget.get(0).addShooterHits(shooter, 1);
                    return true;
                }
                return false;
            }
        }

        public class Whisper extends Weapon {
            public Whisper(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                           boolean alternativeFire, @NotNull ArrayList<PowerUp> alternativePayment) {
                super(cells, players, shooter, alternativeFire, alternativePayment);
                basicPayment.add(BLUE);
                basicPayment.add(BLUE);
                basicPayment.add(YELLOW);
            }

            @Override
            public boolean basicFire() {
                possibleTarget.clear();
                addVisibleTarget();
                for (Player p : possibleTarget) {
                    if ((shooter.getPosition().x == p.getPosition().x
                            && shooter.getPosition().y - p.getPosition().y < 2
                            && shooter.getPosition().y - p.getPosition().y > -2)
                            || (shooter.getPosition().y == p.getPosition().y
                            && shooter.getPosition().x - p.getPosition().x < 2
                            && shooter.getPosition().x - p.getPosition().x > -2)) possibleTarget.remove(p);
                }
                if (possibleTarget.size() > 0) {
                    //TODO: choose 1 target from possibleTarget to BasicTarget
                    basicTarget.get(0).addShooterHits(shooter, 3);
                    basicTarget.get(0).convertShooterMarks(shooter);
                    basicTarget.get(0).addShooterMarks(shooter, 1);
                    return true;
                }
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

        public class Electroscythe extends Weapon {
            public Electroscythe(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                                 boolean alternativeFire, @NotNull ArrayList<PowerUp> alternativePayment) {
                super(cells, players, shooter, alternativeFire, alternativePayment);
                basicPayment.add(BLUE);
            }

            @Override
            public boolean basicFire() {
                basicTarget.clear();
                for (Player p : players) {
                    if (shooter.getPosition().equals(p.getPosition()) && !(shooter.getName().equals(p.getName()))) {
                        basicTarget.add(p);
                    }
                }
                if (basicTarget.size() > 0) {
                    if (alternativeFire) {
                        int blue = 1, red = 1;
                        if (alternativePayment.get(0).getAmmoColor() == BLUE
                                || alternativePayment.get(1).getAmmoColor() == BLUE) blue = 0;
                        if (alternativePayment.get(0).getAmmoColor() == RED
                                || alternativePayment.get(1).getAmmoColor() == RED) red = 0;
                        if (shooter.getColoredCubes(BLUE) >= blue && shooter.getColoredCubes(RED) >= red) {
                            if (blue == 0) {
                                if (alternativePayment.get(0).getAmmoColor() == BLUE) {
                                    shooter.removePowerUp(alternativePayment.get(0));
                                } else {
                                    shooter.removePowerUp(alternativePayment.get(1));
                                }
                            }
                            if (red == 0) {
                                if (alternativePayment.get(0).getAmmoColor() == RED) {
                                    shooter.removePowerUp(alternativePayment.get(0));
                                } else {
                                    shooter.removePowerUp(alternativePayment.get(1));
                                }
                            }
                            shooter.removeColoredCubes(BLUE, blue);
                            shooter.removeColoredCubes(RED, red);
                            for (Player p : basicTarget) {
                                p.addShooterHits(shooter, 2);
                                p.convertShooterMarks(shooter);
                            }
                            return true;
                        }
                    } else {
                        for (Player p : basicTarget) {
                            p.addShooterHits(shooter, 1);
                            p.convertShooterMarks(shooter);
                        }
                        return true;
                    }
                }
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

        public class TractorBeam extends Weapon {
            public TractorBeam(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                               boolean alternativeFire, @NotNull ArrayList<PowerUp> alternativePayment) {
                super(cells, players, shooter, alternativeFire, alternativePayment);
                basicPayment.add(RED);
                basicPayment.add(BLUE);
            }

            @Override
            public boolean basicFire() {
                int x, y;
                Point point;
                possibleTargetPoint.clear();
                for (x = 0; x < 4; x++) {
                    for (y = 0; y < 3; y++) {
                        point = new Point(x, y);
                        if (shooter.canSeeCell(point, cells)
                                && !(shooter.getPosition().equals(point))) possibleTargetPoint.add(point);
                    }
                }
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

        public class VortexCannon extends Weapon {
            public VortexCannon(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                                boolean alternativeFire, @NotNull ArrayList<PowerUp> alternativePayment) {
                super(cells, players, shooter, alternativeFire, alternativePayment);
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
}