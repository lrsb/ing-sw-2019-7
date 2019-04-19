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
    private @NotNull ArrayList<PowerUp> powerUpsPay;
    private @NotNull ArrayList<AmmoCard.Color> basicPayment;
    private @NotNull ArrayList<AmmoCard.Color> firstAdditionalPayment;
    private @NotNull ArrayList<AmmoCard.Color> secondAdditionalPayment;
    private @NotNull ArrayList<AmmoCard.Color> alternativePayment;


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
                  boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
        this.cells = cells;
        this.players = players;
        this.shooter = shooter;
        this.alternativeFire = alternativeFire;
        this.powerUpsPay = powerUpsPay;
    }

    public boolean charging(){
        return payCost(basicPayment);
    }

    //used to reload and to pay additional cost
    public boolean payCost(@NotNull ArrayList<AmmoCard.Color> cost) {
        //red, blue e yellow avranno il valore del costo totale
        //altNomeColore hanno il valore dei cubi che vengono invece pagati tramite PowerUp
        int red = 0, yellow = 0, blue = 0, altRed = 0, altYellow = 0, altBlue = 0;
        for (AmmoCard.Color c : cost) {
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
        for (PowerUp p : powerUpsPay) {
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

    public void addVisibleSquare(){
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
        return obj instanceof Weapon && ((Weapon) obj).getClass().equals(getClass());
    }

    protected boolean canFire() {
        return false;
    }

    public enum Name {
        LOCK_RIFLE(Weapons.LockRifle.class), MACHINE_GUN(Weapons.MachineGun.class), THOR(Weapons.Thor.class),
        PLASMA_GUN(Weapons.PlasmaGun.class), WHISPER(Weapons.Whisper.class), ELECTROSCYTHE(Weapons.Electroscythe.class),
        TRACTOR_BEAM(Weapons.TractorBeam.class), VORTEX_CANNON(Weapons.VortexCannon.class),
        FURNACE(Weapons.Furnace.class), HEATSEEKER(Weapons.Heatseeker.class), HELLION(Weapons.Hellion.class),
        FLAMETHROWER(Weapons.Flamethrower.class), GRENADE_LAUNCHER(Weapons.GrenadeLauncher.class),
        ROCKET_LAUNCHER(Weapons.RocketLauncher.class), RAILGUN(Weapons.Railgun.class),
        CYBERBLADE(Weapons.Cyberblade.class), ZX2(Weapons.ZX2.class), SHOTGUN(Weapons.Shotgun.class)/*,
        POWER_GLOVE(Weapons.PowerGlove.class)/*, SHOCKWAVE(Weapons.Shockwave.class)/*, SLEDGEHAMMER(Weapons.Sledgehammer.class)*/;

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
                for (Player p : possibleTarget) {
                    if (p.getName().equals(basicTarget.get(0).getName())) possibleTarget.remove(p);
                }
                if (possibleTarget.size() > 0) {
                    firstAdditionalTarget.clear();
                    //TODO: insert ONLY 1 Target from possibleTarget to firstAdditionalTarget
                    if (firstAdditionalTarget.size() != 1) return false;
                    if(payCost(firstAdditionalPayment)) {
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
            private MachineGun(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(BLUE);
                basicPayment.add(RED);
                firstAdditionalPayment.add(YELLOW);
                secondAdditionalPayment.add(RED);
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
                firstAdditionalTarget.clear();
                //TODO: choose 1 target from basicTarget to firstAdditionalTarget
                if (firstAdditionalTarget.size() != 1) return false;
                if (payCost(firstAdditionalPayment)) {
                    firstAdditionalTarget.get(0).addShooterHits(shooter, 1);
                    return true;
                }
                return false;
            }

            //qui controllo che sia colpibile anche un secondo bersaglio
            @Override
            public boolean secondAdditionalFire() {
                secondAdditionalTarget.clear();
                //TODO choose 1 or 2 targets adding to secondAdditionalTarget
                if (secondAdditionalTarget.size() < 1 || secondAdditionalTarget.size() > 2) return false;
                if (payCost(secondAdditionalPayment)){
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
            private Thor(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(BLUE);
                basicPayment.add(RED);
                firstAdditionalPayment.add(BLUE);
                secondAdditionalPayment.add(BLUE);
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
                possibleTarget.clear();
                addVisibleTarget(basicTarget.get(0));
                if (possibleTarget.size() > 0) {
                    firstAdditionalTarget.clear();
                    //TODO: choose ONLY 1 target from possibleTarget to firstAdditionalTarget
                    if (firstAdditionalTarget.size() != 1) return false;
                    if (payCost(firstAdditionalPayment)) {
                        firstAdditionalTarget.get(0).addShooterHits(shooter, 1);
                        firstAdditionalTarget.get(0).convertShooterMarks(shooter);
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean secondAdditionalFire() {
                possibleTarget.clear();
                addVisibleTarget(firstAdditionalTarget.get(0));
                for (Player p : possibleTarget) {
                    if (p.getName().equals(shooter.getName())
                            || p.getName().equals(firstAdditionalTarget.get(0).getName())) possibleTarget.remove(p);
                }
                if (possibleTarget.size() > 0) {
                    secondAdditionalTarget.clear();
                    //TODO: choose ONLY 1 target from possibleTarget to secondAdditionalTarget
                    if (secondAdditionalTarget.size() != 1) return false;
                    if(payCost(secondAdditionalPayment)) {
                        secondAdditionalTarget.get(0).addShooterHits(shooter, 2);
                        secondAdditionalTarget.get(0).convertShooterMarks(shooter);
                        return true;
                    }
                }
                return false;
            }
        }

        private class PlasmaGun extends Weapon {
            private PlasmaGun(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(BLUE);
                basicPayment.add(YELLOW);
                secondAdditionalPayment.add(BLUE);
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
                if (payCost(secondAdditionalPayment)) {
                    basicTarget.get(0).addShooterHits(shooter, 1);
                    return true;
                }
                return false;
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
            public boolean basicFire() {
                possibleTarget.clear();
                addVisibleTarget();
                for (var player : possibleTarget) {
                    if (shooter.isPlayerNear(player, cells)) possibleTarget.remove(player);
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
                                 boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(BLUE);
                alternativePayment.add(BLUE);
                alternativePayment.add(RED);
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
                        if (payCost(alternativePayment)) {
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
                               boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(BLUE);
                alternativePayment.add(RED);
                alternativePayment.add(YELLOW);
            }

            @Override
            public boolean basicFire() {
                if(alternativeFire){
                    //TODO add target
                    //TODO verify if target can be moved to shooter's square
                    if(payCost(alternativePayment)){
                        //TODO effectively move target in shooter's square
                        basicTarget.get(0).addShooterHits(shooter, 3);
                        basicTarget.get(0).convertShooterMarks(shooter);
                        return true;
                    }
                } else {
                    addVisibleSquare();
                    //TODO add point where to move target from possibleTargetPoint to basicTargetPoint
                    //TODO verify if target can be moved
                    basicTarget.get(0).addShooterHits(shooter, 3);
                    basicTarget.get(0).convertShooterMarks(shooter);
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

        public class VortexCannon extends Weapon {
            public VortexCannon(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                                boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(RED);
                basicPayment.add(BLUE);
                firstAdditionalPayment.add(RED);
            }

            //fit in possibleTarget players (except shooter) that are near the Vortex
            private void addNearTargets(){
                for (Player p : players) {
                    if(!(shooter.equals((p)))) {
                        if (p.getPosition().equals(basicTargetPoint.get(0))) possibleTarget.add(p);
                        else {
                            for (Bounds.Direction d : Bounds.Direction.values()) {
                                if(cells[basicTargetPoint.get(0).x][basicTargetPoint.get(0).y].getBounds()
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
            public boolean basicFire() {
                possibleTargetPoint.clear();
                addVisibleSquare();
                for (Point p : possibleTargetPoint) {
                    if(p.equals(shooter.getPosition())) possibleTargetPoint.remove(p);
                }
                //TODO: choose a square from possibleTargetPoint to basicTargetPoint
                possibleTarget.clear();
                addNearTargets();
                if(possibleTarget.size() > 0){
                    basicTarget.clear();
                    //TODO: choose ONLY 1 target from possibleTarget to basicTarget
                    //TODO: move basicTarget.get(0) in basicTargetPoint.get(0)
                    basicTarget.get(0).addShooterHits(shooter, 2);
                    basicTarget.get(0).convertShooterMarks(shooter);
                    return true;
                }
                return false;
            }

            @Override
            public boolean firstAdditionalFire() {
                possibleTarget.remove(basicTarget.get(0));
                //TODO: choose 1 or 2 target from possibleTarget to firstAdditionalTarget
                if(payCost(firstAdditionalPayment)){
                    for (Player p : firstAdditionalTarget) {
                        //TODO: move into Vortex
                        p.addShooterHits(shooter, 1);
                        p.convertShooterMarks(shooter);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean secondAdditionalFire() {
                return false;
            }
        }

        public class Furnace extends Weapon {
            public Furnace(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                                boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(RED);
                basicPayment.add(BLUE);
            }

            //so need to be selected the square near the shooter, not one square in the room...
            private void addPossibleRooms(){
                possibleTargetPoint.clear();
                for (var d : Bounds.Direction.values()) {
                    if (cells[shooter.getPosition().x][shooter.getPosition().y].getBounds().getType(d)
                            == Bounds.Type.DOOR) possibleTargetPoint.add(new Point(shooter.getPosition().x + d.getdX(),
                            shooter.getPosition().y + d.getdY()));
                }
            }

            @Override
            public boolean basicFire() {
                if(alternativeFire) {
                    addPossibleRooms();
                    if (possibleTargetPoint.size() > 0) {
                        basicTargetPoint.clear();
                        basicTarget.clear();
                        //TODO: choose one square from possibleTargetPoint to basicTargetPoint
                        for (Player p : players) {
                            if (cells[p.getPosition().x][p.getPosition().y].getColor()
                                    == cells[basicTargetPoint.get(0).x][basicTargetPoint.get(0).y].getColor()) {
                                basicTarget.add(p);
                            }
                        }
                        if (basicTarget.size() > 0) {
                            for (Player p : basicTarget) {
                                p.addShooterHits(shooter, 1);
                                p.convertShooterMarks(shooter);
                            }
                            return true;
                        }
                    }
                } else {
                    firstAdditionalTargetPoint.clear();
                    for (var d : Bounds.Direction.values()) {
                        if(cells[shooter.getPosition().x][shooter.getPosition().y].getBounds().getType(d)
                                != Bounds.Type.WALL) firstAdditionalTargetPoint.add(new Point
                                (shooter.getPosition().x + d.getdX(), shooter.getPosition().y + d.getdY()));
                    }
                    possibleTargetPoint.clear();
                    for (Point point : firstAdditionalTargetPoint) {
                        for (Player player : players) {
                            if (player.getPosition().equals(point) && !possibleTargetPoint.contains(point))
                                possibleTargetPoint.add(point);
                        }
                    }
                    if(possibleTargetPoint.size() > 0){
                        //TODO: choose 1 Point from possibleTargetPoint to basicTargetPoint
                        for (var player : players) {
                            if (player.getPosition().equals(basicTargetPoint.get(0))){
                                player.addShooterHits(shooter, 1);
                                player.convertShooterMarks(shooter);
                                player.addShooterMarks(shooter, 1);
                            }
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

        public class Heatseeker extends Weapon {
            public Heatseeker(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                           boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(RED);
                basicPayment.add(RED);
                basicPayment.add(YELLOW);
            }

            @Override
            public boolean basicFire() {
                possibleTarget.clear();
                for (var player : players) {
                    if (!player.equals(shooter) && !shooter.canSee(player, cells)) possibleTarget.add(player);
                }
                if (possibleTarget.size() > 0) {
                    //TODO: choose 1 target from possibleTarget to basicTarget
                    basicTarget.get(0).addShooterHits(shooter, 3);
                    basicTarget.get(0).convertShooterMarks(shooter);
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

        public class Hellion extends Weapon {
            public Hellion(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                              boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(RED);
                basicPayment.add(YELLOW);
                alternativePayment.add(RED);
            }

            @Override
            public boolean basicFire() {
                possibleTarget.clear();
                addVisibleTarget();
                for (var player : possibleTarget) {
                    if (shooter.isPlayerNear(player, cells)) possibleTarget.remove(player);
                }
                if (possibleTarget.size() > 0) {
                    //TODO: choose 1 target from possibleTarget to basicTarget
                    basicTarget.get(0).addShooterHits(shooter, 1);
                    basicTarget.get(0).convertShooterMarks(shooter);
                    if (alternativeFire) {
                        if(payCost(alternativePayment)) {
                            for (var player : possibleTarget) {
                                if (player.getPosition().equals(basicTarget.get(0).getPosition()))
                                    player.addShooterMarks(shooter, 2);
                            }
                            return true;
                        }
                    } else {
                        for (var player : possibleTarget) {
                            if (player.getPosition().equals(basicTarget.get(0).getPosition()))
                                player.addShooterMarks(shooter, 1);
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

        public class Flamethrower extends Weapon {
            public Flamethrower(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                           boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(RED);
                alternativePayment.add(YELLOW);
                alternativePayment.add(YELLOW);
            }

            @Override
            public boolean basicFire() {
                firstAdditionalTargetPoint.clear();
                for (int x = 0; x < 4; x++) {
                    for (int y = 0; y < 3; y++) {
                        Point point = new Point(x,y);
                        if (shooter.isCellNear(point, cells)) firstAdditionalTargetPoint.add(point);
                    }
                }
                for (Point point : firstAdditionalTargetPoint) {
                    possibleTargetPoint.clear();
                    for (Player player : players) {
                        if (point.equals(player.getPosition())
                                && !possibleTargetPoint.contains(point)) possibleTargetPoint.add(point);
                    }
                }
                if (possibleTargetPoint.size() > 0) {
                    //TODO: choose 1 point from possibleTargetPoint to basicTargetPoint
                    possibleTarget.clear();
                    for (Player player : players) {
                        if (player.getPosition().equals(basicTargetPoint.get(0))) possibleTarget.add(player);
                    }
                    if (possibleTarget.size() > 0) {
                        for (int x = 0; x < 4; x++) {
                            for (int y = 0; y < 3; y++) {
                                Point point = new Point(x,y);
                                if (possibleTarget.get(0).isCellNear(point, cells)
                                        && ((shooter.getPosition().x - point.x == 0
                                        && (shooter.getPosition().y - point.y == 2
                                        || shooter.getPosition().y - point.y == -2))
                                        || (shooter.getPosition().y - point.y == 0
                                        && (shooter.getPosition().x - point.x == 2
                                        || shooter.getPosition().x - point.x == -2))))
                                    basicTargetPoint.add(1, point);
                            }
                        }
                        if (basicTargetPoint.size() > 1) {
                            for (Player player : players) {
                                if (player.getPosition().equals(basicTargetPoint.get(1))) secondAdditionalTarget.add(player);
                            }
                        }
                        if (!alternativeFire) {
                            //TODO: choose 1 target from possibleTarget to basicTarget
                            if (secondAdditionalTarget.size() > 0) {
                                //TODO: choose 1 target from secondAdditionalTarget basicTarget
                            }
                            for (Player player : basicTarget) {
                                player.addShooterHits(shooter, 1);
                                player.convertShooterMarks(shooter);
                            }
                            return true;
                        } else {
                            if (payCost(alternativePayment)) {
                                for (Player player : possibleTarget) {
                                    player.addShooterHits(shooter, 2);
                                    player.convertShooterMarks(shooter);
                                }
                                for (Player player : secondAdditionalTarget) {
                                    player.addShooterHits(shooter, 1);
                                    player.convertShooterMarks(shooter);
                                }
                                return true;
                            }
                        }
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

        public class GrenadeLauncher extends Weapon {
            public GrenadeLauncher(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                                boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(RED);
                firstAdditionalPayment.add(RED);
            }

            private boolean trueIsAfter = alternativeFire;

            @Override
            public boolean basicFire() {
                possibleTarget.clear();
                addVisibleTarget();
                if (possibleTarget.size() > 0) {
                    //TODO: choose 1 target from possibleTarget to basicTarget
                    basicTarget.get(0).addShooterHits(shooter, 1);
                    basicTarget.get(0).convertShooterMarks(shooter);
                    for (int x = 0; x < 4; x++) {
                        for (int y = 0; y < 3; y++) {
                            Point point = new Point(x,y);
                            if (basicTarget.get(0).isCellNear(point, cells)) possibleTargetPoint.add(point);
                        }
                    }
                    //TODO: choose 1 point from possibleTargetPoint to basicTargetPoint
                    if (!trueIsAfter) {
                        //TODO: move basicTarget.get(0) to basicTargetPoint.get(0)
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean firstAdditionalFire() {
                possibleTargetPoint.clear();
                addVisibleSquare();
                //TODO: choose 1 point from possibleTargetPoint to firstAdditionalTargetPoint.get(0)
                for (Player player : players) {
                    if (!player.equals(shooter) && player.getPosition().equals(firstAdditionalTargetPoint.get(0)))
                        firstAdditionalTarget.add(player);
                }
                if (!firstAdditionalTarget.isEmpty()) {
                    if (payCost(firstAdditionalPayment)) {
                        for (Player player : firstAdditionalTarget) {
                            player.addShooterHits(shooter, 1);
                            player.convertShooterMarks(shooter);
                        }
                        if (trueIsAfter) {
                            //TODO: move basicTarget.get(0) to basicTargetPoint.get(0)
                        }
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

        public class RocketLauncher extends Weapon {
            public RocketLauncher(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                                   boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(RED);
                basicPayment.add(RED);
                firstAdditionalPayment.add(BLUE);
                secondAdditionalPayment.add(YELLOW);
            }

            private boolean trueIsAfter = alternativeFire;

            //creare un metodo per gestire il flusso dei metodi fire

            @Override
            public boolean basicFire() {
                possibleTarget.clear();
                addVisibleTarget();
                for (Player player : possibleTarget) {
                    if (player.getPosition().equals(shooter.getPosition())) possibleTarget.remove(player);
                }
                if (!possibleTarget.isEmpty()) {
                    //TODO: choose 1 target from possibleTarget to basicTarget.get(0)
                    secondAdditionalTargetPoint.add(basicTarget.get(0).getPosition());
                    basicTarget.get(0).addShooterHits(shooter, 2);
                    basicTarget.get(0).convertShooterMarks(shooter);
                    //sarebbe da usare la canMove con maxStep 1
                    for (int x = 0; x < 4; x++) {
                        for (int y = 0; y < 3; y++) {
                            Point point = new Point(x,y);
                            if (basicTarget.get(0).isCellNear(point, cells)) possibleTargetPoint.add(point);
                        }
                    }
                    //TODO: choose 1 point from possibleTargetPoint to basicTargetPoint.get(0)
                    //TODO: move basicTarget.get(0) to basicTargetPoint.get(0)
                    return true;
                }
                return false;
            }

            @Override
            public boolean firstAdditionalFire() {
                //dovrei usare il canMove da 2
                //TODO: move shooter to firstAdditionalTargetPoint.get(0)
                return false;
            }

            @Override
            public boolean secondAdditionalFire() {
                if (payCost(secondAdditionalPayment)) {
                    for (Player player : players) {
                        if (player.getPosition().equals(secondAdditionalTargetPoint.get(0))
                                || player.equals(basicTarget.get(0))) {
                            player.addShooterHits(shooter, 1);
                            player.convertShooterMarks(shooter);
                        }
                    }
                        return true;
                }
                return false;
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
            public boolean basicFire() {
                possibleTarget.clear();
                for (Player player : players) {
                    if (!player.equals(shooter) && (shooter.getPosition().x == player.getPosition().x
                            || shooter.getPosition().y == player.getPosition().y)) possibleTarget.add(player);
                }
                if (!possibleTarget.isEmpty()) {
                    //TODO: choose 1 target from possibleTarget to basicTarget(0)
                    if (!alternativeFire) {
                        basicTarget.get(0).addShooterHits(shooter, 3);
                        basicTarget.get(0).convertShooterMarks(shooter);
                        return true;
                    } else {
                        possibleTarget.remove(basicTarget.get(0));
                        for (Player player : possibleTarget) {
                            if (!((shooter.getPosition().x == basicTarget.get(0).getPosition().x &&
                                    ((basicTarget.get(0).getPosition().y - shooter.getPosition().y)
                                            *(player.getPosition().y - shooter.getPosition().y) > 0)) ||
                                    (shooter.getPosition().y == basicTarget.get(0).getPosition().y &&
                                            ((basicTarget.get(0).getPosition().x - shooter.getPosition().x)
                                                    *(player.getPosition().x - shooter.getPosition().x) > 0))))
                                possibleTarget.remove(player);
                        }
                        if (!possibleTarget.isEmpty()) {
                            //TODO: choose 1 target from possibleTarget to basicTarget.get(1)
                            for (Player player : basicTarget) {
                                player.addShooterHits(shooter, 2);
                                player.convertShooterMarks(shooter);
                            }
                            return true;
                        }
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

        public class Cyberblade extends Weapon {
            public Cyberblade(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                           boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
                basicPayment.add(YELLOW);
                basicPayment.add(RED);
                secondAdditionalPayment.add(YELLOW);
            }

            private boolean trueIsAfter = alternativeFire;

            @Override
            public boolean basicFire() {
                possibleTarget.clear();
                for (Player player : players) {
                    if (!player.equals(shooter) &&
                            player.getPosition().equals(shooter.getPosition())) possibleTarget.add(player);
                }
                if (!possibleTarget.isEmpty()) {
                    //TODO: choose 1 target from possibleTarget to basicTarget.get(0)
                    basicTarget.get(0).addShooterHits(shooter, 2);
                    basicTarget.get(0).convertShooterMarks(shooter);
                    return true;
                }
                return false;
            }

            @Override
            public boolean firstAdditionalFire() {
                possibleTargetPoint.clear();
                //dovrei usare canMove con maxStep = 1
                for (int x = 0; x < 4; x++) {
                    for (int y = 0; y < 3; y++) {
                        Point point = new Point(x,y);
                        if (shooter.isCellNear(point, cells)) possibleTargetPoint.add(point);
                    }
                }
                //TODO: choose 1 point from possibleTargetPoint to firstAdditionalTargetPoint
                //TODO: move shooter to firstAdditionalTargetPoint.get(0)
                return false;
            }

            @Override
            public boolean secondAdditionalFire() {
                possibleTarget.clear();
                for (Player player : players) {
                    if (!player.equals(shooter) && !player.equals(basicTarget.get(0)) &&
                            player.getPosition().equals(shooter.getPosition())) possibleTarget.add(player);
                }
                if (!possibleTarget.isEmpty()) {
                    //TODO: choose 1 target from possibleTarget to secondAdditionalTarget.get(0)
                    firstAdditionalTarget.get(0).addShooterHits(shooter, 2);
                    firstAdditionalTarget.get(0).convertShooterMarks(shooter);
                    return true;
                }
                return false;
            }
        }

        public class ZX2 extends Weapon {
            public ZX2(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                              boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
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

        public class Shotgun extends Weapon {
            public Shotgun(@NotNull Cell[][] cells, @NotNull ArrayList<Player> players, @NotNull Player shooter,
                       boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(cells, players, shooter, alternativeFire, powerUpsPay);
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