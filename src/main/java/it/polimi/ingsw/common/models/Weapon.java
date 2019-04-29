package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.MalformedParametersException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static it.polimi.ingsw.common.models.AmmoCard.Color.*;

@SuppressWarnings("SpellCheckingInspection")
public abstract class Weapon {
    @NotNull Game game;
    boolean alternativeFire;
    @NotNull ArrayList<AmmoCard.Color> basicPayment = new ArrayList<>();
    @NotNull ArrayList<AmmoCard.Color> firstAdditionalPayment = new ArrayList<>();
    @NotNull ArrayList<AmmoCard.Color> secondAdditionalPayment = new ArrayList<>();
    @NotNull ArrayList<AmmoCard.Color> alternativePayment = new ArrayList<>();
    @NotNull ArrayList<Player> possibleTarget = new ArrayList<>();
    @NotNull ArrayList<Point> possibleTargetPoint = new ArrayList<>();
    @NotNull ArrayList<Player> basicTarget = new ArrayList<>();
    @NotNull ArrayList<Point> basicTargetPoint = new ArrayList<>();
    @NotNull ArrayList<Player> firstAdditionalTarget = new ArrayList<>();
    @NotNull ArrayList<Point> firstAdditionalTargetPoint = new ArrayList<>();
    @NotNull ArrayList<Player> secondAdditionalTarget = new ArrayList<>();
    @NotNull ArrayList<Point> secondAdditionalTargetPoint = new ArrayList<>();  //forse non serve mai
    @NotNull ArrayList<Integer> fireSort = new ArrayList<>();
    private @NotNull ArrayList<PowerUp> powerUpsPay;

    //Come passare gli array di cui sopra?

    @Contract(pure = true)
    Weapon(@NotNull Game game, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
        this.game = game;
        this.alternativeFire = alternativeFire;
        this.powerUpsPay = powerUpsPay;
    }

    @Contract(pure = true)
    private int getColoredPayment(@NotNull ArrayList<AmmoCard.Color> payment, @NotNull AmmoCard.Color color) {
        var result = 0;
        for (AmmoCard.Color cube : payment) if (cube == color) result++;
        return result;
    }

    @Contract(pure = true)
    private int getPowerUpsColoredPayment(@NotNull AmmoCard.Color color) {
        var result = 0;
        for (PowerUp powerUp : powerUpsPay) if (powerUp.getAmmoColor() == color) result++;
        return result;
    }

