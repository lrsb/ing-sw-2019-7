package it.polimi.ingsw.common.models;

import it.polimi.ingsw.client.views.sprite.Displayable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.MalformedParametersException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static it.polimi.ingsw.common.models.AmmoCard.Color.*;

@SuppressWarnings("SpellCheckingInspection")
public abstract class Weapon {
    @NotNull Game game;
    boolean alternativeFire;

    @NotNull ArrayList<Player> basicTargets = new ArrayList<>();
    @NotNull ArrayList<Point> basicTargetsPoint = new ArrayList<>();
    @NotNull ArrayList<AmmoCard.Color> basicAlternativeCost = new ArrayList<>();
    @NotNull ArrayList<Player> firstAdditionalTargets = new ArrayList<>();
    @NotNull ArrayList<Point> firstAdditionalTargetsPoint = new ArrayList<>();
    @NotNull ArrayList<AmmoCard.Color> firstAdditionalCost = new ArrayList<>();
    @NotNull ArrayList<Player> secondAdditionalTargets = new ArrayList<>();
    @NotNull ArrayList<Point> secondAdditionalTargetPoint = new ArrayList<>();
    @NotNull ArrayList<AmmoCard.Color> secondAdditionalCost = new ArrayList<>();
    private @NotNull ArrayList<PowerUp> basicAlternativePayment = new ArrayList<>();
    private @NotNull ArrayList<PowerUp> firstAdditionalPayment = new ArrayList<>();
    private @NotNull ArrayList<PowerUp> secondAdditionalPayment = new ArrayList<>();

    @Contract(pure = true)
    Weapon(@NotNull Game game, boolean alternativeFire) {
        this.game = game;
        this.alternativeFire = alternativeFire;
    }

    private static @NotNull int[] convertCost(@NotNull ArrayList<AmmoCard.Color> cost) {
        int[] costArray = new int[values().length];
        Stream.of(values()).forEach(e -> costArray[e.getIndex()] = (int) cost.stream().filter(f -> e == f).count());
        return costArray;
    }

    public final boolean basicFire() {
        var cost = convertCost(basicAlternativeCost);
        if (!(game.getActualPlayer().isALoadedGun(Weapon.Name.getName(getClass())) && canBasicFire()) ||
                !canFire(cost, alternativeFire ? basicAlternativePayment : new ArrayList<>())) return false;
        game.getActualPlayer().unloadWeapon(Weapon.Name.getName(getClass()));
        fire(cost, alternativeFire ? basicAlternativePayment : new ArrayList<>());
        basicFireImpl();
        return true;
    }

    @Contract(pure = true)
    abstract boolean canBasicFire();

    abstract void basicFireImpl();

    public final boolean firstAdditionalFire() {
        var cost = convertCost(firstAdditionalCost);
        if (!canFirstAdditionalFire() || !canFire(cost, firstAdditionalPayment)) return false;
        fire(cost, firstAdditionalPayment);
        firstAdditionalFireImpl();
        return true;
    }

    @Contract(pure = true)
    boolean canFirstAdditionalFire() {
        return true;
    }

    void firstAdditionalFireImpl() {
    }

    public final boolean secondAdditionalFire() {
        var cost = convertCost(secondAdditionalCost);
        if (!canSecondAdditionalFire() || !canFire(cost, secondAdditionalPayment)) return false;
        fire(cost, secondAdditionalPayment);
        secondAdditionalFireImpl();
        return true;
    }

    @Contract(pure = true)
    boolean canSecondAdditionalFire() {
        return true;
    }

