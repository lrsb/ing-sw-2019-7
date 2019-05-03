package it.polimi.ingsw.common.models;

import it.polimi.ingsw.client.others.Utils;
import it.polimi.ingsw.client.views.sprite.Displayable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.MalformedParametersException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static it.polimi.ingsw.common.models.AmmoCard.Color.*;

@SuppressWarnings({"SpellCheckingInspection"})
public abstract class Weapon {
    @NotNull Game game;
    boolean alternativeFire;

    @NotNull ArrayList<Player> basicTargets = new ArrayList<>();
    @Nullable Point basicTargetsPoint;
    @NotNull ArrayList<AmmoCard.Color> basicAlternativeCost = new ArrayList<>();

    @NotNull ArrayList<Player> firstAdditionalTargets = new ArrayList<>();
    @Nullable Point firstAdditionalTargetsPoint;
    @Nullable AmmoCard.Color firstAdditionalCost;

    @NotNull ArrayList<Player> secondAdditionalTargets = new ArrayList<>();
    @Nullable AmmoCard.Color secondAdditionalCost;

    private @NotNull ArrayList<PowerUp> basicAlternativePayment = new ArrayList<>();
    private @NotNull ArrayList<PowerUp> firstAdditionalPayment = new ArrayList<>();
    private @NotNull ArrayList<PowerUp> secondAdditionalPayment = new ArrayList<>();

    @Contract(pure = true)
    Weapon(@NotNull Game game, boolean alternativeFire) {
        this.game = game;
        this.alternativeFire = alternativeFire;
    }