    //return true if game.getActualPlayer() can pay and remove cost from game.getActualPlayer() (works for fireCost, reloading and grubbing)
    private boolean payCost() {
        //red, blue e yellow avranno il valore del costo totale
        //altNomeColore hanno il valore dei cubi che vengono invece pagati tramite PowerUp
        var red = 0;
        var yellow = 0;
        var blue = 0;
        var altRed = 0;
        var altYellow = 0;
        var altBlue = 0;
        if (!fireSort.isEmpty()) { //se c'è fireSort sta facendo fuoco, "else" vuole ricaricare o prendere l'arma
            for (var i : fireSort) {
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
            if (!game.getActualPlayer().hasWeapon(this)) {
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
        if (game.getActualPlayer().getColoredCubes(RED) >= red - altRed && red - altRed >= 0
                && game.getActualPlayer().getColoredCubes(YELLOW) >= yellow - altYellow && yellow - altYellow >= 0
                && game.getActualPlayer().getColoredCubes(BLUE) >= blue - altBlue && blue - altBlue >= 0) {
            for (PowerUp p : powerUpsPay) {
                switch (p.getAmmoColor()) {
                    case RED:
                        if (altRed > 0) {
                            game.getActualPlayer().removePowerUp(p);
                            altRed--;
                            red--;
                        }
                        break;
                    case YELLOW:
                        if (altYellow > 0) {
                            game.getActualPlayer().removePowerUp(p);
                            altYellow--;
                            yellow--;
                        }
                        break;
                    case BLUE:
                        if (altBlue > 0) {
                            game.getActualPlayer().removePowerUp(p);
                            altBlue--;
                            blue--;
                        }
                        break;
                }
            }
            game.getActualPlayer().removeColoredCubes(RED, red);
            game.getActualPlayer().removeColoredCubes(YELLOW, yellow);
            game.getActualPlayer().removeColoredCubes(BLUE, blue);
            return true;
        }
        return false;
    }

    boolean chargingOrGrabbing() {
        fireSort.clear();
        return payCost();
    }

    abstract boolean canFire();

    abstract boolean validateTargets();

    abstract boolean validateFireSort();

    void addVisibleTarget() {
        possibleTarget.clear();
        possibleTarget.addAll(game.getPlayers().parallelStream().filter(e -> game.getActualPlayer().canSee(e, game.getCells())).collect(Collectors.toList()));
        //for (var p : game.getPlayers()) if (game.getActualPlayer().canSee(p, game.getCells())) possibleTarget.add(p);
    }

    void addNonVisibleTarget() {
        possibleTarget.clear();
        for (Player player : game.getPlayers()) {
            if (!(game.getActualPlayer().canSee(player, game.getCells())) && !(game.getActualPlayer().equals(player)))
                possibleTarget.add(player);
        }
    }

    void addVisibleSquare() {
        int x, y;
        Point point;
        possibleTargetPoint.clear();
        for (x = 0; x < 4; x++) {
            for (y = 0; y < 3; y++) {
                point = new Point(x, y);
                if (game.getActualPlayer().canSeeCell(point, game.getCells())) possibleTargetPoint.add(point);
            }
        }
    } //forse mai usato

    public void addBasicTarget(@Nullable Player target, @Nullable Point point) {
        basicTarget.add(target);
        basicTargetPoint.add(point);
    }

    public void basicFire() {
    }

    public void addFirstAdditionalTarget(@Nullable Player target, @Nullable Point point) {
        firstAdditionalTarget.add(target);
        firstAdditionalTargetPoint.add(point);
    }

    public void firstAdditionalFire() {
    }

    public void addSecondAdditionalTarget(@Nullable Player target, @Nullable Point point) {
        secondAdditionalTarget.add(target);
        secondAdditionalTargetPoint.add(point);
    }

    public void secondAdditionalFire() {
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Weapon && ((Weapon) obj).getClass().equals(getClass());
    }

    //shoot è di Weapon astratta, ma tutti i metodi chiamati all'interno devono essere specifici dell'arma usata
    public boolean shoot() {
        if (game.getActualPlayer().hasWeapon(this) && game.getActualPlayer().isALoadedGun(this) && canFire() &&
                validateFireSort() && validateTargets() && payCost()) {
            for (var fireNumber : fireSort)
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
            return true;
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
        public @NotNull <T extends Weapon> T build(@NotNull Game game, boolean alternativeFire) throws MalformedParametersException {
            try {
                //noinspection unchecked
                return (T) getWeaponClass().getDeclaredConstructors()[0].newInstance(game, alternativeFire, new ArrayList<>());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                throw new MalformedParametersException();
            }
        }
    }

    private static class Weapons {
        private static class LockRifle extends Weapon {
            private LockRifle(@NotNull Game game, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(game, alternativeFire, powerUpsPay);
                basicPayment.add(BLUE);
                basicPayment.add(BLUE);
                firstAdditionalPayment.add(RED);
            }

            //per ora dice se può essere usata, al di fuori del "è carica?"
            @Override
            protected boolean canFire() {
                addVisibleTarget();
                return !possibleTarget.isEmpty();
            }

            @Override
            protected boolean validateFireSort() {
                return fireSort.get(0) == 1 && (fireSort.size() == 1 || (fireSort.size() == 2 && fireSort.get(1) == 2));
            }

            @Override
            protected boolean validateTargets() {
                for (Integer mode : fireSort) {
                    switch (mode) {
                        case (1):
                            if (!game.getActualPlayer().canSee(basicTarget.get(0), game.getCells())) return false;
                            break;
                        case (2):
                            if (!game.getActualPlayer().canSee(firstAdditionalTarget.get(0), game.getCells()) ||
                                    firstAdditionalTarget.get(0).equals(basicTarget.get(0))) return false;
                            break;
                        default:
                            return false;
                    }
                }
                return true;
            }

            @Override
            public void basicFire() {
                basicTarget.get(0).takeHits(game.getActualPlayer(), 2, 1);
            }

            @Override
            public void firstAdditionalFire() {
                firstAdditionalTarget.get(0).takeHits(game.getActualPlayer(), 0, 1);
            }
        }

        private static class MachineGun extends Weapon {
            private MachineGun(@NotNull Game game, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(game, alternativeFire, powerUpsPay);
                basicPayment.add(BLUE);
                basicPayment.add(RED);
                firstAdditionalPayment.add(YELLOW);
                secondAdditionalPayment.add(BLUE);
            }


            @Override
            protected boolean canFire() {
                addVisibleTarget();
                return !possibleTarget.isEmpty();
            }

            @Override
            protected boolean validateFireSort() {
                if (fireSort.size() == 1 && fireSort.get(0) == 1) return true;
                if (fireSort.size() == 2 && fireSort.get(0) == 1 && (fireSort.get(1) == 2 || fireSort.get(1) == 3))
                    return true;
                return (fireSort.size() == 3 && fireSort.get(0) == 1 && fireSort.get(1) == 2 && fireSort.get(2) == 3);
            }

            @Override
            protected boolean validateTargets() {
                for (var mode : fireSort) {
                    switch (mode) {
                        case 1:
                            if (basicTarget.size() > 2 || basicTarget.isEmpty()) return false;
                            if (basicTarget.size() == 2 && basicTarget.get(0).equals(basicTarget.get(1))) return false;
                            for (var player : basicTarget)
                                if (!game.getActualPlayer().canSee(player, game.getCells())) return false;
                            break;
                        case 2:
                            if (firstAdditionalTarget.size() != 1) return false;
                            if (!basicTarget.contains(firstAdditionalTarget.get(0))) return false;
                            break;
                        case 3:
                            if (secondAdditionalTarget.size() > 2 || secondAdditionalTarget.isEmpty()) return false;
                            for (var player : secondAdditionalTarget)
                                if (!game.getActualPlayer().canSee(player, game.getCells()) ||
                                        firstAdditionalTarget.contains(player)) return false;
                            if (secondAdditionalTarget.size() == 2) {
                                if (secondAdditionalTarget.get(0).equals(secondAdditionalTarget.get(1))) return false;
                                if ((basicTarget.contains(secondAdditionalTarget.get(0)) && basicTarget.contains(secondAdditionalTarget.get(1))) ||
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
                for (var player : basicTarget) player.takeHits(game.getActualPlayer(), 1, 0);
            }

            @Override
            public void firstAdditionalFire() {
                firstAdditionalTarget.get(0).takeHits(game.getActualPlayer(), 1, 0);
            }

            @Override
            public void secondAdditionalFire() {
                for (var player : secondAdditionalTarget) player.takeHits(game.getActualPlayer(), 1, 0);
            }
        }

        private static class Thor extends Weapon {
            private Thor(@NotNull Game game, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(game, alternativeFire, powerUpsPay);
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
                return (fireSort.size() == 3 && fireSort.get(0) == 1 && fireSort.get(1) == 2 && fireSort.get(2) == 3);
            }

            @Override
            protected boolean validateTargets() {
                if (basicTarget.size() != 1) return false;
                if (fireSort.size() > 1 && (firstAdditionalTarget.size() != 1 ||
                        firstAdditionalTarget.get(0).equals(basicTarget.get(0)) ||
                        !basicTarget.get(0).canSee(firstAdditionalTarget.get(0), game.getCells()))) return false;
                return !(fireSort.size() > 2 && (secondAdditionalTarget.size() != 1 ||
                        secondAdditionalTarget.get(0).equals(firstAdditionalTarget.get(0)) ||
                        secondAdditionalTarget.get(0).equals(basicTarget.get(0)) ||
                        !firstAdditionalTarget.get(0).canSee(secondAdditionalTarget.get(0), game.getCells())));
            }

            @Override
            public void basicFire() {
                basicTarget.get(0).takeHits(game.getActualPlayer(), 2, 0);
            }

            @Override
            public void firstAdditionalFire() {
                firstAdditionalTarget.get(0).takeHits(game.getActualPlayer(), 1, 0);
            }

            @Override
            public void secondAdditionalFire() {
                secondAdditionalTarget.get(0).takeHits(game.getActualPlayer(), 2, 0);
            }
        }

        private static class PlasmaGun extends Weapon {
            private PlasmaGun(@NotNull Game game, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(game, alternativeFire, powerUpsPay);
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
                    if (!game.canMove(game.getActualPlayer().getPosition(), firstAdditionalTargetPoint.get(0), 0, 2))
                        return false;
                }
                if (fireSort.get(0) == 1 && !game.getActualPlayer().canSee(basicTarget.get(0), game.getCells()))
                    return false;
                if (fireSort.contains(2) && fireSort.indexOf(2) < fireSort.indexOf(1)) {
                    if (!basicTarget.get(0).canBeSeenFrom(firstAdditionalTargetPoint.get(0), game.getCells()))
                        return false;
                }
                return !(fireSort.get(0) == 2);
            }

            @Override
            public void basicFire() {
                basicTarget.get(0).takeHits(game.getActualPlayer(), 2, 0);
            }

            @Override
            public void firstAdditionalFire() {
                game.getActualPlayer().setPosition(firstAdditionalTargetPoint.get(0));
            }

            @Override
            public void secondAdditionalFire() {
                basicTarget.get(0).takeHits(game.getActualPlayer(), 1, 0);
            }
        }

        private static class Whisper extends Weapon {
            public Whisper(@NotNull Game game, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(game, alternativeFire, powerUpsPay);
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
                return game.getActualPlayer().canSee(basicTarget.get(0), game.getCells()) && !game.getActualPlayer().isPlayerNear(basicTarget.get(0), game.getCells());
            }

            @Override
            public void basicFire() {
                basicTarget.get(0).takeHits(game.getActualPlayer(), 3, 1);
            }
        }

        private static class Electroscythe extends Weapon {
            public Electroscythe(@NotNull Game game, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(game, alternativeFire, powerUpsPay);
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
                for (Player player : game.getPlayers()) {
                    if (game.getActualPlayer().getPosition().equals(player.getPosition()) &&
                            !game.getActualPlayer().equals(player)) basicTarget.add(player);
                }
                return !basicTarget.isEmpty();
            }

            @Override
            public void basicFire() {
                if (!alternativeFire) {
                    for (Player player : basicTarget) {
                        player.takeHits(game.getActualPlayer(), 1, 0);
                    }
                } else {
                    for (Player player : basicTarget) {
                        player.takeHits(game.getActualPlayer(), 2, 0);
                    }
                }
            }
        }

        private static class TractorBeam extends Weapon {
            public TractorBeam(@NotNull Game game, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(game, alternativeFire, powerUpsPay);
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
                    basicTargetPoint.add(game.getActualPlayer().getPosition());
                }
                if (basicTarget.size() != 1) return false;
                if (basicTarget.get(0).equals(game.getActualPlayer())) return false;
                if (basicTargetPoint.size() != 1) return false;
                return game.canMove(basicTarget.get(0).getPosition(), basicTargetPoint.get(0), 0, 3);
            }

            @Override
            public void basicFire() {
                basicTarget.get(0).setPosition(basicTargetPoint.get(0));
                if (!alternativeFire) {
                    basicTarget.get(0).takeHits(game.getActualPlayer(), 1, 0);
                } else {
                    basicTarget.get(0).takeHits(game.getActualPlayer(), 3, 0);
                }
            }
        }

        private static class VortexCannon extends Weapon {
            public VortexCannon(@NotNull Game game, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(game, alternativeFire, powerUpsPay);
                basicPayment.add(RED);
                basicPayment.add(BLUE);
                firstAdditionalPayment.add(RED);
            }

            //fit in possibleTarget game.getPlayers() (except game.getActualPlayer()) that are near the Vortex
            private void addNearTargets() {
                possibleTarget.clear();
                for (Player p : game.getPlayers()) {
                    if (!(game.getActualPlayer().equals((p)))) {
                        if (p.getPosition().equals(basicTargetPoint.get(0))) possibleTarget.add(p);
                        else {
                            for (Bounds.Direction d : Bounds.Direction.values()) {
                                if (game.getCells()[basicTargetPoint.get(0).x][basicTargetPoint.get(0).y].getBounds()
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
                return game.getActualPlayer().canSeeCell(basicTargetPoint.get(0), game.getCells()) && !possibleTarget.isEmpty();
            }

            @Override
            protected boolean validateFireSort() {
                if (fireSort.isEmpty() || fireSort.size() > 2) return false;
                if (fireSort.get(0) != 1) return false;
                return !(fireSort.size() == 2 && fireSort.get(1) != 2);
            }

            @Override
            protected boolean validateTargets() {
                if (basicTargetPoint.size() != 1 || !game.getActualPlayer().canSeeCell(basicTargetPoint.get(0), game.getCells()) ||
                        basicTarget.size() != 1 ||
                        !basicTarget.get(0).isCellNear(basicTargetPoint.get(0), game.getCells())) return false;
                if (fireSort.size() == 2) {
                    if (firstAdditionalTarget.isEmpty() || firstAdditionalTarget.size() > 2) return false;
                    if (firstAdditionalTarget.size() == 2 &&
                            firstAdditionalTarget.get(0).equals(firstAdditionalTarget.get(1))) return false;
                    for (Player player : firstAdditionalTarget) {
                        if (player.equals(basicTarget.get(0)) ||
                                !player.isCellNear(basicTargetPoint.get(0), game.getCells())) return false;
                    }
                }
                return true;
            }

            @Override
            public void basicFire() {
                basicTarget.get(0).setPosition(basicTargetPoint.get(0));
                basicTarget.get(0).takeHits(game.getActualPlayer(), 2, 0);
            }

            @Override
            public void firstAdditionalFire() {
                for (Player player : firstAdditionalTarget) {
                    player.setPosition(basicTargetPoint.get(0));
                    player.takeHits(game.getActualPlayer(), 1, 0);
                }
            }
        }

        private static class Furnace extends Weapon {
            public Furnace(@NotNull Game game, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(game, alternativeFire, powerUpsPay);
                basicPayment.add(RED);
                basicPayment.add(BLUE);
            }

            //so need to be selected the square near the game.getActualPlayer(), not one square in the room...
            private void addPossibleRooms() {
                possibleTargetPoint.clear();
                for (var d : Bounds.Direction.values()) {
                    if (game.getCells()[game.getActualPlayer().getPosition().x][game.getActualPlayer().getPosition().y].getBounds().getType(d)
                            == Bounds.Type.DOOR)
                        possibleTargetPoint.add(new Point(game.getActualPlayer().getPosition().x + d.getdX(),
                                game.getActualPlayer().getPosition().y + d.getdY()));
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
                if (basicTargetPoint.size() != 1 || basicTargetPoint.get(0).equals(game.getActualPlayer().getPosition()))
                    return false;
                if (!alternativeFire) {
                    addPossibleRooms();
                    if (!possibleTargetPoint.contains(basicTargetPoint.get(0))) return false;
                    basicTarget.clear();
                    for (Player player : game.getPlayers()) {
                        if (game.getCells()[player.getPosition().x][player.getPosition().y].getColor() ==
                                game.getCells()[basicTargetPoint.get(0).x][basicTargetPoint.get(0).y].getColor())
                            basicTarget.add(player);
                    }
                } else {
                    if (!game.getActualPlayer().isCellNear(basicTargetPoint.get(0), game.getCells())) return false;
                    basicTarget.clear();
                    for (Player player : game.getPlayers()) {
                        if (player.getPosition().equals(basicTargetPoint.get(0))) basicTarget.add(player);
                    }
                }
                return !basicTarget.isEmpty();
            }

            @Override
            public void basicFire() {
                if (!alternativeFire) {
                    for (Player player : basicTarget) {
                        player.takeHits(game.getActualPlayer(), 1, 0);
                    }
                } else {
                    for (Player player : basicTarget) {
                        player.takeHits(game.getActualPlayer(), 1, 1);
                    }
                }
            }
        }

        private static class Heatseeker extends Weapon {
            public Heatseeker(@NotNull Game game, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(game, alternativeFire, powerUpsPay);
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
                basicTarget.get(0).takeHits(game.getActualPlayer(), 3, 0);
            }
        }

        private static class Hellion extends Weapon {
            public Hellion(@NotNull Game game, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(game, alternativeFire, powerUpsPay);
                basicPayment.add(RED);
                basicPayment.add(YELLOW);
                alternativePayment.add(RED);
            }

            @Override
            protected boolean canFire() {
                possibleTarget.clear();
                for (Player player : game.getPlayers()) {
                    if (game.getActualPlayer().canSee(player, game.getCells()) && !game.getActualPlayer().isPlayerNear(player, game.getCells()))
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
                return (game.getActualPlayer().canSee(basicTarget.get(0), game.getCells()) && !game.getActualPlayer().isPlayerNear(basicTarget.get(0), game.getCells()));
            }

            @Override
            public void basicFire() {
                basicTarget.get(0).takeHits(game.getActualPlayer(), 1, 0);
                if (!alternativeFire) {
                    for (Player player : game.getPlayers()) {
                        if (player.getPosition().equals(basicTarget.get(0).getPosition()))
                            player.takeHits(game.getActualPlayer(), 0, 1);
                    }
                } else {
                    for (Player player : game.getPlayers()) {
                        if (player.getPosition().equals(basicTarget.get(0).getPosition()))
                            player.takeHits(game.getActualPlayer(), 0, 2);
                    }
                }
            }
        }

        private static class Flamethrower extends Weapon {
            public Flamethrower(@NotNull Game game, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(game, alternativeFire, powerUpsPay);
                basicPayment.add(RED);
                alternativePayment.add(YELLOW);
                alternativePayment.add(YELLOW);
            }

            @Override
            protected boolean canFire() {
                possibleTarget.clear();
                for (Player player : game.getPlayers()) {
                    if (game.getActualPlayer().isPlayerNear(player, game.getCells()) || game.getActualPlayer().isPlayerNear2(player, game.getCells()))
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
                    if (basicTarget.size() == 1 && (game.getActualPlayer().isPlayerNear(basicTarget.get(0), game.getCells()) ||
                            game.getActualPlayer().isPlayerNear2(basicTarget.get(0), game.getCells()))) return true;
                    return basicTarget.size() == 2 && ((game.getActualPlayer().isPlayerNear(basicTarget.get(0), game.getCells()) &&
                            game.getActualPlayer().isPlayerNear2(basicTarget.get(1), game.getCells())) ||
                            (game.getActualPlayer().isPlayerNear(basicTarget.get(1), game.getCells()) &&
                                    game.getActualPlayer().isPlayerNear2(basicTarget.get(0), game.getCells())));
                } else {
                    if (basicTargetPoint.size() != 2) return false;
                    if (!(game.getActualPlayer().isCellNear(basicTargetPoint.get(0), game.getCells()) &&
                            game.getActualPlayer().isCellNear2Straight(basicTargetPoint.get(1), game.getCells())))
                        return false;
                    basicTarget.clear();
                    firstAdditionalTarget.clear();
                    for (Player player : game.getPlayers()) {
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
                        player.takeHits(game.getActualPlayer(), 1, 0);
                    }
                } else {
                    for (Player player : basicTarget) {
                        player.takeHits(game.getActualPlayer(), 2, 0);
                    }
                    for (Player player : firstAdditionalTarget) {
                        player.takeHits(game.getActualPlayer(), 1, 0);
                    }
                }
            }
        }

        private static class GrenadeLauncher extends Weapon {
            public GrenadeLauncher(@NotNull Game game, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(game, alternativeFire, powerUpsPay);
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
                if (!game.getActualPlayer().canSee(basicTarget.get(0), game.getCells())) return false;
                if (fireSort.size() == 2 && (firstAdditionalTargetPoint.size() != 1 ||
                        !game.getActualPlayer().canSeeCell(firstAdditionalTargetPoint.get(0), game.getCells())))
                    return false;
                firstAdditionalTarget.clear();
                if (fireSort.size() == 2) {
                    for (Player player : game.getPlayers()) {
                        if (player.getPosition().equals(firstAdditionalTargetPoint.get(0)) &&
                                !player.equals(game.getActualPlayer())) firstAdditionalTarget.add(player);
                    }
                }
                if (!basicTargetPoint.isEmpty()) {
                    if (!basicTarget.get(0).isCellNear(basicTargetPoint.get(0), game.getCells())) return false;
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
                basicTarget.get(0).takeHits(game.getActualPlayer(), 1, 0);
                if (!basicTargetPoint.isEmpty()) basicTarget.get(0).setPosition(basicTargetPoint.get(0));
            }

            @Override
            public void firstAdditionalFire() {
                for (Player player : firstAdditionalTarget) {
                    player.takeHits(game.getActualPlayer(), 1, 0);
                }
            }
        }

        private static class RocketLauncher extends Weapon {
            public RocketLauncher(@NotNull Game game, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(game, alternativeFire, powerUpsPay);
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
                    if (!game.canMove(game.getActualPlayer().getPosition(), firstAdditionalTargetPoint.get(0), 0, 2))
                        if (fireSort.indexOf(2) < fireSort.indexOf(1)) {
                            if (!(basicTarget.get(0).canBeSeenFrom(firstAdditionalTargetPoint.get(0), game.getCells())))
                                return false;
                        } else {
                            if (!game.getActualPlayer().canSee(basicTarget.get(0), game.getCells())) return false;
                        }
                } else {
                    if (!game.getActualPlayer().canSee(basicTarget.get(0), game.getCells())) return false;
                }
                if (!basicTargetPoint.isEmpty()) {
                    if (basicTargetPoint.size() != 1 ||
                            !basicTarget.get(0).isCellNear(basicTargetPoint.get(0), game.getCells())) return false;
                }
                for (Player player : game.getPlayers()) {
                    if (player.getPosition().equals(basicTarget.get(0).getPosition()))
                        secondAdditionalTarget.add(player);
                }
                return true;
            }

            @Override
            public void basicFire() {
                basicTarget.get(0).takeHits(game.getActualPlayer(), 2, 0);
                if (!basicTargetPoint.isEmpty()) basicTarget.get(0).setPosition(basicTargetPoint.get(0));
            }

            @Override
            public void firstAdditionalFire() {
                game.getActualPlayer().setPosition(firstAdditionalTargetPoint.get(0));
            }

            @Override
            public void secondAdditionalFire() {
                for (Player player : secondAdditionalTarget) {
                    player.takeHits(game.getActualPlayer(), 1, 0);
                }
            }
        }

        private static class Railgun extends Weapon {
            public Railgun(@NotNull Game game, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(game, alternativeFire, powerUpsPay);
                basicPayment.add(YELLOW);
                basicPayment.add(YELLOW);
                basicPayment.add(BLUE);
            }

            @Override
            protected boolean canFire() {
                possibleTarget.clear();
                for (Player player : game.getPlayers()) {
                    if (!player.equals(game.getActualPlayer()) && (player.getPosition().x == game.getActualPlayer().getPosition().x ||
                            player.getPosition().y == game.getActualPlayer().getPosition().y))
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
                if (basicTarget.isEmpty() || basicTarget.size() > 2) return false;
                if (basicTarget.size() == 1 && !alternativeFire) {
                    return !basicTarget.get(0).equals(game.getActualPlayer()) &&
                            (basicTarget.get(0).getPosition().x == game.getActualPlayer().getPosition().x ||
                                    basicTarget.get(0).getPosition().y == game.getActualPlayer().getPosition().y);
                }
                if (basicTarget.size() == 2 && alternativeFire && !basicTarget.get(0).equals(basicTarget.get(1))) {
                    for (Player player : basicTarget) {
                        if (player.equals(game.getActualPlayer())) return false;
                    }
                    if (basicTarget.get(0).getPosition().x == basicTarget.get(1).getPosition().x) {
                        return (basicTarget.get(0).getPosition().y - game.getActualPlayer().getPosition().y) *
                                (basicTarget.get(1).getPosition().y - game.getActualPlayer().getPosition().y) >= 0;
                    } else if (basicTarget.get(0).getPosition().y == basicTarget.get(1).getPosition().y) {
                        return (basicTarget.get(0).getPosition().x - game.getActualPlayer().getPosition().x) *
                                (basicTarget.get(1).getPosition().x - game.getActualPlayer().getPosition().x) >= 0;
                    }
                }
                return false;
            }

            @Override
            public void basicFire() {
                if (!alternativeFire) {
                    basicTarget.get(0).takeHits(game.getActualPlayer(), 3, 0);
                } else {
                    for (Player player : basicTarget) {
                        player.takeHits(game.getActualPlayer(), 2, 0);
                    }
                }
            }
        }

        private static class Cyberblade extends Weapon {
            public Cyberblade(@NotNull Game game, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(game, alternativeFire, powerUpsPay);
                basicPayment.add(YELLOW);
                basicPayment.add(RED);
                secondAdditionalPayment.add(YELLOW);
            }

            @Override
            protected boolean canFire() {
                possibleTarget.clear();
                for (Player player : game.getPlayers()) {
                    if (!player.equals(game.getActualPlayer()) && (game.getActualPlayer().getPosition().equals(player.getPosition()) ||
                            game.getActualPlayer().isPlayerNear(player, game.getCells()))) possibleTarget.add(player);
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
                if (fireSort.size() == 1)
                    return game.getActualPlayer().getPosition().equals(basicTarget.get(0).getPosition());
                if (fireSort.contains(2)) {
                    if (firstAdditionalTargetPoint.size() != 1 ||
                            !game.getActualPlayer().isCellNear(firstAdditionalTargetPoint.get(0), game.getCells()))
                        return false;
                    if (fireSort.indexOf(2) < fireSort.indexOf(1)) {
                        if (!basicTarget.get(0).getPosition().equals(firstAdditionalTargetPoint.get(0))) return false;
                    } else {
                        if (!game.getActualPlayer().getPosition().equals(basicTarget.get(0).getPosition()))
                            return false;
                    }
                }
                if (fireSort.contains(3) && secondAdditionalTarget.size() != 1) return false;
                if (fireSort.contains(3) && !fireSort.contains(2) &&
                        !game.getActualPlayer().getPosition().equals(secondAdditionalTarget.get(0).getPosition()))
                    return false;
                if (fireSort.contains(2) && fireSort.contains(3)) {
                    if (fireSort.indexOf(2) < fireSort.indexOf(3)) {
                        return secondAdditionalTarget.get(0).getPosition().equals(firstAdditionalTargetPoint.get(0));
                    } else {
                        return game.getActualPlayer().getPosition().equals(secondAdditionalTarget.get(0).getPosition());
                    }
                }
                return true;
            }

            @Override
            public void basicFire() {
                basicTarget.get(0).takeHits(game.getActualPlayer(), 2, 0);
            }

            @Override
            public void firstAdditionalFire() {
                game.getActualPlayer().setPosition(firstAdditionalTargetPoint.get(0));
            }

            @Override
            public void secondAdditionalFire() {
                secondAdditionalTarget.get(0).takeHits(game.getActualPlayer(), 2, 0);
            }
        }

        private static class ZX2 extends Weapon {
            public ZX2(@NotNull Game game, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(game, alternativeFire, powerUpsPay);
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
                        (basicTarget.size() != 1 || !game.getActualPlayer().canSee(basicTarget.get(0), game.getCells())))
                    return false;
                if (alternativeFire) {
                    if (basicTarget.size() > 3) return false;
                    for (Player player : basicTarget) {
                        if (!game.getActualPlayer().canSee(player, game.getCells())) return false;
                    }
                }
                return true;
            }

            @Override
            public void basicFire() {
                if (!alternativeFire) {
                    basicTarget.get(0).takeHits(game.getActualPlayer(), 1, 2);
                } else {
                    for (Player player : basicTarget) {
                        player.takeHits(game.getActualPlayer(), 0, 1);
                    }
                }
            }
        }

        private static class Shotgun extends Weapon {
            public Shotgun(@NotNull Game game, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(game, alternativeFire, powerUpsPay);
                basicPayment.add(YELLOW);
                basicPayment.add(YELLOW);
            }

            @Override
            protected boolean canFire() {
                for (Player player : game.getPlayers()) {
                    if (!game.getActualPlayer().equals(player) && (game.getActualPlayer().getPosition().equals(player.getPosition()) ||
                            game.getActualPlayer().isPlayerNear(player, game.getCells()))) possibleTarget.add(player);
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
                    if (!game.getActualPlayer().getPosition().equals(basicTarget.get(0).getPosition())) return false;
                    if (!basicTargetPoint.isEmpty()) {
                        return (basicTargetPoint.size() == 1 && game.getActualPlayer().isCellNear(basicTargetPoint.get(0), game.getCells()));
                    }
                } else {
                    return game.getActualPlayer().isPlayerNear(basicTarget.get(0), game.getCells());
                }
                return true;
            }

            @Override
            public void basicFire() {
                if (!alternativeFire) {
                    basicTarget.get(0).takeHits(game.getActualPlayer(), 3, 0);
                    if (!basicTargetPoint.isEmpty()) {
                        basicTarget.get(0).setPosition(basicTargetPoint.get(0));
                    }
                } else {
                    basicTarget.get(0).takeHits(game.getActualPlayer(), 2, 0);
                }
            }
        }

        private static class PowerGlove extends Weapon {
            public PowerGlove(@NotNull Game game, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(game, alternativeFire, powerUpsPay);
                basicPayment.add(YELLOW);
                basicPayment.add(BLUE);
                alternativePayment.add(BLUE);
            }

            @Override
            protected boolean canFire() {
                possibleTarget.clear();
                for (Player player : game.getPlayers()) {
                    if (game.getActualPlayer().isPlayerNear(player, game.getCells()) || game.getActualPlayer().isPlayerNear2(player, game.getCells()))
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
                    return (basicTarget.size() == 1 && game.getActualPlayer().isPlayerNear(basicTarget.get(0), game.getCells()));
                } else {
                    if (basicTargetPoint.isEmpty() || basicTargetPoint.size() > 2 ||
                            basicTarget.size() > 2) return false;
                    if (basicTargetPoint.size() == 1) {
                        if (!(game.getActualPlayer().isCellNear(basicTargetPoint.get(0), game.getCells()) ||
                                game.getActualPlayer().isCellNear2Straight(basicTargetPoint.get(0), game.getCells())))
                            return false;
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
                            if (game.getCells()[game.getActualPlayer().getPosition().x][game.getActualPlayer().getPosition().y].getBounds().getType(d) !=
                                    Bounds.Type.WALL &&
                                    game.getActualPlayer().getPosition().x + d.getdX() == basicTargetPoint.get(0).x &&
                                    game.getActualPlayer().getPosition().y + d.getdY() == basicTargetPoint.get(0).y) {
                                if (game.getCells()[game.getActualPlayer().getPosition().x + d.getdX()][game.getActualPlayer().getPosition().y + d.getdY()]
                                        .getBounds().getType(d) != Bounds.Type.WALL &&
                                        game.getActualPlayer().getPosition().x + 2 * d.getdX() == basicTargetPoint.get(1).x &&
                                        game.getActualPlayer().getPosition().y + 2 * d.getdY() == basicTargetPoint.get(1).y)
                                    return true;
                            }
                        }
                        return false;
                    }
                }
            }

            @Override
            public void basicFire() {
                if (!alternativeFire) {
                    game.getActualPlayer().setPosition(basicTarget.get(0).getPosition());
                    basicTarget.get(0).takeHits(game.getActualPlayer(), 1, 2);
                } else {
                    for (int i = 0; i < basicTargetPoint.size(); i++) {
                        game.getActualPlayer().setPosition(basicTargetPoint.get(i));
                        if (basicTarget.size() > i) {
                            basicTarget.get(i).takeHits(game.getActualPlayer(), 2, 0);
                        }
                    }
                }
            }
        }

        private static class Shockwave extends Weapon {
            public Shockwave(@NotNull Game game, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(game, alternativeFire, powerUpsPay);
                basicPayment.add(YELLOW);
                alternativePayment.add(YELLOW);
            }

            @Override
            protected boolean canFire() {
                possibleTarget.clear();
                for (Player player : game.getPlayers()) {
                    if (game.getActualPlayer().isPlayerNear(player, game.getCells())) possibleTarget.add(player);
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
                        if (!game.getActualPlayer().isPlayerNear(player, game.getCells())) return false;
                    }
                    for (int i = 0; i < basicTarget.size() - 1; i++) {
                        for (Player player : basicTarget.subList(i + 1, basicTarget.size() - 1)) {
                            if (basicTarget.get(i).getPosition().equals(player.getPosition())) return false;
                        }
                    }
                } else {
                    basicTarget.clear();
                    for (Player player : game.getPlayers()) {
                        if (game.getActualPlayer().isPlayerNear(player, game.getCells())) basicTarget.add(player);
                    }
                    return !basicTarget.isEmpty();
                }
                return true;
            }

            @Override
            public void basicFire() {
                for (Player player : basicTarget) {
                    player.takeHits(game.getActualPlayer(), 1, 0);
                }
            }
        }

        private static class Sledgehammer extends Weapon {
            public Sledgehammer(@NotNull Game game, boolean alternativeFire, @NotNull ArrayList<PowerUp> powerUpsPay) {
                super(game, alternativeFire, powerUpsPay);
                basicPayment.add(YELLOW);
                alternativePayment.add(RED);
            }

            @Override
            protected boolean canFire() {
                possibleTarget.clear();
                for (Player player : game.getPlayers()) {
                    if (game.getActualPlayer().getPosition().equals(player.getPosition())) possibleTarget.add(player);
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
                if (!basicTarget.get(0).getPosition().equals(game.getActualPlayer().getPosition())) return false;
                if (!alternativeFire) {
                    if (!basicTargetPoint.isEmpty()) {
                        if (basicTargetPoint.size() != 1) return false;
                        return game.getActualPlayer().isCellNear(basicTargetPoint.get(0), game.getCells()) ||
                                game.getActualPlayer().isCellNear2Straight(basicTargetPoint.get(0), game.getCells());
                    }
                }
                return true;
            }

            @Override
            public void basicFire() {
                if (!alternativeFire) {
                    basicTarget.get(0).takeHits(game.getActualPlayer(), 2, 0);
                } else {
                    basicTarget.get(0).takeHits(game.getActualPlayer(), 3, 0);
                    if (!basicTargetPoint.isEmpty()) {
                        basicTarget.get(0).setPosition(basicTargetPoint.get(0));
                    }
                }
            }
        }
    }
}