    void secondAdditionalFireImpl() {
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

    //TODO: sistemare
    public enum Name implements Displayable {
        LOCK_RIFLE(Weapons.LockRifle.class, RED, new int[]{0, 1, 1}), MACHINE_GUN(Weapons.MachineGun.class, RED, new int[]{0, 1, 1}),
        THOR(Weapons.Thor.class, RED, new int[]{0, 1, 1}), PLASMA_GUN(Weapons.PlasmaGun.class, RED, new int[]{0, 1, 1}),
        WHISPER(Weapons.Whisper.class, RED, new int[]{0, 1, 1}), ELECTROSCYTHE(Weapons.Electroscythe.class, RED, new int[]{0, 1, 1}),
        TRACTOR_BEAM(Weapons.TractorBeam.class, RED, new int[]{0, 1, 1}), VORTEX_CANNON(Weapons.VortexCannon.class, RED, new int[]{0, 1, 1}),
        FURNACE(Weapons.Furnace.class, RED, new int[]{0, 1, 1}), HEATSEEKER(Weapons.Heatseeker.class, RED, new int[]{0, 1, 1}),
        HELLION(Weapons.Hellion.class, RED, new int[]{0, 1, 1}), FLAMETHROWER(Weapons.Flamethrower.class, RED, new int[]{0, 1, 1}),
        GRENADE_LAUNCHER(Weapons.GrenadeLauncher.class, RED, new int[]{0, 1, 1}), ROCKET_LAUNCHER(Weapons.RocketLauncher.class, RED, new int[]{0, 1, 1}),
        RAILGUN(Weapons.Railgun.class, RED, new int[]{0, 1, 1}), CYBERBLADE(Weapons.Cyberblade.class, RED, new int[]{0, 1, 1}),
        ZX2(Weapons.ZX2.class, RED, new int[]{0, 1, 1}), SHOTGUN(Weapons.Shotgun.class, RED, new int[]{0, 1, 1}),
        POWER_GLOVE(Weapons.PowerGlove.class, RED, new int[]{0, 1, 1}), SHOCKWAVE(Weapons.Shockwave.class, RED, new int[]{0, 1, 1}),
        SLEDGEHAMMER(Weapons.Sledgehammer.class, RED, new int[]{0, 1, 1});

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
        public @NotNull BufferedImage getImage() throws IOException {
            return ImageIO.read(Weapon.class.getResourceAsStream("Weapon/" + toString() + ".png"));
        }
    }

    //TODO: sistemare, ho implementato le prime due classi, occhio che in fire devi usre takehit sul parametro
    private static class Weapons {
        private static class LockRifle extends Weapon {
            private LockRifle(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                firstAdditionalCost.add(RED);
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
                firstAdditionalCost.add(YELLOW);
                secondAdditionalCost.add(BLUE);
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
                firstAdditionalCost.add(BLUE);
                secondAdditionalCost.add(BLUE);
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
                secondAdditionalCost.add(BLUE);
            }

            @Override
            boolean canBasicFire() {
                var mockPlayer = new Player(new User(""));
                mockPlayer.setPosition(firstAdditionalTargetsPoint.get(0));
                return game.getActualPlayer().canSeeNotSame(basicTargets.get(0), game.getCells()) ||
                        (game.canMove(game.getActualPlayer().getPosition(), firstAdditionalTargetsPoint.get(0), 2) &&
                                mockPlayer.canSeeNotSame(basicTargets.get(0), game.getCells()));
            }

            @Override
            void basicFireImpl() {
                game.getActualPlayer().setPosition(firstAdditionalTargetsPoint.get(0));
                basicTargets.get(0).takeHits(game.getActualPlayer(), 2, 0);
            }

            @Override
            void secondAdditionalFireImpl() {
                secondAdditionalTargets.get(0).takeHits(game.getActualPlayer(), 1, 0);
            }
        }

        private static class Whisper extends Weapon {
            public Whisper(@NotNull Game game, boolean alternativeFire) {
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
            public Electroscythe(@NotNull Game game, boolean alternativeFire) {
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
                            .forEach(e -> e.takeHits(game.getActualPlayer(), 1, 0));
            }

            @Override
            void firstAdditionalFireImpl() {
                if (game.getActualPlayer().getPosition() != null)
                    game.getPlayers().stream().filter(e -> game.getActualPlayer().getPosition().equals(e.getPosition()))
                            .forEach(e -> e.takeHits(game.getActualPlayer(), 2, 0));
            }
        }

        private static class TractorBeam extends Weapon {
            public TractorBeam(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                basicAlternativeCost.addAll(List.of(RED, YELLOW));
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
            public VortexCannon(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
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
            public Furnace(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
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
            public Heatseeker(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
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
            public Hellion(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                basicPayment.add(RED);
                basicPayment.add(YELLOW);
                alternativePayment.add(RED);
            }

            @Override
            protected boolean canFire() {
                possibleTarget.clear();
                for (Player player : game.getPlayers()) {
                    if (game.getActualPlayer().canSeeNotSame(player, game.getCells()) && !game.getActualPlayer().isPlayerNear(player, game.getCells()))
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
                return (game.getActualPlayer().canSeeNotSame(basicTarget.get(0), game.getCells()) && !game.getActualPlayer().isPlayerNear(basicTarget.get(0), game.getCells()));
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
            public Flamethrower(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
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
            public GrenadeLauncher(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
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
                if (!game.getActualPlayer().canSeeNotSame(basicTarget.get(0), game.getCells())) return false;
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
            public RocketLauncher(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
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
                            if (!game.getActualPlayer().canSeeNotSame(basicTarget.get(0), game.getCells()))
                                return false;
                        }
                } else {
                    if (!game.getActualPlayer().canSeeNotSame(basicTarget.get(0), game.getCells())) return false;
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
            public Railgun(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
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
            public Cyberblade(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
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
            public ZX2(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
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
                        (basicTarget.size() != 1 || !game.getActualPlayer().canSeeNotSame(basicTarget.get(0), game.getCells())))
                    return false;
                if (alternativeFire) {
                    if (basicTarget.size() > 3) return false;
                    for (Player player : basicTarget) {
                        if (!game.getActualPlayer().canSeeNotSame(player, game.getCells())) return false;
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
            public Shotgun(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
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
            public PowerGlove(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
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
            public Shockwave(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
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
            public Sledgehammer(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
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