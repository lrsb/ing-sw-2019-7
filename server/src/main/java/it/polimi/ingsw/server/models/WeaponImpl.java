package it.polimi.ingsw.server.models;

import it.polimi.ingsw.common.models.*;
import it.polimi.ingsw.common.models.wrappers.Opt;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.MalformedParametersException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static it.polimi.ingsw.common.models.AmmoCard.Color.*;

abstract class WeaponImpl {
    final static int FIRST = 1;
    final static int SECOND = 2;

    @NotNull Game game;
    boolean alternativeFire;

    @NotNull ArrayList<Player> basicTargets = new ArrayList<>();
    @Nullable Point basicTargetsPoint;
    @NotNull ArrayList<AmmoCard.Color> basicAlternativeCost = new ArrayList<>();

    @NotNull ArrayList<Player> firstAdditionalTargets = new ArrayList<>();
    @Nullable Point firstAdditionalTargetsPoint;
    @Nullable AmmoCard.Color firstAdditionalCost;

    @NotNull ArrayList<Player> secondAdditionalTargets = new ArrayList<>();
    @Nullable Point secondAdditionalTargetsPoint;
    @Nullable AmmoCard.Color secondAdditionalCost;

    private @NotNull ArrayList<PowerUp> alternativePaymentToUse = new ArrayList<>();
    private @NotNull ArrayList<PowerUp> alternativePaymentUsed = new ArrayList<>();
    private @NotNull int[] usedCubes = {0, 0, 0};

    @Contract(pure = true)
    private WeaponImpl(@NotNull Game game, boolean alternativeFire) {
        this.game = game;
        this.alternativeFire = alternativeFire;
    }