    public final boolean basicFire() {
        try {
            int[] cost = new int[values().length];
            Stream.of(values()).forEach(e -> cost[e.getIndex()] = (int) basicAlternativeCost.stream().filter(f -> e == f).count());
            if (!(game.getActualPlayer().isALoadedGun(Weapon.Name.getName(getClass())) && canBasicFire()) ||
                    !canFire(cost, alternativeFire ? basicAlternativePayment : new ArrayList<>())) return false;
            game.getActualPlayer().unloadWeapon(Weapon.Name.getName(getClass()));
            fire(cost, alternativeFire ? basicAlternativePayment : new ArrayList<>());
            basicFireImpl();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Contract(pure = true)
    abstract boolean canBasicFire();

    abstract void basicFireImpl();

    public final boolean firstAdditionalFire() {
        try {
            var cost = new int[values().length];
            if (firstAdditionalCost != null) cost[firstAdditionalCost.getIndex()] = 1;
            if (!canFirstAdditionalFire() || !canFire(cost, firstAdditionalPayment)) return false;
            fire(cost, firstAdditionalPayment);
            firstAdditionalFireImpl();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Contract(pure = true)
    boolean canFirstAdditionalFire() {
        return true;
    }

    void firstAdditionalFireImpl() {
    }

    public final boolean secondAdditionalFire() {
        try {
            var cost = new int[values().length];
            if (secondAdditionalCost != null) cost[secondAdditionalCost.getIndex()] = 1;
            if (!canSecondAdditionalFire() || !canFire(cost, secondAdditionalPayment)) return false;
            fire(cost, secondAdditionalPayment);
            secondAdditionalFireImpl();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Contract(pure = true)
    boolean canSecondAdditionalFire() {
        return true;
    }

    void secondAdditionalFireImpl() {
    }

    @Contract(pure = true)
    public final boolean canAllFire() {
        return canBasicFire() && canFirstAdditionalFire() && canSecondAdditionalFire();
    }

    public void addBasicTarget(@NotNull Player player) {
        game.getPlayers().stream().filter(e -> e.equals(player)).findAny().ifPresent(basicTargets::add);
    }

    public void addFirstAdditionalTarget(@NotNull Player player) {
        game.getPlayers().stream().filter(e -> e.equals(player)).findAny().ifPresent(firstAdditionalTargets::add);
    }

    public void addSecondAdditionalTarget(@NotNull Player player) {
        game.getPlayers().stream().filter(e -> e.equals(player)).findAny().ifPresent(secondAdditionalTargets::add);
    }

    public void setBasicTargetsPoint(@NotNull Point basicTargetsPoint) {
        this.basicTargetsPoint = basicTargetsPoint;
    }

    public void setFirstAdditionalTargetsPoint(@NotNull Point firstAdditionalTargetsPoint) {
        this.firstAdditionalTargetsPoint = firstAdditionalTargetsPoint;
    }

    public void setBasicAlternativePayment(@NotNull ArrayList<PowerUp> basicAlternativePayment) {
        this.basicAlternativePayment = basicAlternativePayment;
    }

    public void setFirstAdditionalPayment(@NotNull ArrayList<PowerUp> firstAdditionalPayment) {
        this.firstAdditionalPayment = firstAdditionalPayment;
    }

    public void setSecondAdditionalPayment(@NotNull ArrayList<PowerUp> secondAdditionalPayment) {
        this.secondAdditionalPayment = secondAdditionalPayment;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean canFire(@NotNull int[] cost, @NotNull ArrayList<PowerUp> payment) {
        payment.forEach(e -> cost[e.getAmmoColor().getIndex()]--);
        return Stream.of(AmmoCard.Color.values()).map(e -> cost[e.getIndex()] <= game.getActualPlayer().getColoredCubes(e))
                .reduce(true, (e, f) -> e && f);
    }

    private void fire(@NotNull int[] cost, @NotNull ArrayList<PowerUp> payment) {
        payment.forEach(e -> game.getActualPlayer().getPowerUps().remove(e));
        Stream.of(AmmoCard.Color.values()).forEach(e -> game.getActualPlayer().removeColoredCubes(e, cost[e.getIndex()]));
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Weapon && ((Weapon) obj).getClass().equals(getClass());
    }

    public enum Name implements Displayable {
        LOCK_RIFLE(Weapons.LockRifle.class, BLUE, new int[]{0, 0, 1}), MACHINE_GUN(Weapons.MachineGun.class, BLUE, new int[]{1, 0, 0}),
        THOR(Weapons.Thor.class, BLUE, new int[]{1, 0, 0}), PLASMA_GUN(Weapons.PlasmaGun.class, BLUE, new int[]{0, 1, 0}),
        WHISPER(Weapons.Whisper.class, BLUE, new int[]{0, 1, 1}), ELECTROSCYTHE(Weapons.Electroscythe.class, BLUE, new int[]{0, 0, 0}),
        TRACTOR_BEAM(Weapons.TractorBeam.class, BLUE, new int[]{0, 0, 0}), VORTEX_CANNON(Weapons.VortexCannon.class, RED, new int[]{0, 0, 1}),
        FURNACE(Weapons.Furnace.class, RED, new int[]{0, 0, 1}), HEATSEEKER(Weapons.Heatseeker.class, RED, new int[]{1, 1, 0}),
        HELLION(Weapons.Hellion.class, RED, new int[]{0, 1, 0}), FLAMETHROWER(Weapons.Flamethrower.class, RED, new int[]{0, 0, 0}),
        GRENADE_LAUNCHER(Weapons.GrenadeLauncher.class, RED, new int[]{0, 0, 0}), ROCKET_LAUNCHER(Weapons.RocketLauncher.class, RED, new int[]{1, 0, 0}),
        RAILGUN(Weapons.Railgun.class, YELLOW, new int[]{0, 1, 1}), CYBERBLADE(Weapons.Cyberblade.class, YELLOW, new int[]{1, 0, 0}),
        ZX2(Weapons.ZX2.class, YELLOW, new int[]{1, 0, 0}), SHOTGUN(Weapons.Shotgun.class, YELLOW, new int[]{0, 1, 0}),
        POWER_GLOVE(Weapons.PowerGlove.class, YELLOW, new int[]{0, 0, 1}), SHOCKWAVE(Weapons.Shockwave.class, YELLOW, new int[]{0, 0, 0}),
        SLEDGEHAMMER(Weapons.Sledgehammer.class, YELLOW, new int[]{0, 0, 0});

        private final @NotNull Class<? extends Weapon> weaponClass;
        private final @NotNull AmmoCard.Color color;
        private final @NotNull int[] grabCost;

        @Contract(pure = true)
        Name(@NotNull Class<? extends Weapon> weaponClass, @NotNull AmmoCard.Color color, @NotNull int[] grabCost) {
            this.weaponClass = weaponClass;
            this.color = color;
            this.grabCost = grabCost;
        }

        public static @Nullable Name getName(@NotNull Class<? extends Weapon> weaponClass) {
            return Stream.of(values()).filter(e -> e.weaponClass.equals(weaponClass)).findAny().orElse(null);
        }

        @Contract(pure = true)
        public @NotNull Class<? extends Weapon> getWeaponClass() {
            return weaponClass;
        }

        @Contract(pure = true)
        public @NotNull AmmoCard.Color getColor() {
            return color;
        }

        @Contract(pure = true)
        public int getGrabCost(@NotNull AmmoCard.Color color) {
            return grabCost[color.getIndex()];
        }

        public @NotNull <T extends Weapon> T build(@NotNull Game game, boolean alternativeFire) throws MalformedParametersException {
            try {
                //noinspection unchecked
                return (T) getWeaponClass().getDeclaredConstructors()[0].newInstance(game, alternativeFire);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                throw new MalformedParametersException();
            }
        }

        @Override
        public @NotNull BufferedImage getFrontImage() throws IOException {
            return Utils.readImage(Weapon.class, toString());
        }

        @Override
        public @NotNull BufferedImage getBackImage() throws IOException {
            return Utils.readImage(Weapon.class, "back");
        }
    }

    private static class Weapons {
        private static class LockRifle extends Weapon {
            private LockRifle(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                firstAdditionalCost = RED;
            }

            @Override
            boolean canBasicFire() {
                return game.getActualPlayer().canSeeNotSame(basicTargets.get(0), game.getCells());
            }

            @Override
            void basicFireImpl() {
                basicTargets.get(0).takeHits(game.getActualPlayer(), 2, 1);
            }

            @Override
            boolean canFirstAdditionalFire() {
                return !basicTargets.get(0).equals(firstAdditionalTargets.get(0)) &&
                        game.getActualPlayer().canSeeNotSame(firstAdditionalTargets.get(0), game.getCells());
            }

            @Override
            void firstAdditionalFireImpl() {
                firstAdditionalTargets.get(0).takeHits(game.getActualPlayer(), 0, 1);
            }
        }

        private static class MachineGun extends Weapon {
            private MachineGun(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                firstAdditionalCost = YELLOW;
                secondAdditionalCost = BLUE;
            }

            @Override
            boolean canBasicFire() {
                return (basicTargets.size() == 1 || basicTargets.size() == 2) &&
                        basicTargets.stream().allMatch(e -> game.getActualPlayer().canSeeNotSame(e, game.getCells()));
            }

            @Override
            void basicFireImpl() {
                basicTargets.forEach(e -> e.takeHits(game.getActualPlayer(), 1, 0));
            }

            @Override
            boolean canFirstAdditionalFire() {
                return basicTargets.stream()
                        .anyMatch(e -> game.getActualPlayer().canSeeNotSame(e, game.getCells()) && firstAdditionalTargets.get(0).equals(e));
            }

            @Override
            void firstAdditionalFireImpl() {
                firstAdditionalTargets.get(0).takeHits(game.getActualPlayer(), 1, 0);
            }

            @Override
            boolean canSecondAdditionalFire() {
                return (secondAdditionalTargets.size() == 1 || secondAdditionalTargets.size() == 2) &&
                        secondAdditionalTargets.stream().allMatch(e ->
                                (basicTargets.contains(e) && !e.equals(firstAdditionalTargets.get(0))) ||
                                        (!basicTargets.contains(e) && game.getActualPlayer().canSeeNotSame(e, game.getCells())));
            }

            @Override
            void secondAdditionalFireImpl() {
                secondAdditionalTargets.forEach(e -> e.takeHits(game.getActualPlayer(), 1, 0));
            }
        }

        private static class Thor extends Weapon {
            private Thor(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                firstAdditionalCost = BLUE;
                secondAdditionalCost = BLUE;
            }

            @Override
            boolean canBasicFire() {
                return game.getActualPlayer().canSeeNotSame(basicTargets.get(0), game.getCells());
            }

            @Override
            void basicFireImpl() {
                basicTargets.get(0).takeHits(game.getActualPlayer(), 2, 0);
            }

            @Override
            boolean canFirstAdditionalFire() {
                return basicTargets.get(0).canSeeNotSame(firstAdditionalTargets.get(0), game.getCells());
            }

            @Override
            void firstAdditionalFireImpl() {
                firstAdditionalTargets.get(0).takeHits(game.getActualPlayer(), 1, 0);
            }

            @Override
            boolean canSecondAdditionalFire() {
                return firstAdditionalTargets.get(0).canSeeNotSame(secondAdditionalTargets.get(0), game.getCells());
            }

            @Override
            void secondAdditionalFireImpl() {
                secondAdditionalTargets.get(0).takeHits(game.getActualPlayer(), 2, 0);
            }
        }

        private static class PlasmaGun extends Weapon {
            private PlasmaGun(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                secondAdditionalCost = BLUE;
            }

            @Override
            boolean canBasicFire() {
                if (firstAdditionalTargetsPoint == null) return false;
                var mockPlayer = new Player(new User(""));
                mockPlayer.setPosition(firstAdditionalTargetsPoint);
                return game.getActualPlayer().canSeeNotSame(basicTargets.get(0), game.getCells()) ||
                        (game.canMove(game.getActualPlayer().getPosition(), firstAdditionalTargetsPoint, 2) &&
                                mockPlayer.canSeeNotSame(basicTargets.get(0), game.getCells()));
            }

            @Override
            void basicFireImpl() {
                if (firstAdditionalTargetsPoint == null) return;
                game.getActualPlayer().setPosition(firstAdditionalTargetsPoint);
                basicTargets.get(0).takeHits(game.getActualPlayer(), 2, 0);
            }

            @Override
            void secondAdditionalFireImpl() {
                secondAdditionalTargets.get(0).takeHits(game.getActualPlayer(), 1, 0);
            }
        }

        private static class Whisper extends Weapon {
            private Whisper(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
            }

            @Override
            boolean canBasicFire() {
                return game.getActualPlayer().canSeeNotSame(basicTargets.get(0), game.getCells()) &&
                        !game.canMove(game.getActualPlayer().getPosition(), basicTargets.get(0).getPosition(), 0) &&
                        !game.canMove(game.getActualPlayer().getPosition(), basicTargets.get(0).getPosition(), 1);
            }

            @Override
            void basicFireImpl() {
                basicTargets.get(0).takeHits(game.getActualPlayer(), 3, 1);
            }
        }

        private static class Electroscythe extends Weapon {
            private Electroscythe(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                basicAlternativeCost.addAll(List.of(BLUE, RED));
            }

            @Override
            boolean canBasicFire() {
                return true;
            }

            @Override
            void basicFireImpl() {
                if (game.getActualPlayer().getPosition() != null)
                    game.getPlayers().stream().filter(e -> game.getActualPlayer().getPosition().equals(e.getPosition()))
                            .forEach(e -> e.takeHits(game.getActualPlayer(), alternativeFire ? 2 : 1, 0));
            }
        }

        private static class TractorBeam extends Weapon {
            private TractorBeam(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                basicAlternativeCost.addAll(List.of(RED, YELLOW));
            }

            @Override
            boolean canBasicFire() {
                if (basicTargetsPoint == null) return false;
                var mockPlayer = new Player(new User(""));
                mockPlayer.setPosition(basicTargetsPoint);
                return (alternativeFire && game.canMove(basicTargets.get(0).getPosition(), game.getActualPlayer().getPosition(), 2)) ||
                        (!alternativeFire && game.canMove(basicTargets.get(0).getPosition(), basicTargetsPoint, 2) &&
                                game.getActualPlayer().canSeeNotSame(mockPlayer, game.getCells()));
            }

            @Override
            void basicFireImpl() {
                if (basicTargetsPoint == null) return;
                if (!alternativeFire) basicTargets.get(0).setPosition(basicTargetsPoint);
                basicTargets.get(0).takeHits(game.getActualPlayer(), alternativeFire ? 3 : 1, 0);
            }
        }

        private static class VortexCannon extends Weapon {
            private VortexCannon(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                firstAdditionalCost = RED;
            }

            @Override
            boolean canBasicFire() {
                return basicTargetsPoint != null && !basicTargetsPoint.equals(game.getActualPlayer().getPosition()) &&
                        game.canMove(basicTargets.get(0).getPosition(), basicTargetsPoint, 1);
            }

            @Override
            void basicFireImpl() {
                if (basicTargetsPoint == null) return;
                basicTargets.get(0).setPosition(basicTargetsPoint);
                basicTargets.get(0).takeHits(game.getActualPlayer(), 2, 0);
            }

            @Override
            boolean canFirstAdditionalFire() {
                return basicTargetsPoint != null && (firstAdditionalTargets.size() == 1 || firstAdditionalTargets.size() == 2) &&
                        firstAdditionalTargets.stream().allMatch(e -> !e.equals(basicTargets.get(0)) &&
                                game.canMove(e.getPosition(), basicTargetsPoint, 1));
            }

            @Override
            void firstAdditionalFireImpl() {
                if (basicTargetsPoint != null) firstAdditionalTargets.forEach(e -> {
                    e.setPosition(basicTargetsPoint);
                    e.takeHits(game.getActualPlayer(), 1, 0);
                });
            }
        }

        private static class Furnace extends Weapon {
            private Furnace(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
            }

            @Override
            boolean canBasicFire() {
                if (basicTargetsPoint == null) return false;
                if (alternativeFire) return game.canMove(game.getActualPlayer().getPosition(), basicTargetsPoint, 1);
                return game.getCell(game.getActualPlayer().getPosition()).getColor() != game.getCell(basicTargetsPoint).getColor() &&
                        game.getActualPlayer().canSeeCell(basicTargetsPoint, game.getCells());
            }

            @Override
            void basicFireImpl() {
                if (basicTargetsPoint == null) return;
                if (alternativeFire) game.getPlayers().stream().filter(e -> e.getPosition().equals(basicTargetsPoint))
                        .forEach(e -> e.takeHits(game.getActualPlayer(), 1, 1));
                else game.getPlayers().stream().filter(e -> game.getCell(e.getPosition()).getColor() ==
                        game.getCell(basicTargetsPoint).getColor()).forEach(e -> e.takeHits(game.getActualPlayer(), 1, 0));
            }
        }

        private static class Heatseeker extends Weapon {
            private Heatseeker(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
            }

            @Override
            boolean canBasicFire() {
                return !game.getActualPlayer().canSeeNotSame(basicTargets.get(0), game.getCells());
            }

            @Override
            void basicFireImpl() {
                basicTargets.get(0).takeHits(game.getActualPlayer(), 3, 0);
            }
        }

        private static class Hellion extends Weapon {
            private Hellion(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                basicAlternativeCost.add(RED);
            }

            @Override
            boolean canBasicFire() {
                return game.getActualPlayer().getPosition().equals(basicTargets.get(0).getPosition()) &&
                        !game.canMove(game.getActualPlayer().getPosition(), basicTargets.get(0).getPosition(), 1);
            }

            @Override
            void basicFireImpl() {
                basicTargets.get(0).takeHits(game.getActualPlayer(), 1, 0);
                game.getPlayers().stream().filter(e -> basicTargets.get(0).getPosition().equals(e.getPosition()))
                        .forEach(e -> e.takeHits(game.getActualPlayer(), 0, alternativeFire ? 2 : 1));
            }
        }

        private static class Flamethrower extends Weapon {
            private Flamethrower(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                basicAlternativeCost.addAll(List.of(YELLOW, YELLOW));
            }

            @Override
            boolean canBasicFire() {
                return false;
            }

            @Override
            void basicFireImpl() {

            }
        }

        private static class GrenadeLauncher extends Weapon {
            private GrenadeLauncher(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                firstAdditionalCost = RED;
            }

            @Override
            boolean canBasicFire() {
                return false;
            }

            @Override
            void basicFireImpl() {

            }
        }

        private static class RocketLauncher extends Weapon {
            private RocketLauncher(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                firstAdditionalCost = BLUE;
                secondAdditionalCost = YELLOW;
            }

            @Override
            boolean canBasicFire() {
                return false;
            }

            @Override
            void basicFireImpl() {

            }
        }

        private static class Railgun extends Weapon {
            private Railgun(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
            }

            @Override
            boolean canBasicFire() {
                return false;
            }

            @Override
            void basicFireImpl() {

            }
        }

        private static class Cyberblade extends Weapon {
            private Cyberblade(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                secondAdditionalCost = YELLOW;
            }

            @Override
            boolean canBasicFire() {
                return false;
            }

            @Override
            void basicFireImpl() {

            }

        }

        private static class ZX2 extends Weapon {
            private ZX2(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
            }

            @Override
            boolean canBasicFire() {
                return false;
            }

            @Override
            void basicFireImpl() {

            }
        }

        private static class Shotgun extends Weapon {
            private Shotgun(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
            }

            @Override
            boolean canBasicFire() {
                return false;
            }

            @Override
            void basicFireImpl() {

            }

        }

        private static class PowerGlove extends Weapon {
            private PowerGlove(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                basicAlternativeCost.add(BLUE);
            }

            @Override
            boolean canBasicFire() {
                return false;
            }

            @Override
            void basicFireImpl() {

            }

        }

        private static class Shockwave extends Weapon {
            private Shockwave(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                basicAlternativeCost.add(YELLOW);
            }

            @Override
            boolean canBasicFire() {
                return false;
            }

            @Override
            void basicFireImpl() {

            }
        }

        private static class Sledgehammer extends Weapon {
            private Sledgehammer(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                basicAlternativeCost.add(RED);
            }

            @Override
            boolean canBasicFire() {
                return false;
            }

            @Override
            void basicFireImpl() {

            }
        }
    }
}