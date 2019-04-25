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
    private Game game;
    //siccome si possono usare anche le powerups come ammo, se le metti, queste hanno la precedenza sul pagamento
    private ArrayList<PowerUp> powerUpsPay;
    private ArrayList<AmmoCard.Color> basicPayment;
    private ArrayList<AmmoCard.Color> firstAdditionalPayment;
    private ArrayList<AmmoCard.Color> secondAdditionalPayment;
    private ArrayList<AmmoCard.Color> alternativePayment;


    private @NotNull ArrayList<Player> possibleTarget = new ArrayList<>();
    private @NotNull ArrayList<Point> possibleTargetPoint = new ArrayList<>();
    private @NotNull ArrayList<Player> basicTarget = new ArrayList<>();
    private @NotNull ArrayList<Point> basicTargetPoint = new ArrayList<>();
    private @NotNull ArrayList<Player> firstAdditionalTarget = new ArrayList<>();
    private @NotNull ArrayList<Point> firstAdditionalTargetPoint = new ArrayList<>();
    private @NotNull ArrayList<Player> secondAdditionalTarget = new ArrayList<>();
    private @NotNull ArrayList<Point> secondAdditionalTargetPoint = new ArrayList<>();
    private @NotNull ArrayList<Integer> fireSort = new ArrayList<>();

    //bisogna passare anche tutti gli Array sopra, alcuni come null a seconda dell'arma
    @Contract(pure = true)
    public Weapon(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                  boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
        this.cells = cells;
        this.players = players;
        this.shooter = shooter;
        this.alternativeFire = alternativeFire;
        this.powerUpsPay = powerUpsPay;
    }

    //ritorna il numero di occorrenze di "color" in "payment"
    //es: ricaricare un'arma costa 2 blu e 1 rosso: getColoredPayment(basicPayment, BLUE) -> 2
    @Contract(pure = true)
    private int getColoredPayment(ArrayList<AmmoCard.Color> payment, @NotNull AmmoCard.Color color) {
        int result = 0;
        for (AmmoCard.Color cube : payment) {
            if (cube == color) result++;
        }
        return result;
    }

    //ritorna il numero di carte di quel "color" presenti in powerUpsPay
    @Contract(pure = true)
    private int getPowerUpsColoredPayment(AmmoCard.Color color) {
        int result = 0;
        for (PowerUp powerUp : powerUpsPay) {
            if (powerUp.getAmmoColor() == color) result++;
        }
        return result;
    }

    //return true if shooter can pay and remove cost from shooter (work for fireCost and for reloading)
    protected boolean payCost() {
        //red, blue e yellow avranno il valore del costo totale
        //altNomeColore hanno il valore dei cubi che vengono invece pagati tramite PowerUp
        int red = 0, yellow = 0, blue = 0, altRed, altYellow, altBlue;
        if (!fireSort.isEmpty()) { //se c'è fireSort sta facendo fuoco "else" vuole ricaricare prendere l'arma
            for (Integer i : fireSort) {
                switch (i) {
                    case 1:
                        if (alternativeFire) {
                            red += getColoredPayment(alternativePayment, RED);
                            yellow += getColoredPayment(alternativePayment, YELLOW);
                            blue += getColoredPayment(alternativePayment, BLUE);
                        }
                        break;
                    case 2:
                        red += getColoredPayment(firstAdditionalPayment, RED);
                        yellow += getColoredPayment(firstAdditionalPayment, YELLOW);
                        blue += getColoredPayment(firstAdditionalPayment, BLUE);
                        break;
                    case 3:
                        red += getColoredPayment(secondAdditionalPayment, RED);
                        yellow += getColoredPayment(secondAdditionalPayment, YELLOW);
                        blue += getColoredPayment(secondAdditionalPayment, BLUE);
                        break;
                    default:
                        return false;
                }
            }
        } else {
            red = getColoredPayment(basicPayment, RED);
            yellow = getColoredPayment(basicPayment, YELLOW);
            blue = getColoredPayment(basicPayment, BLUE);
            if (!shooter.hasWeapon(this)) {
                switch (basicPayment.get(0)) {
                    case RED:
                        red--;
                        break;
                    case YELLOW:
                        yellow--;
                        break;
                    case BLUE:
                        blue--;
                        break;
                }
            }
        }
        altRed = getPowerUpsColoredPayment(RED);
        altYellow = getPowerUpsColoredPayment(YELLOW);
        altBlue = getPowerUpsColoredPayment(BLUE);
        if (shooter.getColoredCubes(RED) >= red - altRed && red - altRed >= 0
                && shooter.getColoredCubes(YELLOW) >= yellow - altYellow && yellow - altYellow >= 0
                && shooter.getColoredCubes(BLUE) >= blue - altBlue && blue - altBlue >= 0) {
            for (PowerUp p : powerUpsPay) {
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

    public boolean charging() {
        fireSort.clear();
        return payCost();
    }

    protected abstract boolean validateTargets();

    protected abstract boolean validateFireSort();

    protected void addVisibleTarget() {
        possibleTarget.clear();
        for (Player p : players) {
            if (shooter.canSee(p, cells)) possibleTarget.add(p);
        }
    }

    public void addNonVisibleTarget() {
        possibleTarget.clear();
        for (Player player : players) {
            if (!(shooter.canSee(player, cells)) && !(shooter.equals(player))) possibleTarget.add(player);
        }
    }

    protected void addVisibleSquare() {
        int x, y;
        Point point;
        possibleTargetPoint.clear();
        for (x = 0; x < 4; x++) {
            for (y = 0; y < 3; y++) {
                point = new Point(x, y);
                if (shooter.canSeeCell(point, cells)) possibleTargetPoint.add(point);
            }
        }
    }

    public void addBasicTarget(@Nullable Player target, @Nullable Point point) {
        basicTarget.add(target);
        basicTargetPoint.add(point);
    }

    protected abstract void basicFire();

    public void addFirstAdditionalTarget(@Nullable Player target, @Nullable Point point) {
        firstAdditionalTarget.add(target);
        firstAdditionalTargetPoint.add(point);
    }

    protected abstract void firstAdditionalFire();

    public void addSecondAdditionalTarget(@Nullable Player target, @Nullable Point point) {
        secondAdditionalTarget.add(target);
        secondAdditionalTargetPoint.add(point);
    }

    protected abstract void secondAdditionalFire();

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Weapon && ((Weapon) obj).getClass().equals(getClass());
    }

    protected abstract boolean canFire();

    //shoot è di Weapon astratta, ma tutti i metodi chiamati all'interno devono essere specifici dell'arma usata
    public boolean shoot() {
        if (canFire() && validateFireSort()) {
            if (validateTargets()) {
                if (payCost()) {
                    for (Integer fireNumber : fireSort) {
                        switch (fireNumber) {
                            case 1:
                                basicFire();
                                break;
                            case 2:
                                firstAdditionalFire();
                                break;
                            case 3:
                                secondAdditionalFire();
                                break;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public enum Name {
        LOCK_RIFLE(Weapons.LockRifle.class), MACHINE_GUN(Weapons.MachineGun.class), THOR(Weapons.Thor.class),
        PLASMA_GUN(Weapons.PlasmaGun.class), WHISPER(Weapons.Whisper.class), ELECTROSCYTHE(Weapons.Electroscythe.class),
        TRACTOR_BEAM(Weapons.TractorBeam.class), VORTEX_CANNON(Weapons.VortexCannon.class),
        FURNACE(Weapons.Furnace.class), HEATSEEKER(Weapons.Heatseeker.class), HELLION(Weapons.Hellion.class),
        FLAMETHROWER(Weapons.Flamethrower.class), GRENADE_LAUNCHER(Weapons.GrenadeLauncher.class),
        ROCKET_LAUNCHER(Weapons.RocketLauncher.class), RAILGUN(Weapons.Railgun.class),
        CYBERBLADE(Weapons.Cyberblade.class), ZX2(Weapons.ZX2.class), SHOTGUN(Weapons.Shotgun.class),
        POWER_GLOVE(Weapons.PowerGlove.class), SHOCKWAVE(Weapons.Shockwave.class), SLEDGEHAMMER(Weapons.Sledgehammer.class);

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
            private LockRifle(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(BLUE);
                basicPayment.add(BLUE);
                firstAdditionalPayment.add(RED);
            }

            //per ora dice se può essere usata, al di fuori del "è carica?"
            @Override
            protected boolean canFire() {
                possibleTarget.clear();
                addVisibleTarget();
                return !possibleTarget.isEmpty();
            }

            @Override
            protected boolean validateFireSort() {
                return (fireSort.get(0) == 1 && (fireSort.size() == 1
                        || (fireSort.size() == 2 && fireSort.get(1) == 2)));
            }

            @Override
            protected boolean validateTargets() {
                for (Integer mode : fireSort) {
                    switch (mode) {
                        case (1):
                            if (!shooter.canSee(basicTarget.get(0), cells)) return false;
                            break;
                        case (2):
                            if (!shooter.canSee(firstAdditionalTarget.get(0), cells) || firstAdditionalTarget.get(0).equals(basicTarget.get(0)))
                                return false;
                            break;
                        default:
                            return false;
                    }
                }
                return true;
            }

            @Override
            protected void basicFire() {
                basicTarget.get(0).takeHits(shooter, 2, 1);
            }

            @Override
            protected void firstAdditionalFire() {
                firstAdditionalTarget.get(0).takeHits(shooter, 0, 1);
            }

            @Override
            protected void secondAdditionalFire() {
            }
        }

        private class MachineGun extends Weapon {
            private MachineGun(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(BLUE);
                basicPayment.add(RED);
                firstAdditionalPayment.add(YELLOW);
                secondAdditionalPayment.add(BLUE);
            }


            @Override
            protected boolean canFire() {
                possibleTarget.clear();
                addVisibleTarget();
                return !possibleTarget.isEmpty();
            }

            @Override
            protected boolean validateFireSort() {
                if (fireSort.size() == 1 && fireSort.get(0) == 1) return true;
                if (fireSort.size() == 2 && fireSort.get(0) == 1 &&
                        (fireSort.get(1) == 2 || fireSort.get(1) == 3)) return true;
                return (fireSort.size() == 3 && fireSort.get(0) == 1 && fireSort.get(1) == 2 && fireSort.get(2) == 3);
            }

            @Override
            protected boolean validateTargets() {
                for (Integer mode : fireSort) {
                    switch (mode) {
                        case 1:
                            if (basicTarget.size() > 2 || basicTarget.isEmpty()) return false;
                            if (basicTarget.size() == 2 && basicTarget.get(0).equals(basicTarget.get(1))) return false;
                            for (Player player : basicTarget) {
                                if (!shooter.canSee(player, cells)) return false;
                            }
                            break;
                        case 2:
                            if (firstAdditionalTarget.size() != 1) return false;
                            if (!basicTarget.contains(firstAdditionalTarget.get(0))) return false;
                            break;
                        case 3:
                            if (secondAdditionalTarget.size() > 2 || secondAdditionalTarget.isEmpty()) return false;
                            for (Player player : secondAdditionalTarget) {
                                if (!shooter.canSee(player, cells) ||
                                        firstAdditionalTarget.contains(player)) return false;
                            }
                            if (secondAdditionalTarget.size() == 2) {
                                if (secondAdditionalTarget.get(0).equals(secondAdditionalTarget.get(1))) return false;
                                if ((basicTarget.contains(secondAdditionalTarget.get(0)) &&
                                        basicTarget.contains(secondAdditionalTarget.get(1))) ||
                                        (!basicTarget.contains(secondAdditionalTarget.get(0)) &&
                                                !basicTarget.contains(secondAdditionalTarget.get(1)))) return false;
                            }
                            break;
                    }
                }
                return true;
            }

            @Override
            public void basicFire() {
                for (Player player : basicTarget) {
                    player.takeHits(shooter, 1, 0);
                }
            }

            @Override
            public void firstAdditionalFire() {
                firstAdditionalTarget.get(0).takeHits(shooter, 1, 0);
            }

            //qui controllo che sia colpibile anche un secondo bersaglio
            @Override
            public void secondAdditionalFire() {
                for (Player player : secondAdditionalTarget) {
                    player.takeHits(shooter, 1, 0);
                }
            }
        }

        private class Thor extends Weapon {
            private Thor(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(BLUE);
                basicPayment.add(RED);
                firstAdditionalPayment.add(BLUE);
                secondAdditionalPayment.add(BLUE);
            }

            @Override
            protected boolean canFire() {
                possibleTarget.clear();
                addVisibleTarget();
                return !possibleTarget.isEmpty();
            }

            @Override
            protected boolean validateFireSort() {
                if (fireSort.size() == 1 && fireSort.get(0) == 1) return true;
                if (fireSort.size() == 2 && fireSort.get(0) == 1 && fireSort.get(1) == 2) return true;
                return (fireSort.size() == 3 && fireSort.get(0) == 1 && fireSort.get(1) == 2 &&
                        fireSort.get(2) == 3);
            }

            @Override
            protected boolean validateTargets() {
                if (basicTarget.size() != 1) return false;
                if (fireSort.size() > 1 && (firstAdditionalTarget.size() != 1 ||
                        firstAdditionalTarget.get(0).equals(basicTarget.get(0)) ||
                        !basicTarget.get(0).canSee(firstAdditionalTarget.get(0), cells))) return false;
                return !(fireSort.size() > 2 && (secondAdditionalTarget.size() != 1 ||
                        secondAdditionalTarget.get(0).equals(firstAdditionalTarget.get(0)) ||
                        secondAdditionalTarget.get(0).equals(basicTarget.get(0)) ||
                        !firstAdditionalTarget.get(0).canSee(secondAdditionalTarget.get(0), cells)));
            }

            @Override
            public void basicFire() {
                basicTarget.get(0).takeHits(shooter, 2, 0);
            }

            @Override
            public void firstAdditionalFire() {
                firstAdditionalTarget.get(0).takeHits(shooter, 1, 0);
            }

            @Override
            public void secondAdditionalFire() {
                secondAdditionalTarget.get(0).takeHits(shooter, 2, 0);
            }
        }

        private class PlasmaGun extends Weapon {
            private PlasmaGun(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(BLUE);
                basicPayment.add(YELLOW);
                secondAdditionalPayment.add(BLUE);
            }

            @Override
            protected boolean canFire() {
                return true;
            }

            @Override
            protected boolean validateFireSort() {
                if (fireSort.size() > 3 || fireSort.isEmpty()) return false;
                if (fireSort.size() == 1 && fireSort.get(0) == 1) return true;
                if (fireSort.size() > 1 && !((fireSort.get(0) == 1 && fireSort.get(1) == 2) ||
                        (fireSort.get(0) == 2 && fireSort.get(1) == 1))) return false;
                return !(fireSort.size() == 3 && fireSort.get(2) != 3);
            }

            @Override
            protected boolean validateTargets() {
                if (basicTarget.size() != 1 ||
                        (fireSort.contains(2) && firstAdditionalTargetPoint.size() != 1)) return false;
                if (!firstAdditionalTargetPoint.isEmpty()) {
                    if (!game.canMove(shooter.getPosition(), firstAdditionalTargetPoint.get(0), 0, 2))
                        return false;
                }
                if (fireSort.get(0) == 1 && !shooter.canSee(basicTarget.get(0), cells)) return false;
                if (fireSort.contains(2) && fireSort.indexOf(2) < fireSort.indexOf(1)) {
                    if (!basicTarget.get(0).canBeSeenFrom(firstAdditionalTargetPoint.get(0), cells)) return false;
                }
                return !(fireSort.get(0) == 2);
            }

            @Override
            public void basicFire() {
                basicTarget.get(0).takeHits(shooter, 2, 0);
            }

            @Override
            public void firstAdditionalFire() { shooter.setPosition(firstAdditionalTargetPoint.get(0)); }

            @Override
            public void secondAdditionalFire() {
                basicTarget.get(0).takeHits(shooter, 1, 0);
            }
        }

        public class Whisper extends Weapon {
            public Whisper(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                           boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(BLUE);
                basicPayment.add(BLUE);
                basicPayment.add(YELLOW);
            }

            @Override
            protected boolean canFire() {
                return true;
            }

            @Override
            protected boolean validateFireSort() {
                fireSort.clear();
                fireSort.add(1);
                return true;
            }

            @Override
            protected boolean validateTargets() {
                if (basicTarget.size() != 1) return false;
                return shooter.canSee(basicTarget.get(0), cells) && !shooter.isPlayerNear(basicTarget.get(0), cells);
            }

            @Override
            public void basicFire() {
                basicTarget.get(0).takeHits(shooter, 3, 1);
            }

            @Override
            public void firstAdditionalFire() {
            }

            @Override
            public void secondAdditionalFire() {
            }
        }

        public class Electroscythe extends Weapon {
            public Electroscythe(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                                 boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(BLUE);
                alternativePayment.add(BLUE);
                alternativePayment.add(RED);
            }

            @Override
            protected boolean canFire() {
                return true;
            }

            @Override
            protected boolean validateFireSort() {
                fireSort.clear();
                fireSort.add(1);
                return true;
            }

            @Override
            protected boolean validateTargets() {
                basicTarget.clear();
                for (Player player : players) {
                    if (shooter.getPosition().equals(player.getPosition()) &&
                            !shooter.equals(player)) basicTarget.add(player);
                }
                return !basicTarget.isEmpty();
            }

            @Override
            public void basicFire() {
                if (!alternativeFire) {
                    for (Player player : basicTarget) {
                        player.takeHits(shooter, 1, 0);
                    }
                } else {
                    for (Player player : basicTarget) {
                        player.takeHits(shooter, 2, 0);
                    }
                }
            }

            @Override
            public void firstAdditionalFire() { }

            @Override
            public void secondAdditionalFire() { }
        }

        public class TractorBeam extends Weapon {
            public TractorBeam(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                               boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(BLUE);
                alternativePayment.add(RED);
                alternativePayment.add(YELLOW);
            }

            @Override
            protected boolean canFire() {
                return true;
            }

            @Override
            protected boolean validateFireSort() {
                fireSort.clear();
                fireSort.add(1);
                return true;
            }

            @Override
            protected boolean validateTargets() {
                if (alternativeFire) {
                    basicTargetPoint.clear();
                    basicTargetPoint.add(shooter.getPosition());
                }
                if (basicTarget.size() != 1) return false;
                if (basicTarget.get(0).equals(shooter)) return false;
                if (basicTargetPoint.size() !=1) return false;
                return game.canMove(basicTarget.get(0).getPosition(), basicTargetPoint.get(0), 0, 3);
            }

            @Override
            public void basicFire() {
                basicTarget.get(0).setPosition(basicTargetPoint.get(0));
                if(!alternativeFire){
                    basicTarget.get(0).takeHits(shooter, 1, 0);
                } else {
                    basicTarget.get(0).takeHits(shooter, 3, 0);
                }
            }

            @Override
            public void firstAdditionalFire() { }

            @Override
            public void secondAdditionalFire() { }
        }

        public class VortexCannon extends Weapon {
            public VortexCannon(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                                boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(RED);
                basicPayment.add(BLUE);
                firstAdditionalPayment.add(RED);
            }

            //fit in possibleTarget players (except shooter) that are near the Vortex
            private void addNearTargets() {
                possibleTarget.clear();
                for (Player p : players) {
                    if (!(shooter.equals((p)))) {
                        if (p.getPosition().equals(basicTargetPoint.get(0))) possibleTarget.add(p);
                        else {
                            for (Bounds.Direction d : Bounds.Direction.values()) {
                                if (cells[basicTargetPoint.get(0).x][basicTargetPoint.get(0).y].getBounds()
                                        .getType(d) != Bounds.Type.WALL
                                        && basicTargetPoint.get(0).x + d.getdX() == p.getPosition().x
                                        && basicTargetPoint.get(0).y + d.getdY() == p.getPosition().y)
                                    possibleTarget.add(p);
                            }
                        }
                    }
                }
            }

            @Override
            protected boolean canFire() {
                addNearTargets();
                return shooter.canSeeCell(basicTargetPoint.get(0), cells) && !possibleTarget.isEmpty();
            }

            @Override
            protected boolean validateFireSort() {
                if (fireSort.isEmpty() || fireSort.size() > 2) return false;
                if (fireSort.get(0) != 1) return false;
                return !(fireSort.size() == 2 && fireSort.get(1) != 2);
            }

            @Override
            protected boolean validateTargets() {
                if (basicTargetPoint.size() != 1 || !shooter.canSeeCell(basicTargetPoint.get(0), cells) ||
                        basicTarget.size() != 1 ||
                        !basicTarget.get(0).isCellNear(basicTargetPoint.get(0), cells)) return false;
                if (fireSort.size() == 2) {
                    if (firstAdditionalTarget.isEmpty() || firstAdditionalTarget.size() > 2) return false;
                    if (firstAdditionalTarget.size() == 2 &&
                            firstAdditionalTarget.get(0).equals(firstAdditionalTarget.get(1))) return false;
                    for (Player player : firstAdditionalTarget) {
                        if (player.equals(basicTarget.get(0)) ||
                                !player.isCellNear(basicTargetPoint.get(0), cells)) return false;
                    }
                }
                return true;
            }

            @Override
            public void basicFire() {
                basicTarget.get(0).setPosition(basicTargetPoint.get(0));
                basicTarget.get(0).takeHits(shooter, 2, 0);
            }

            @Override
            public void firstAdditionalFire() {
                for (Player player : firstAdditionalTarget) {
                    player.setPosition(basicTargetPoint.get(0));
                    player.takeHits(shooter, 1, 0);
                }
            }

            @Override
            public void secondAdditionalFire() { }
        }

        public class Furnace extends Weapon {
            public Furnace(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                           boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(RED);
                basicPayment.add(BLUE);
            }

            //so need to be selected the square near the shooter, not one square in the room...
            private void addPossibleRooms() {
                possibleTargetPoint.clear();
                for (var d : Bounds.Direction.values()) {
                    if (cells[shooter.getPosition().x][shooter.getPosition().y].getBounds().getType(d)
                            == Bounds.Type.DOOR) possibleTargetPoint.add(new Point(shooter.getPosition().x + d.getdX(),
                            shooter.getPosition().y + d.getdY()));
                }
            }

            @Override
            protected boolean canFire() {
                return true;
            }

            @Override
            protected boolean validateFireSort() {
                fireSort.clear();
                fireSort.add(1);
                return true;
            }

            @Override
            protected boolean validateTargets() {
                if (basicTargetPoint.size() != 1 || basicTargetPoint.get(0).equals(shooter.getPosition())) return false;
                if (!alternativeFire) {
                    addPossibleRooms();
                    if (!possibleTargetPoint.contains(basicTargetPoint.get(0))) return false;
                    basicTarget.clear();
                    for (Player player : players) {
                        if (cells[player.getPosition().x][player.getPosition().y].getColor() ==
                                cells[basicTargetPoint.get(0).x][basicTargetPoint.get(0).y].getColor())
                            basicTarget.add(player);
                    }
                } else {
                    if (!shooter.isCellNear(basicTargetPoint.get(0), cells)) return false;
                    basicTarget.clear();
                    for (Player player : players) {
                        if (player.getPosition().equals(basicTargetPoint.get(0))) basicTarget.add(player);
                    }
                }
                return !basicTarget.isEmpty();
            }

            @Override
            public void basicFire() {
                if (!alternativeFire) {
                    for (Player player : basicTarget) {
                        player.takeHits(shooter, 1, 0);
                    }
                } else {
                    for (Player player : basicTarget) {
                        player.takeHits(shooter, 1, 1);
                    }
                }
            }

            @Override
            public void firstAdditionalFire() { }

            @Override
            public void secondAdditionalFire() { }
        }

        public class Heatseeker extends Weapon {
            public Heatseeker(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                              boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(RED);
                basicPayment.add(RED);
                basicPayment.add(YELLOW);
            }

            @Override
            protected boolean canFire() {
                addNonVisibleTarget();
                return !possibleTarget.isEmpty();
            }

            @Override
            protected boolean validateFireSort() {
                fireSort.clear();
                fireSort.add(1);
                return true;
            }

            @Override
            protected boolean validateTargets() {
                addNonVisibleTarget();
                return basicTarget.size() == 1 && possibleTarget.contains(basicTarget.get(0));
            }

            @Override
            public void basicFire() {
                basicTarget.get(0).takeHits(shooter, 3, 0);
            }

            @Override
            public void firstAdditionalFire() { }

            @Override
            public void secondAdditionalFire() { }
        }

        public class Hellion extends Weapon {
            public Hellion(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                           boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(RED);
                basicPayment.add(YELLOW);
                alternativePayment.add(RED);
            }

            @Override
            protected boolean canFire() {
                possibleTarget.clear();
                for (Player player : players) {
                    if (shooter.canSee(player, cells) && !shooter.isPlayerNear(player, cells))
                        possibleTarget.add(player);
                }
                return !possibleTarget.isEmpty();
            }

            @Override
            protected boolean validateFireSort() {
                fireSort.clear();
                fireSort.add(1);
                return true;
            }

            @Override
            protected boolean validateTargets() {
                if (basicTarget.size() != 1) return false;
                return (shooter.canSee(basicTarget.get(0), cells) && !shooter.isPlayerNear(basicTarget.get(0), cells));
            }

            @Override
            public void basicFire() {
                basicTarget.get(0).takeHits(shooter, 1, 0);
                if (!alternativeFire) {
                    for (Player player : players) {
                        if (player.getPosition().equals(basicTarget.get(0).getPosition()))
                            player.takeHits(shooter, 0, 1);
                    }
                } else {
                    for (Player player : players) {
                        if (player.getPosition().equals(basicTarget.get(0).getPosition()))
                            player.takeHits(shooter, 0, 2);
                    }
                }
            }

            @Override
            public void firstAdditionalFire() {
            }

            @Override
            public void secondAdditionalFire() {
            }
        }

        public class Flamethrower extends Weapon {
            public Flamethrower(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                                boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(RED);
                alternativePayment.add(YELLOW);
                alternativePayment.add(YELLOW);
            }

            @Override
            protected boolean canFire() {
                possibleTarget.clear();
                for (Player player : players) {
                    if (shooter.isPlayerNear(player, cells) || shooter.isPlayerNear2(player, cells))
                        possibleTarget.add(player);
                }
                return !possibleTarget.isEmpty();
            }

            @Override
            protected boolean validateFireSort() {
                fireSort.clear();
                fireSort.add(1);
                return true;
            }

            @Override
            protected boolean validateTargets() {
                if (!alternativeFire) {
                    if (basicTarget.isEmpty() || basicTarget.size() > 2) return false;
                    if (basicTarget.size() == 1 && (shooter.isPlayerNear(basicTarget.get(0), cells) ||
                            shooter.isPlayerNear2(basicTarget.get(0), cells))) return true;
                    return basicTarget.size() == 2 && ((shooter.isPlayerNear(basicTarget.get(0), cells) &&
                            shooter.isPlayerNear2(basicTarget.get(1), cells)) ||
                            (shooter.isPlayerNear(basicTarget.get(1), cells) &&
                                    shooter.isPlayerNear2(basicTarget.get(0), cells)));
                } else {
                    if (basicTargetPoint.size() != 2) return false;
                    if (!(shooter.isCellNear(basicTargetPoint.get(0), cells) &&
                            shooter.isCellNear2Straight(basicTargetPoint.get(1), cells))) return false;
                    basicTarget.clear();
                    firstAdditionalTarget.clear();
                    for (Player player : players) {
                        if (player.getPosition().equals(basicTargetPoint.get(0))) basicTarget.add(player);
                        else if (player.getPosition().equals(basicTargetPoint.get(1)))
                            firstAdditionalTarget.add(player);
                    }
                    return !(basicTarget.isEmpty() && firstAdditionalTarget.isEmpty());
                }
            }

            @Override
            public void basicFire() {
                if (!alternativeFire) {
                    for (Player player : basicTarget) {
                        player.takeHits(shooter, 1, 0);
                    }
                } else {
                    for (Player player : basicTarget) {
                        player.takeHits(shooter, 2, 0);
                    }
                    for (Player player : firstAdditionalTarget) {
                        player.takeHits(shooter, 1, 0);
                    }
                }
            }

            @Override
            public void firstAdditionalFire() {
            }

            @Override
            public void secondAdditionalFire() {
            }
        }

        public class GrenadeLauncher extends Weapon {
            public GrenadeLauncher(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                                   boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(RED);
                firstAdditionalPayment.add(RED);
            }

            @Override
            protected boolean canFire() {
                addVisibleTarget();
                return !possibleTarget.isEmpty();
            }

            @Override
            protected boolean validateFireSort() {
                if (fireSort.isEmpty() || fireSort.size() > 2) return false;
                if (fireSort.size() == 1 && fireSort.get(0) != 1) return false;
                return !(fireSort.size() == 2 && !((fireSort.get(0) == 1 && fireSort.get(1) == 2) ||
                        (fireSort.get(0) == 2 && fireSort.get(1) == 1)));
            }

            @Override
            protected boolean validateTargets() {
                if (basicTarget.size() != 1 || basicTargetPoint.size() > 1) return false;
                if (!shooter.canSee(basicTarget.get(0), cells)) return false;
                if (fireSort.size() == 2 && (firstAdditionalTargetPoint.size() != 1 ||
                        !shooter.canSeeCell(firstAdditionalTargetPoint.get(0), cells))) return false;
                firstAdditionalTarget.clear();
                if (fireSort.size() == 2) {
                    for (Player player : players) {
                        if (player.getPosition().equals(firstAdditionalTargetPoint.get(0)) &&
                                !player.equals(shooter)) firstAdditionalTarget.add(player);
                    }
                }
                if (!basicTargetPoint.isEmpty()) {
                    if (!basicTarget.get(0).isCellNear(basicTargetPoint.get(0), cells)) return false;
                    if (fireSort.size() == 2) {
                        if (fireSort.get(0) == 1) {
                            if (basicTargetPoint.get(0).equals(firstAdditionalTargetPoint.get(0)))
                                firstAdditionalTarget.add(basicTarget.get(0));
                            else if (basicTarget.get(0).getPosition().equals(firstAdditionalTargetPoint.get(0)))
                                firstAdditionalTarget.remove(basicTarget.get(0));
                        }
                    }
                }
                return fireSort.size() == 1 || !firstAdditionalTarget.isEmpty();
            }

            @Override
            public void basicFire() {
                basicTarget.get(0).takeHits(shooter, 1, 0);
                if (!basicTargetPoint.isEmpty()) basicTarget.get(0).setPosition(basicTargetPoint.get(0));
            }

            @Override
            public void firstAdditionalFire() {
                for (Player player : firstAdditionalTarget) {
                    player.takeHits(shooter, 1, 0);
                }
            }

            @Override
            public void secondAdditionalFire() {
            }
        }

        public class RocketLauncher extends Weapon {
            public RocketLauncher(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                                  boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(RED);
                basicPayment.add(RED);
                firstAdditionalPayment.add(BLUE);
                secondAdditionalPayment.add(YELLOW);
            }

            @Override
            protected boolean canFire() {
                return true;
            }

            @Override
            protected boolean validateFireSort() {
                if (fireSort.size() == 1 && fireSort.get(0) == 1) return true;
                if (fireSort.size() == 2 && fireSort.contains(1) && (fireSort.contains(3) || fireSort.contains(2)))
                    return true;
                return (fireSort.size() == 3 && fireSort.contains(1) && fireSort.contains(2) && fireSort.contains(3));
            }

            @Override
            protected boolean validateTargets() {
                if (basicTarget.size() != 1) return false;
                secondAdditionalTarget.clear();
                if (fireSort.contains(2)) {
                    if (firstAdditionalTargetPoint.size() != 1) return false;
                    if(!game.canMove(shooter.getPosition(), firstAdditionalTargetPoint.get(0), 0, 2))
                    if (fireSort.indexOf(2) < fireSort.indexOf(1)) {
                        if (!(basicTarget.get(0).canBeSeenFrom(firstAdditionalTargetPoint.get(0), cells))) return false;
                    } else {
                        if (!shooter.canSee(basicTarget.get(0), cells)) return false;
                    }
                } else {
                    if (!shooter.canSee(basicTarget.get(0), cells)) return false;
                }
                if (!basicTargetPoint.isEmpty()) {
                    if (basicTargetPoint.size() != 1 ||
                            !basicTarget.get(0).isCellNear(basicTargetPoint.get(0), cells)) return false;
                }
                for (Player player : players) {
                    if (player.getPosition().equals(basicTarget.get(0).getPosition()))
                        secondAdditionalTarget.add(player);
                }
                return true;
            }

            @Override
            public void basicFire() {
                basicTarget.get(0).takeHits(shooter, 2, 0);
                if (!basicTargetPoint.isEmpty()) basicTarget.get(0).setPosition(basicTargetPoint.get(0));
            }

            @Override
            public void firstAdditionalFire() { shooter.setPosition(firstAdditionalTargetPoint.get(0)); }

            @Override
            public void secondAdditionalFire() {
                for (Player player : secondAdditionalTarget) {
                    player.takeHits(shooter, 1, 0);
                }
            }
        }

        public class Railgun extends Weapon {
            public Railgun(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                           boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(YELLOW);
                basicPayment.add(YELLOW);
                basicPayment.add(BLUE);
            }

            @Override
            protected boolean canFire() {
                possibleTarget.clear();
                for (Player player : players) {
                    if (!player.equals(shooter) && (player.getPosition().x == shooter.getPosition().x ||
                            player.getPosition().y == shooter.getPosition().y)) possibleTarget.add(player);
                }
                return !possibleTarget.isEmpty();
            }

            @Override
            protected boolean validateFireSort() {
                fireSort.clear();
                fireSort.add(1);
                return true;
            }

            @Override
            protected boolean validateTargets() {
                if (basicTarget.isEmpty() || basicTarget.size() > 2) return false;
                if (basicTarget.size() == 1 && !alternativeFire) {
                    return !basicTarget.get(0).equals(shooter) &&
                            (basicTarget.get(0).getPosition().x == shooter.getPosition().x ||
                                    basicTarget.get(0).getPosition().y == shooter.getPosition().y);
                }
                if (basicTarget.size() == 2 && alternativeFire && !basicTarget.get(0).equals(basicTarget.get(1))) {
                    for (Player player : basicTarget) {
                        if (player.equals(shooter)) return false;
                    }
                    if (basicTarget.get(0).getPosition().x == basicTarget.get(1).getPosition().x) {
                        return (basicTarget.get(0).getPosition().y - shooter.getPosition().y) *
                                (basicTarget.get(1).getPosition().y - shooter.getPosition().y) >= 0;
                    } else if (basicTarget.get(0).getPosition().y == basicTarget.get(1).getPosition().y) {
                        return (basicTarget.get(0).getPosition().x - shooter.getPosition().x) *
                                (basicTarget.get(1).getPosition().x - shooter.getPosition().x) >= 0;
                    }
                }
                return false;
            }

            @Override
            public void basicFire() {
                if (!alternativeFire) {
                    basicTarget.get(0).takeHits(shooter, 3, 0);
                } else {
                    for (Player player : basicTarget) {
                        player.takeHits(shooter, 2, 0);
                    }
                }
            }

            @Override
            public void firstAdditionalFire() {
            }

            @Override
            public void secondAdditionalFire() {
            }
        }

        public class Cyberblade extends Weapon {
            public Cyberblade(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                              boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(YELLOW);
                basicPayment.add(RED);
                secondAdditionalPayment.add(YELLOW);
            }

            @Override
            protected boolean canFire() {
                possibleTarget.clear();
                for (Player player : players) {
                    if (!player.equals(shooter) && (shooter.getPosition().equals(player.getPosition()) ||
                            shooter.isPlayerNear(player, cells))) possibleTarget.add(player);
                }
                return !possibleTarget.isEmpty();
            }

            @Override
            protected boolean validateFireSort() {
                if (fireSort.isEmpty() || fireSort.size() > 3 || !fireSort.contains(1)) return false;
                if (fireSort.size() > 1 && !(fireSort.contains(2) || fireSort.contains(3))) return false;
                if (fireSort.size() == 3 && !(fireSort.contains(2) && fireSort.contains(3))) return false;
                return !(fireSort.contains(3) && fireSort.indexOf(1) > fireSort.indexOf(3));
            }

            @Override
            protected boolean validateTargets() {
                if (basicTarget.size() != 1) return false;
                if (fireSort.size() == 1) return shooter.getPosition().equals(basicTarget.get(0).getPosition());
                if (fireSort.contains(2)) {
                    if (firstAdditionalTargetPoint.size() != 1 ||
                            !shooter.isCellNear(firstAdditionalTargetPoint.get(0), cells)) return false;
                    if (fireSort.indexOf(2) < fireSort.indexOf(1)) {
                        if (!basicTarget.get(0).getPosition().equals(firstAdditionalTargetPoint.get(0))) return false;
                    } else {
                        if (!shooter.getPosition().equals(basicTarget.get(0).getPosition())) return false;
                    }
                }
                if (fireSort.contains(3) && secondAdditionalTarget.size() != 1) return false;
                if (fireSort.contains(3) && !fireSort.contains(2) &&
                        !shooter.getPosition().equals(secondAdditionalTarget.get(0).getPosition())) return false;
                if (fireSort.contains(2) && fireSort.contains(3)) {
                    if (fireSort.indexOf(2) < fireSort.indexOf(3)) {
                        return secondAdditionalTarget.get(0).getPosition().equals(firstAdditionalTargetPoint.get(0));
                    } else {
                        return shooter.getPosition().equals(secondAdditionalTarget.get(0).getPosition());
                    }
                }
                return true;
            }

            @Override
            public void basicFire() {
                basicTarget.get(0).takeHits(shooter, 2, 0);
            }

            @Override
            public void firstAdditionalFire() { shooter.setPosition(firstAdditionalTargetPoint.get(0)); }

            @Override
            public void secondAdditionalFire() {
                secondAdditionalTarget.get(0).takeHits(shooter, 2, 0);
            }
        }

        public class ZX2 extends Weapon {
            public ZX2(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                       boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(YELLOW);
                basicPayment.add(RED);
            }

            @Override
            protected boolean canFire() {
                addVisibleTarget();
                return !possibleTarget.isEmpty();
            }

            @Override
            protected boolean validateFireSort() {
                fireSort.clear();
                fireSort.add(1);
                return true;
            }

            @Override
            protected boolean validateTargets() {
                if (basicTarget.isEmpty()) return false;
                if (!alternativeFire &&
                        (basicTarget.size() != 1 || !shooter.canSee(basicTarget.get(0), cells))) return false;
                if (alternativeFire) {
                    if (basicTarget.size() > 3) return false;
                    for (Player player : basicTarget) {
                        if (!shooter.canSee(player, cells)) return false;
                    }
                }
                return true;
            }

            @Override
            public void basicFire() {
                if (!alternativeFire) {
                    basicTarget.get(0).takeHits(shooter, 1, 2);
                } else {
                    for (Player player : basicTarget) {
                        player.takeHits(shooter, 0, 1);
                    }
                }
            }

            @Override
            public void firstAdditionalFire() {
            }

            @Override
            public void secondAdditionalFire() {
            }
        }

        public class Shotgun extends Weapon {
            public Shotgun(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                           boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(YELLOW);
                basicPayment.add(YELLOW);
            }

            @Override
            protected boolean canFire() {
                for (Player player : players) {
                    if (!shooter.equals(player) && (shooter.getPosition().equals(player.getPosition()) ||
                            shooter.isPlayerNear(player, cells))) possibleTarget.add(player);
                }
                return !possibleTarget.isEmpty();
            }

            @Override
            protected boolean validateFireSort() {
                fireSort.clear();
                fireSort.add(1);
                return false;
            }

            @Override
            protected boolean validateTargets() {
                if (basicTarget.size() != 1) return false;
                if (!alternativeFire) {
                    if (!shooter.getPosition().equals(basicTarget.get(0).getPosition())) return false;
                    if (!basicTargetPoint.isEmpty()) {
                        return (basicTargetPoint.size() == 1 && shooter.isCellNear(basicTargetPoint.get(0), cells));
                    }
                } else {
                    return shooter.isPlayerNear(basicTarget.get(0), cells);
                }
                return true;
            }

            @Override
            public void basicFire() {
                if (!alternativeFire) {
                    basicTarget.get(0).takeHits(shooter, 3, 0);
                    if (!basicTargetPoint.isEmpty()) {
                        basicTarget.get(0).setPosition(basicTargetPoint.get(0));
                    }
                } else {
                    basicTarget.get(0).takeHits(shooter, 2, 0);
                }
            }

            @Override
            public void firstAdditionalFire() {
            }

            @Override
            public void secondAdditionalFire() {
            }
        }

        public class PowerGlove extends Weapon {
            public PowerGlove(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                              boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(YELLOW);
                basicPayment.add(BLUE);
                alternativePayment.add(BLUE);
            }

            @Override
            protected boolean canFire() {
                possibleTarget.clear();
                for (Player player : players) {
                    if (shooter.isPlayerNear(player, cells) || shooter.isPlayerNear2(player, cells))
                        possibleTarget.add(player);
                }
                return !possibleTarget.isEmpty();
            }

            @Override
            protected boolean validateFireSort() {
                fireSort.clear();
                fireSort.add(1);
                return true;
            }

            @Override
            protected boolean validateTargets() {
                if (basicTarget.isEmpty() && basicTargetPoint.isEmpty()) return false;
                if (!alternativeFire) {
                    return (basicTarget.size() == 1 && shooter.isPlayerNear(basicTarget.get(0), cells));
                } else {
                    if (basicTargetPoint.isEmpty() || basicTargetPoint.size() > 2 ||
                            basicTarget.size() > 2) return false;
                    if (basicTargetPoint.size() == 1) {
                        if (!(shooter.isCellNear(basicTargetPoint.get(0), cells) ||
                                shooter.isCellNear2Straight(basicTargetPoint.get(0), cells))) return false;
                        if (basicTarget.size() > 1) return false;
                        if (!basicTarget.isEmpty()) {
                            return basicTarget.get(0).getPosition().equals(basicTargetPoint.get(0));
                        }
                        return true;
                    } else {
                        if (basicTarget.size() == 1) {
                            if (!(basicTarget.get(0).getPosition().equals(basicTargetPoint.get(0)) ||
                                    basicTarget.get(0).getPosition().equals(basicTargetPoint.get(1)))) return false;
                        }
                        if (basicTarget.size() == 2) {
                            if (!(basicTarget.get(0).getPosition().equals(basicTargetPoint.get(0)) &&
                                    basicTarget.get(1).getPosition().equals(basicTargetPoint.get(1)))) return false;
                        }
                        for (Bounds.Direction d : Bounds.Direction.values()) {
                            if (cells[shooter.getPosition().x][shooter.getPosition().y].getBounds().getType(d) !=
                                    Bounds.Type.WALL &&
                                    shooter.getPosition().x + d.getdX() == basicTargetPoint.get(0).x &&
                                    shooter.getPosition().y + d.getdY() == basicTargetPoint.get(0).y) {
                                if (cells[shooter.getPosition().x + d.getdX()][shooter.getPosition().y + d.getdY()]
                                        .getBounds().getType(d) != Bounds.Type.WALL &&
                                        shooter.getPosition().x + 2*d.getdX() == basicTargetPoint.get(1).x &&
                                        shooter.getPosition().y + 2*d.getdY() == basicTargetPoint.get(1).y) return true;
                            }
                        }
                        return false;
                    }
                }
            }

            @Override
            public void basicFire() {
                if (!alternativeFire) {
                    shooter.setPosition(basicTarget.get(0).getPosition());
                    basicTarget.get(0).takeHits(shooter, 1, 2);
                } else {
                    for (int i = 0; i < basicTargetPoint.size(); i++) {
                        shooter.setPosition(basicTargetPoint.get(i));
                        if (basicTarget.size() > i) {
                            basicTarget.get(i).takeHits(shooter, 2, 0);
                        }
                    }
                }
            }

            @Override
            public void firstAdditionalFire() {
            }

            @Override
            public void secondAdditionalFire() {
            }
        }

        public class Shockwave extends Weapon {
            public Shockwave(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                             boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(YELLOW);
                alternativePayment.add(YELLOW);
            }

            @Override
            protected boolean canFire() {
                possibleTarget.clear();
                for (Player player : players) {
                    if (shooter.isPlayerNear(player, cells)) possibleTarget.add(player);
                }
                return !possibleTarget.isEmpty();
            }

            @Override
            protected boolean validateFireSort() {
                fireSort.clear();
                fireSort.add(1);
                return true;
            }

            @Override
            protected boolean validateTargets() {
                if (!alternativeFire) {
                    if (basicTarget.isEmpty() || basicTarget.size() > 3) return false;
                    for (Player player : basicTarget) {
                        if (!shooter.isPlayerNear(player, cells)) return false;
                    }
                    for (int i = 0; i < basicTarget.size() - 1; i++) {
                        for (Player player : basicTarget.subList(i + 1, basicTarget.size() - 1)) {
                            if (basicTarget.get(i).getPosition().equals(player.getPosition())) return false;
                        }
                    }
                } else {
                    basicTarget.clear();
                    for (Player player : players) {
                        if (shooter.isPlayerNear(player, cells)) basicTarget.add(player);
                    }
                    return !basicTarget.isEmpty();
                }
                return true;
            }

            @Override
            public void basicFire() {
                for (Player player : basicTarget) {
                    player.takeHits(shooter, 1, 0);
                }
            }

            @Override
            public void firstAdditionalFire() {
            }

            @Override
            public void secondAdditionalFire() {
            }
        }

        public class Sledgehammer extends Weapon {
            public Sledgehammer(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                                boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(YELLOW);
                alternativePayment.add(RED);
            }

            @Override
            protected boolean canFire() {
                possibleTarget.clear();
                for (Player player : players) {
                    if (shooter.getPosition().equals(player.getPosition())) possibleTarget.add(player);
                }
                return !possibleTarget.isEmpty();
            }

            @Override
            protected boolean validateFireSort() {
                fireSort.clear();
                fireSort.add(1);
                return true;
            }

            @Override
            protected boolean validateTargets() {
                if (basicTarget.size() != 1) return false;
                if (!basicTarget.get(0).getPosition().equals(shooter.getPosition())) return false;
                if (!alternativeFire) {
                    if (!basicTargetPoint.isEmpty()) {
                        if (basicTargetPoint.size() != 1) return false;
                        return shooter.isCellNear(basicTargetPoint.get(0), cells) ||
                                shooter.isCellNear2Straight(basicTargetPoint.get(0), cells);
                    }
                }
                return true;
            }

            @Override
            public void basicFire() {
                if (!alternativeFire) {
                    basicTarget.get(0).takeHits(shooter, 2, 0);
                } else {
                    basicTarget.get(0).takeHits(shooter, 3, 0);
                    if (!basicTargetPoint.isEmpty()) {
                        basicTarget.get(0).setPosition(basicTargetPoint.get(0));
                    }
                }
            }

            @Override
            public void firstAdditionalFire() {
            }

            @Override
            public void secondAdditionalFire() {
            }
        }
    }
}