    private boolean basicFire() {
        try {
            if (!canBasicFire()) return false;
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

    private boolean firstAdditionalFire() {
        try {
            if (!canFirstAdditionalFire()) return false;
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

    private boolean secondAdditionalFire() {
        try {
            if (!canSecondAdditionalFire()) return false;
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
    final boolean fire(int option) {
        if (!canBasicFire()) return false;
        if (!canPayCost(option)) return false;
        switch (option) {
            case 0:
                if (!(getClass().equals(Weapons.RocketLauncher.class) && firstAdditionalTargetsPoint != null))
                    if (basicFire()) {
                        payCost();
                        game.getActualPlayer().unloadWeapon(WeaponImpl.Loader.getName(getClass()));
                        return true;
                    }
                break;
            case 1:
                if (canFirstAdditionalFire()) if (basicFire() && firstAdditionalFire()) {
                    payCost();
                    game.getActualPlayer().unloadWeapon(WeaponImpl.Loader.getName(getClass()));
                    return true;
                }
                break;
            case 2:
                if (canSecondAdditionalFire() && !getClass().equals(Weapons.Thor.class) &&
                        !(getClass().equals(Weapons.RocketLauncher.class) && firstAdditionalTargetsPoint != null))
                    if (basicFire() && secondAdditionalFire()) {
                        payCost();
                        game.getActualPlayer().unloadWeapon(WeaponImpl.Loader.getName(getClass()));
                        return true;
                    }
                break;
            case 3:
                if (canFirstAdditionalFire() && canSecondAdditionalFire())
                    if (basicFire() && firstAdditionalFire() && secondAdditionalFire()) {
                        payCost();
                        game.getActualPlayer().unloadWeapon(WeaponImpl.Loader.getName(getClass()));
                        return true;
                    }
                break;
        }
        return false;
    }

    void addBasicTarget(@NotNull UUID playerUuid) {
        if (game.getActualPlayer().getUuid().equals(playerUuid) ||
                basicTargets.parallelStream().anyMatch(e -> e.getUuid().equals(playerUuid))) return;
        game.getPlayers().stream().filter(e -> e.getUuid().equals(playerUuid) && e.getPosition() != null).findAny().ifPresent(basicTargets::add);
    }

    void addFirstAdditionalTarget(@NotNull UUID playerUuid) {
        if (game.getActualPlayer().getUuid().equals(playerUuid) ||
                firstAdditionalTargets.parallelStream().anyMatch(e -> e.getUuid().equals(playerUuid))) return;
        game.getPlayers().stream().filter(e -> e.getUuid().equals(playerUuid) && e.getPosition() != null).findAny().ifPresent(firstAdditionalTargets::add);
    }

    void addSecondAdditionalTarget(@NotNull UUID playerUuid) {
        if (game.getActualPlayer().getUuid().equals(playerUuid) ||
                secondAdditionalTargets.parallelStream().anyMatch(e -> e.getUuid().equals(playerUuid))) return;
        game.getPlayers().stream().filter(e -> e.getUuid().equals(playerUuid) && e.getPosition() != null).findAny().ifPresent(secondAdditionalTargets::add);
    }

    void setBasicTargetsPoint(@NotNull Point basicTargetsPoint) {
        this.basicTargetsPoint = basicTargetsPoint;
    }

    void setFirstAdditionalTargetsPoint(@NotNull Point firstAdditionalTargetsPoint) {
        this.firstAdditionalTargetsPoint = firstAdditionalTargetsPoint;
    }

    void setSecondAdditionalTargetsPoint(@NotNull Point secondAdditionalTargetsPoint) {
        this.secondAdditionalTargetsPoint = secondAdditionalTargetsPoint;
    }

    void setAlternativePaymentToUse(@NotNull List<PowerUp> alternativePaymentToUse) {
        this.alternativePaymentToUse.clear();
        this.alternativePaymentToUse.addAll(alternativePaymentToUse);
    }

    @NotNull ArrayList<PowerUp> getAlternativePaymentUsed() {
        return alternativePaymentUsed;
    }

    private boolean canPayCost(int option) {
        if (!game.getActualPlayer().isALoadedGun(WeaponImpl.Loader.getName(getClass())) && game.getSkulls() == 0) {
            usedCubes[WeaponImpl.Loader.getName(getClass()).getColor().getIndex()]++;
            Stream.of(AmmoCard.Color.values()).forEach(e -> usedCubes[e.getIndex()] += WeaponImpl.Loader.getName(getClass()).getGrabCost(e));
        }
        switch (option) {
            case 0:
                if (alternativeFire) basicAlternativeCost.forEach(e -> usedCubes[e.getIndex()]++);
                break;
            case 1:
                if (firstAdditionalCost != null) usedCubes[firstAdditionalCost.getIndex()]++;
                break;
            case 2:
                if (secondAdditionalCost != null) usedCubes[secondAdditionalCost.getIndex()]++;
                break;
            case 3:
                if (firstAdditionalCost != null) usedCubes[firstAdditionalCost.getIndex()]++;
                if (secondAdditionalCost != null) usedCubes[secondAdditionalCost.getIndex()]++;
                break;
            default:
                return false;
        }
        alternativePaymentToUse.forEach(e -> {
            if (usedCubes[e.getAmmoColor().getIndex()] > 0 && game.getActualPlayer().hasPowerUp(e) &&
                    game.getActualPlayer().getPowerUps().stream().filter(e::equals).count() > alternativePaymentUsed.stream().filter(e::equals).count()) {
                alternativePaymentUsed.add(e);
                usedCubes[e.getAmmoColor().getIndex()]--;
            }
        });
        alternativePaymentUsed.forEach(e -> {
            alternativePaymentToUse.remove(e);
        });
        return Stream.of(AmmoCard.Color.values()).allMatch(e -> game.getActualPlayer().getColoredCubes(e) >= usedCubes[e.getIndex()]);
    }

    private void payCost() {
        alternativePaymentUsed.forEach(e -> game.getActualPlayer().removePowerUp(e));
        Stream.of(AmmoCard.Color.values()).forEach(e -> game.getActualPlayer().removeColoredCubes(e, usedCubes[e.getIndex()]));
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj) {
        return obj instanceof WeaponImpl && ((WeaponImpl) obj).getClass().equals(getClass());
    }

    static class Loader {
        static @NotNull <T extends WeaponImpl> T build(@NotNull Weapon weapon, @NotNull Game game, boolean alternativeFire) throws MalformedParametersException {
            try {
                var weaponClass = getWeapon(weapon);
                if (weaponClass == null) throw new MalformedParametersException();
                //noinspection unchecked
                return (T) weaponClass.getDeclaredConstructors()[0].newInstance(game, alternativeFire);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                throw new MalformedParametersException();
            }
        }

        private static @Nullable Class getWeapon(@NotNull Weapon weapon) {
            return Stream.of(WeaponImpl.Weapons.class.getDeclaredClasses()).parallel()
                    .filter(e -> e.getSimpleName().toLowerCase().contains(weapon.toString().toLowerCase().replace("_", ""))).findAny().orElse(null);
        }


        static @Nullable Weapon getName(@NotNull Class<? extends WeaponImpl> weaponClass) {
            return Stream.of(Weapon.values()).filter(e -> weaponClass.equals(getWeapon(e))).findAny().orElse(null);
        }
    }

    @SuppressWarnings("unused")
    static class Weapons {
        static class LockRifle extends WeaponImpl {
            LockRifle(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                firstAdditionalCost = RED;
            }

            @Override
            boolean canBasicFire() {
                return basicTargets.size() == 1 &&
                        game.getActualPlayer().canSeeNotSame(basicTargets.get(0), game.getCells());
            }

            @Override
            void basicFireImpl() {
                basicTargets.get(0).takeHits(game, 2, 1);
            }

            @Override
            boolean canFirstAdditionalFire() {
                return firstAdditionalTargets.size() == 1 &&
                        !basicTargets.get(0).equals(firstAdditionalTargets.get(0)) &&
                        game.getActualPlayer().canSeeNotSame(firstAdditionalTargets.get(0), game.getCells());
            }

            @Override
            void firstAdditionalFireImpl() {
                firstAdditionalTargets.get(0).takeHits(game, 0, 1);
            }
        }

        static class MachineGun extends WeaponImpl {
            MachineGun(@NotNull Game game, boolean alternativeFire) {
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
                basicTargets.forEach(e -> e.takeHits(game, 1, 0));
            }

            @Override
            boolean canFirstAdditionalFire() {
                return firstAdditionalTargets.size() == 1 && basicTargets.stream()
                        .anyMatch(e -> game.getActualPlayer().canSeeNotSame(e, game.getCells()) && firstAdditionalTargets.get(0).equals(e));
            }

            @Override
            void firstAdditionalFireImpl() {
                firstAdditionalTargets.get(0).takeHits(game, 1, 0);
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
                secondAdditionalTargets.forEach(e -> e.takeHits(game, 1, 0));
            }
        }

        static class Thor extends WeaponImpl {
            Thor(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                firstAdditionalCost = BLUE;
                secondAdditionalCost = BLUE;
            }

            @Override
            boolean canBasicFire() {
                return basicTargets.size() == 1 &&
                        game.getActualPlayer().canSeeNotSame(basicTargets.get(0), game.getCells());
            }

            @Override
            void basicFireImpl() {
                basicTargets.get(0).takeHits(game, 2, 0);
            }

            @Override
            boolean canFirstAdditionalFire() {
                return firstAdditionalTargets.size() == 1 &&
                        basicTargets.get(0).canSeeNotSame(firstAdditionalTargets.get(0), game.getCells());
            }

            @Override
            void firstAdditionalFireImpl() {
                firstAdditionalTargets.get(0).takeHits(game, 1, 0);
            }

            @Override
            boolean canSecondAdditionalFire() {
                return secondAdditionalTargets.size() == 1 &&
                        !basicTargets.get(0).equals(secondAdditionalTargets.get(0)) &&
                        firstAdditionalTargets.get(0).canSeeNotSame(secondAdditionalTargets.get(0), game.getCells());
            }

            @Override
            void secondAdditionalFireImpl() {
                secondAdditionalTargets.get(0).takeHits(game, 2, 0);
            }
        }

        static class PlasmaGun extends WeaponImpl {
            PlasmaGun(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                secondAdditionalCost = BLUE;
            }

            @Override
            boolean canBasicFire() {
                if (basicTargets.size() != 1) return false;
                if (basicTargetsPoint == null) return game.getActualPlayer()
                        .canSeeNotSame(basicTargets.get(0), game.getCells());
                var mockPlayer = new Player(new User(""), Player.BoardType.BANSHEE);
                mockPlayer.setPosition(basicTargetsPoint);
                return game.canMove(game.getActualPlayer().getPosition(), basicTargetsPoint, 2) &&
                        (mockPlayer.canSeeNotSame(basicTargets.get(0), game.getCells()) ||
                                game.getActualPlayer().canSeeNotSame(basicTargets.get(0), game.getCells()));
            }

            @Override
            void basicFireImpl() {
                if (basicTargetsPoint != null)
                    game.getActualPlayer().setPosition(basicTargetsPoint);
                basicTargets.get(0).takeHits(game, 2, 0);
            }

            @Override
            void secondAdditionalFireImpl() {
                basicTargets.get(0).takeHits(game, 1, 0);
            }
        }

        static class Whisper extends WeaponImpl {
            Whisper(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
            }

            @Override
            boolean canBasicFire() {
                return basicTargets.size() == 1 &&
                        game.getActualPlayer().canSeeNotSame(basicTargets.get(0), game.getCells()) &&
                        !game.canMove(game.getActualPlayer().getPosition(), basicTargets.get(0).getPosition(), 1);
            }

            @Override
            void basicFireImpl() {
                basicTargets.get(0).takeHits(game, 3, 1);
            }
        }

        static class Electroscythe extends WeaponImpl {
            Electroscythe(@NotNull Game game, boolean alternativeFire) {
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
                    game.getPlayers().stream().filter(e -> !e.equals(game.getActualPlayer()) &&
                            game.getActualPlayer().getPosition().equals(e.getPosition()))
                            .forEach(e -> e.takeHits(game, alternativeFire ? 2 : 1, 0));
            }
        }

        static class TractorBeam extends WeaponImpl {
            TractorBeam(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                basicAlternativeCost.addAll(List.of(RED, YELLOW));
            }

            @Override
            boolean canBasicFire() {
                if (basicTargets.size() != 1) return false;
                var mockPlayer = new Player(new User(""), Player.BoardType.BANSHEE);
                if (basicTargetsPoint == null) basicTargetsPoint = basicTargets.get(0).getPosition();
                mockPlayer.setPosition(basicTargetsPoint);
                return (alternativeFire && game.canMove(basicTargets.get(0).getPosition(), game.getActualPlayer().getPosition(), 2)) ||
                        (!alternativeFire && game.canMove(basicTargets.get(0).getPosition(), mockPlayer.getPosition(), 2) &&
                                game.getActualPlayer().canSeeNotSame(mockPlayer, game.getCells()));
            }

            @Override
            void basicFireImpl() {
                if (!alternativeFire) basicTargets.get(0).setPosition(basicTargetsPoint);
                else basicTargets.get(0).setPosition(game.getActualPlayer().getPosition());
                basicTargets.get(0).takeHits(game, alternativeFire ? 3 : 1, 0);
            }
        }

        static class VortexCannon extends WeaponImpl {
            VortexCannon(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                firstAdditionalCost = RED;
            }

            @Override
            boolean canBasicFire() {
                return basicTargets.size() == 1 && basicTargetsPoint != null &&
                        !basicTargetsPoint.equals(game.getActualPlayer().getPosition()) &&
                        game.canMove(basicTargets.get(0).getPosition(), basicTargetsPoint, 1);
            }

            @Override
            void basicFireImpl() {
                if (basicTargetsPoint == null) return;
                basicTargets.get(0).setPosition(basicTargetsPoint);
                basicTargets.get(0).takeHits(game, 2, 0);
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
                    e.takeHits(game, 1, 0);
                });
            }
        }

        static class Furnace extends WeaponImpl {
            Furnace(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
            }

            @Override
            boolean canBasicFire() {
                if (basicTargetsPoint == null || game.getActualPlayer().getPosition().equals(basicTargetsPoint))
                    return false;
                if (alternativeFire) return game.canMove(game.getActualPlayer().getPosition(), basicTargetsPoint, 1);
                return Opt.of(game.getCell(game.getActualPlayer().getPosition())).e(Cell::getColor).get() != Opt.of(game.getCell(basicTargetsPoint)).e(Cell::getColor).get() &&
                        game.getActualPlayer().canSeeCell(basicTargetsPoint, game.getCells());
            }

            @Override
            void basicFireImpl() {
                if (basicTargetsPoint == null) return;
                if (alternativeFire)
                    game.getPlayers().parallelStream().filter(e -> Opt.of(e.getPosition()).e(f -> f.equals(basicTargetsPoint))
                            .get(false)).forEachOrdered(e -> e.takeHits(game, 1, 1));
                else game.getPlayers().parallelStream().filter(e -> Opt.of(game.getCell(e.getPosition()))
                        .e(f -> f.getColor().equals(Opt.of(game.getCell(basicTargetsPoint)).e(Cell::getColor).get()))
                        .get(false)).forEachOrdered(e -> e.takeHits(game, 1, 0));
            }
        }

        static class Heatseeker extends WeaponImpl {
            Heatseeker(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
            }

            @Override
            boolean canBasicFire() {
                return basicTargets.size() == 1 &&
                        !game.getActualPlayer().canSeeNotSame(basicTargets.get(0), game.getCells());
            }

            @Override
            void basicFireImpl() {
                basicTargets.get(0).takeHits(game, 3, 0);
            }
        }

        static class Hellion extends WeaponImpl {
            Hellion(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                basicAlternativeCost.add(RED);
            }

            @Override
            boolean canBasicFire() {
                return basicTargets.size() == 1 &&
                        Opt.of(game.getActualPlayer().getPosition()).e(e -> !e.equals(basicTargets.get(0).getPosition())).get(false) &&
                        game.getActualPlayer().canSeeNotSame(basicTargets.get(0), game.getCells());
            }

            @Override
            void basicFireImpl() {
                basicTargets.get(0).takeHits(game, 1, 0);
                game.getPlayers().stream().filter(e -> Opt.of(e.getPosition()).e(f -> f.equals(basicTargets.get(0).getPosition())).get(false))
                        .forEach(e -> e.takeHits(game, 0, alternativeFire ? 2 : 1));
            }
        }

        static class Flamethrower extends WeaponImpl {
            Flamethrower(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                basicAlternativeCost.addAll(List.of(YELLOW, YELLOW));
            }

            @Override
            boolean canBasicFire() {
                if (!alternativeFire && !basicTargets.isEmpty() && basicTargets.size() < 3)
                    return Stream.of(Bounds.Direction.values()).parallel().anyMatch(e ->
                            basicTargets.stream().allMatch(f -> Opt.of(f.getPosition()).e(g -> game.getActualPlayer().isPointAtMaxDistanceInDirection(g, game.getCells(), 2, e)).get(false))) &&
                            (basicTargets.size() != 2 && !game.getActualPlayer().getPosition().equals(basicTargets.get(0).getPosition()) ||
                                    (basicTargets.stream().mapToDouble(e -> Opt.of(e.getPosition()).e(Point::getX).get()).reduce(1, (e, f) -> e * (f - game.getActualPlayer().getPosition().getX())) == 2) ||
                                    (basicTargets.stream().mapToDouble(e -> Opt.of(e.getPosition()).e(Point::getY).get()).reduce(1, (e, f) -> e * (f - game.getActualPlayer().getPosition().getY())) == 2));
                else
                    return alternativeFire && basicTargetsPoint != null && !game.getActualPlayer().getPosition().equals(basicTargetsPoint) &&
                            Stream.of(Bounds.Direction.values()).anyMatch(e -> game.getActualPlayer().isPointAtMaxDistanceInDirection(basicTargetsPoint, game.getCells(), 2, e));
            }

            @Override
            void basicFireImpl() {
                if (!alternativeFire) basicTargets.forEach(e -> e.takeHits(game, 1, 0));
                else if (basicTargetsPoint != null) {
                    if (Stream.of(Bounds.Direction.values()).noneMatch(e -> game.getActualPlayer()
                            .isPointAtMaxDistanceInDirection(basicTargetsPoint, game.getCells(), 1, e))) {
                        Stream.of(Bounds.Direction.values()).filter(e -> game.getActualPlayer()
                                .isPointAtMaxDistanceInDirection(basicTargetsPoint, game.getCells(), 2, e))
                                .forEach(e -> game.getPlayersAtPosition(new Point((int) basicTargetsPoint.getX() - e.getdX(),
                                        (int) basicTargetsPoint.getY() - e.getdY())).forEach(f -> f.takeHits(game, 2, 0)));
                        game.getPlayersAtPosition(basicTargetsPoint)
                                .forEach(e -> e.takeHits(game, 1, 0));
                    } else game.getPlayersAtPosition(basicTargetsPoint).forEach(e -> e.takeHits(game, 2, 0));
                }
            }
        }

        static class GrenadeLauncher extends WeaponImpl {
            GrenadeLauncher(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                firstAdditionalCost = RED;
            }

            @Override
            boolean canBasicFire() {
                return basicTargets.size() == 1 && game.getActualPlayer().canSeeNotSame(basicTargets.get(0), game.getCells()) &&
                        (basicTargetsPoint == null || Opt.of(basicTargets.get(0).getPosition())
                                .e(e -> e.equals(basicTargetsPoint)).get(false) ||
                                Stream.of(Bounds.Direction.values()).anyMatch(e -> basicTargets.get(0)
                                        .isPointAtMaxDistanceInDirection(basicTargetsPoint, game.getCells(), 1, e)));
            }

            @Override
            void basicFireImpl() {
                basicTargets.get(0).takeHits(game, 1, 0);
                if (!alternativeFire && basicTargetsPoint != null) basicTargets.get(0).setPosition(basicTargetsPoint);
            }

            @Override
            boolean canFirstAdditionalFire() {
                return firstAdditionalTargetsPoint != null &&
                        game.getActualPlayer().canSeeCell(firstAdditionalTargetsPoint, game.getCells());
            }

            @Override
            void firstAdditionalFireImpl() {
                game.getPlayers().stream().filter(e -> !e.equals(game.getActualPlayer()) &&
                        Opt.of(e.getPosition()).e(f -> f.equals(firstAdditionalTargetsPoint)).get(false))
                        .forEach(e -> e.takeHits(game, 1, 0));
                if (alternativeFire) basicTargets.get(0).setPosition(basicTargetsPoint);
            }
        }

        static class RocketLauncher extends WeaponImpl {
            RocketLauncher(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                firstAdditionalCost = BLUE;
                secondAdditionalCost = YELLOW;
            }

            @Override
            boolean canBasicFire() {
                var mockPlayer = new Player(new User(""), Player.BoardType.BANSHEE);
                if (firstAdditionalTargetsPoint != null) mockPlayer.setPosition(firstAdditionalTargetsPoint);
                return basicTargets.size() == 1 &&
                        (game.getActualPlayer().canSeeNotSame(basicTargets.get(0), game.getCells()) &&
                                Opt.of(game.getActualPlayer().getPosition())
                                        .e(e -> !e.equals(basicTargets.get(0).getPosition())).get(false) ||
                                canFirstAdditionalFire() && mockPlayer.canSeeNotSame(basicTargets.get(0), game.getCells()) &&
                                        !firstAdditionalTargetsPoint.equals(basicTargets.get(0).getPosition())) &&
                        (basicTargetsPoint == null ||
                                game.canMove(basicTargets.get(0).getPosition(), basicTargetsPoint, 1));
            }

            @Override
            void basicFireImpl() {
                secondAdditionalTargetsPoint = basicTargets.get(0).getPosition();
                basicTargets.get(0).takeHits(game, 2, 0);
                if (basicTargetsPoint != null) basicTargets.get(0).setPosition(basicTargetsPoint);
            }

            @Override
            boolean canFirstAdditionalFire() {
                return firstAdditionalTargetsPoint != null &&
                        game.canMove(game.getActualPlayer().getPosition(), firstAdditionalTargetsPoint, 2);
            }

            @Override
            void firstAdditionalFireImpl() {
                game.getActualPlayer().setPosition(firstAdditionalTargetsPoint);
            }

            @Override
            boolean canSecondAdditionalFire() {
                return true;
            }

            @Override
            void secondAdditionalFireImpl() {
                game.getPlayers().stream().filter(e -> e.equals(basicTargets.get(0)) ||
                        Opt.of(e.getPosition()).e(f -> f.equals(secondAdditionalTargetsPoint)).get(false))
                        .forEach(e -> e.takeHits(game, 1, 0));
            }
        }

        static class Railgun extends WeaponImpl {
            Railgun(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
            }

            @Override
            boolean canBasicFire() {
                if (game.getActualPlayer().getPosition() == null || basicTargets.isEmpty()) return false;
                var actualX = game.getActualPlayer().getPosition().getX();
                var actualY = game.getActualPlayer().getPosition().getY();
                if (!(Opt.of(basicTargets.get(0).getPosition()).e(e -> e.getX() == actualX).get(false) ||
                        Opt.of(basicTargets.get(0).getPosition()).e(e -> e.getY() == actualY).get(false)))
                    return false;
                if (basicTargets.size() == 2) {
                    if (!alternativeFire) return false;
                    var firstX = Opt.of(basicTargets.get(0).getPosition()).e(e -> e.x).get(-1);
                    var firstY = Opt.of(basicTargets.get(0).getPosition()).e(e -> e.y).get(-1);
                    var secondX = Opt.of(basicTargets.get(1).getPosition()).e(e -> e.x).get(-1);
                    var secondY = Opt.of(basicTargets.get(1).getPosition()).e(e -> e.y).get(-1);
                    return (actualX == firstX && actualX == secondX && (actualY - firstY) * (actualY - secondY) >= 0 ||
                            actualY == firstY && actualY == secondY && (actualX - firstX) * (actualX - secondX) >= 0);
                }
                return basicTargets.size() == 1;
            }

            @Override
            void basicFireImpl() {
                basicTargets.forEach(e -> e.takeHits(game, alternativeFire ? 2 : 3, 0));
            }
        }

        static class Cyberblade extends WeaponImpl {
            Player mockPlayer = new Player(new User(""), Player.BoardType.BANSHEE);

            Cyberblade(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                secondAdditionalCost = YELLOW;
            }

            @Override
            boolean canBasicFire() {
                if (basicTargets.size() != 1) return false;
                mockPlayer.setPosition(game.getActualPlayer().getPosition());
                if (Opt.of(mockPlayer.getPosition()).e(e -> !e.equals(basicTargets.get(0).getPosition())).get(false) &&
                        basicTargetsPoint != null)
                    mockPlayer.setPosition(basicTargetsPoint);
                return Opt.of(mockPlayer.getPosition()).e(e ->
                        e.equals(basicTargets.get(0).getPosition())).get(false) && (basicTargetsPoint == null ||
                        game.canMove(game.getActualPlayer().getPosition(), basicTargetsPoint, 1));
            }

            @Override
            void basicFireImpl() {
                game.getActualPlayer().setPosition(mockPlayer.getPosition());
                basicTargets.get(0).takeHits(game, 2, 0);
                if (!canFirstAdditionalFire() && basicTargetsPoint != null)
                    game.getActualPlayer().setPosition(basicTargetsPoint);
            }

            @Override
            boolean canSecondAdditionalFire() {
                if (secondAdditionalTargets.size() != 1) return false;
                if (Opt.of(mockPlayer.getPosition()).e(e -> !e.equals(secondAdditionalTargets.get(0).getPosition()))
                        .get(false) && basicTargetsPoint != null)
                    mockPlayer.setPosition(basicTargetsPoint);
                return Opt.of(mockPlayer.getPosition()).e(e -> e.equals(secondAdditionalTargets.get(0).getPosition()))
                        .get(false) && !basicTargets.get(0).equals(secondAdditionalTargets.get(0));
            }

            @Override
            void secondAdditionalFireImpl() {
                game.getActualPlayer().setPosition(mockPlayer.getPosition());
                secondAdditionalTargets.get(0).takeHits(game, 2, 0);
                if (basicTargetsPoint != null) game.getActualPlayer().setPosition(basicTargetsPoint);
            }

        }

        static class ZX2 extends WeaponImpl {
            ZX2(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
            }

            @Override
            boolean canBasicFire() {
                return !basicTargets.isEmpty() &&
                        (!alternativeFire && basicTargets.size() == 1 || alternativeFire && basicTargets.size() < 4) &&
                        basicTargets.stream().allMatch(e -> game.getActualPlayer().canSeeNotSame(e, game.getCells()));
            }

            @Override
            void basicFireImpl() {
                basicTargets.forEach(e -> e.takeHits(game, alternativeFire ? 0 : 1, alternativeFire ? 1 : 2));
            }
        }

        static class Shotgun extends WeaponImpl {
            Shotgun(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
            }

            @Override
            boolean canBasicFire() {
                var targetPosition = basicTargets.size() == 1 ? basicTargets.get(0).getPosition() : null;
                if (targetPosition == null) return false;
                return basicTargets.size() == 1 && (!alternativeFire &&
                        targetPosition.equals(game.getActualPlayer().getPosition()) &&
                        (basicTargetsPoint == null ||
                                game.canMove(basicTargets.get(0).getPosition(), basicTargetsPoint, 1)) ||
                        alternativeFire && !targetPosition.equals(game.getActualPlayer().getPosition()) &&
                                game.canMove(game.getActualPlayer().getPosition(), basicTargets.get(0).getPosition(), 1));
            }

            @Override
            void basicFireImpl() {
                basicTargets.get(0).takeHits(game, alternativeFire ? 2 : 3, 0);
                if (!alternativeFire) basicTargets.get(0).setPosition(basicTargetsPoint);
            }

        }

        static class PowerGlove extends WeaponImpl {
            PowerGlove(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                basicAlternativeCost.add(BLUE);
            }

            @Override
            boolean canBasicFire() {
                var actualPlayerPosition = game.getActualPlayer().getPosition();
                if (actualPlayerPosition == null) return false;
                if (alternativeFire) {
                    if (basicTargetsPoint == null || game.getActualPlayer().getPosition().equals(basicTargetsPoint) ||
                            Stream.of(Bounds.Direction.values()).noneMatch(e -> game.getActualPlayer()
                                    .isPointAtMaxDistanceInDirection(basicTargetsPoint, game.getCells(), 2, e) &&
                                    (basicTargets.isEmpty() || basicTargets.stream().allMatch(f ->
                                            !game.getActualPlayer().getPosition().equals(f.getPosition()) &&
                                                    game.getActualPlayer().isPointAtMaxDistanceInDirection
                                                            (f.getPosition(), game.getCells(), 2, e)))))
                        return false;
                    if (basicTargets.isEmpty()) return true;
                    else if (basicTargets.size() == 1)
                        return !(!game.canMove(game.getActualPlayer().getPosition(), basicTargets.get(0).getPosition(), 1) &&
                                game.canMove(game.getActualPlayer().getPosition(), basicTargetsPoint, 1));
                    else if (basicTargets.size() == 2)
                        return !Opt.of(basicTargets.get(0).getPosition()).e(e -> e.equals(basicTargets.get(1).getPosition())).get(false) &&
                                !game.canMove(game.getActualPlayer().getPosition(), basicTargetsPoint, 1);
                    return false;
                } else {
                    return basicTargets.size() == 1 &&
                            !game.getActualPlayer().getPosition().equals(basicTargets.get(0).getPosition()) &&
                            game.canMove(game.getActualPlayer().getPosition(), basicTargets.get(0).getPosition(), 1);
                }
            }

            @Override
            void basicFireImpl() {
                if (!alternativeFire) {
                    game.getActualPlayer().setPosition(basicTargets.get(0).getPosition());
                    basicTargets.get(0).takeHits(game, 1, 2);
                } else {
                    if (basicTargetsPoint == null || game.getActualPlayer().getPosition() == null) return;
                    Stream.of(Bounds.Direction.values()).filter(e -> game.getActualPlayer()
                            .isPointAtMaxDistanceInDirection(basicTargetsPoint, game.getCells(), 2, e))
                            .forEach(e -> game.getActualPlayer().setPosition
                                    (new Point((int) game.getActualPlayer().getPosition().getX() + e.getdX(),
                                            (int) game.getActualPlayer().getPosition().getY() + e.getdY())));
                    basicTargets.stream().filter(e -> game.getActualPlayer().getPosition().equals(e.getPosition()))
                            .forEach(e -> e.takeHits(game, 2, 0));
                    if (!game.getActualPlayer().getPosition().equals(basicTargetsPoint)) {
                        game.getActualPlayer().setPosition(basicTargetsPoint);
                        basicTargets.stream().filter(e -> game.getActualPlayer().getPosition().equals(e.getPosition()))
                                .forEach(e -> e.takeHits(game, 2, 0));
                    }
                }
            }

        }

        static class Shockwave extends WeaponImpl {
            Shockwave(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                basicAlternativeCost.add(YELLOW);
            }

            @Override
            boolean canBasicFire() {
                var actualPlayerPosition = game.getActualPlayer().getPosition();
                if (actualPlayerPosition == null) return false;
                return alternativeFire || !basicTargets.isEmpty() && basicTargets.size() < 3 && basicTargets.stream()
                        .allMatch(e -> game.canMove(game.getActualPlayer().getPosition(), e.getPosition(), 1) &&
                                !game.getActualPlayer().getPosition().equals(e.getPosition())) &&
                        basicTargets.stream().map(Player::getPosition).distinct().count() == basicTargets.size();
            }

            @Override
            void basicFireImpl() {
                if (!alternativeFire) {
                    basicTargets.forEach(e -> e.takeHits(game, 1, 0));
                } else {
                    game.getPlayers().parallelStream().filter(e ->
                            Opt.of(game.getActualPlayer().getPosition()).e(f -> !f.equals(e.getPosition())).get(false) &&
                                    game.canMove(game.getActualPlayer().getPosition(), e.getPosition(), 1))
                            .forEach(e -> e.takeHits(game, 1, 0));
                }
            }
        }

        static class Sledgehammer extends WeaponImpl {
            Sledgehammer(@NotNull Game game, boolean alternativeFire) {
                super(game, alternativeFire);
                basicAlternativeCost.add(RED);
            }

            @Override
            boolean canBasicFire() {
                var actualPlayerPosition = game.getActualPlayer().getPosition();
                if (actualPlayerPosition == null) return false;
                return basicTargets.size() == 1 && game.getActualPlayer().getPosition().equals(basicTargets.get(0).getPosition()) &&
                        (!alternativeFire || basicTargetsPoint == null ||
                                Stream.of(Bounds.Direction.values()).anyMatch(e -> game.getActualPlayer()
                                        .isPointAtMaxDistanceInDirection(basicTargetsPoint, game.getCells(), 2, e)));
            }

            @Override
            void basicFireImpl() {
                basicTargets.get(0).takeHits(game, alternativeFire ? 3 : 2, 0);
                if (alternativeFire && basicTargetsPoint != null) basicTargets.get(0).setPosition(basicTargetsPoint);
            }
        }
    }
}