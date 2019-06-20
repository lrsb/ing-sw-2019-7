package it.polimi.ingsw.server.models;

import it.polimi.ingsw.common.models.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class WeaponTest {
    @Test
    void testAllWeapons() {
        Stream.of(Weapon.values()).forEach(e -> {
            var room = new Room("", new User(""));
            Collections.nCopies(5, null).parallelStream().map(f -> new User(UUID.randomUUID().toString())).collect(Collectors.toList()).forEach(room::addUser);
            var game = GameImpl.Creator.newGame(room);
            game.getPlayers().forEach(f -> f.setPosition(new Point(new SecureRandom().nextInt(3), new SecureRandom().nextInt(3))));
            var weapon = WeaponImpl.Loader.build(e, game, false);
            weapon.addBasicTarget(game.getPlayers().get(3).getUuid());
        });
    }

    @RepeatedTest(value = 100)
    void testLockRifle() {
        GameImpl game = createGameImpl(Game.Type.FIVE_FIVE);
        for (Player player : game.getPlayers()) {
            while(player.getPowerUps().size() > 0) player.removePowerUp(player.getPowerUps().get(0));
        }
        ArrayList<UUID> basicTargets = new ArrayList<>();
        ArrayList<UUID> firstTargets = new ArrayList<>();
        ArrayList<UUID> secondTargets = new ArrayList<>();
        game.getPlayers().forEach(e -> e.setPosition(new Point(0, 0)));
        game.getPlayers().forEach(e -> e.addWeapon(Weapon.LOCK_RIFLE));
        //Turno 1, giocatore 1
        basicTargets.add(game.getPlayers().get(1).getUuid());
        firstTargets.add(game.getPlayers().get(2).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.LOCK_RIFLE, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, null, firstTargets, null,
                secondTargets, null)));
        assertEquals(2, game.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(1, game.getPlayers().get(1).getMarksTaken().size());
        assertEquals(0, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(2).getMarksTaken().size());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildMoveAction(new Point(2, 1))));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildReload(Weapon.LOCK_RIFLE, null)));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        basicTargets.clear();
        firstTargets.clear();
        //Turno 1, giocatore 2
        basicTargets.add(game.getPlayers().get(0).getUuid());
        firstTargets.add(game.getPlayers().get(3).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.LOCK_RIFLE, game.getActualPlayer().getPosition(),
                null, false, 1, basicTargets, null, firstTargets, null,
                secondTargets, null)));
        basicTargets.clear();
        basicTargets.add(game.getPlayers().get(2).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.LOCK_RIFLE, game.getActualPlayer().getPosition(),
                null, true, 1, basicTargets, null, firstTargets, null,
                secondTargets, null)));
        assertEquals(2, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(1, game.getPlayers().get(2).getMarksTaken().size());
        assertEquals(0, game.getPlayers().get(3).getDamagesTaken().size());
        assertEquals(1, game.getPlayers().get(3).getMarksTaken().size());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildMoveAction(new Point(1, 2))));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildReload(Weapon.LOCK_RIFLE, null)));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        //Turno 1, giocatore 3
        basicTargets.clear();
        firstTargets.clear();
        basicTargets.add(game.getPlayers().get(3).getUuid());
        firstTargets.add(game.getPlayers().get(4).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.LOCK_RIFLE, game.getActualPlayer().getPosition(),
                null, true, 2, basicTargets, null, firstTargets, null,
                secondTargets, null)));
        assertEquals(2, game.getPlayers().get(3).getDamagesTaken().size());
        assertEquals(2, game.getPlayers().get(3).getMarksTaken().size());
        assertEquals(0, game.getPlayers().get(4).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(4).getMarksTaken().size());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildMoveAction(new Point(1, 1))));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildReload(Weapon.LOCK_RIFLE, null)));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        basicTargets.clear();
        firstTargets.clear();
        //Turno 1, giocatore 4
        basicTargets.add(game.getPlayers().get(4).getUuid());
        firstTargets.add(game.getPlayers().get(0).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.LOCK_RIFLE, game.getActualPlayer().getPosition(),
                null, false, 3, basicTargets, null, firstTargets, null,
                secondTargets, null)));
        firstTargets.clear();
        firstTargets.add(game.getPlayers().get(1).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.LOCK_RIFLE, game.getActualPlayer().getPosition(),
                null, false, 3, basicTargets, null, firstTargets, null,
                secondTargets, null)));
        assertEquals(2, game.getPlayers().get(4).getDamagesTaken().size());
        assertEquals(1, game.getPlayers().get(4).getMarksTaken().size());
        assertEquals(2, game.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(2, game.getPlayers().get(1).getMarksTaken().size());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildMoveAction(new Point(1, 1))));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildReload(Weapon.LOCK_RIFLE, null)));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        basicTargets.clear();
        firstTargets.clear();
        //Turno 1, giocatore 5
        basicTargets.add(game.getPlayers().get(4).getUuid());
        firstTargets.add(game.getPlayers().get(3).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.LOCK_RIFLE, game.getActualPlayer().getPosition(),
                null, false, 1, basicTargets, null, firstTargets, null,
                secondTargets, null)));
        basicTargets.clear();
        basicTargets.add(game.getPlayers().get(3).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.LOCK_RIFLE, game.getActualPlayer().getPosition(),
                null, false, 1, basicTargets, null, firstTargets, null,
                secondTargets, null)));
        basicTargets.clear();
        basicTargets.add(game.getPlayers().get(1).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.LOCK_RIFLE, game.getActualPlayer().getPosition(),
                null, false, 1, basicTargets, null, firstTargets, null,
                secondTargets, null)));
        assertEquals(4, game.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(3, game.getPlayers().get(1).getMarksTaken().size());
        assertEquals(2, game.getPlayers().get(3).getDamagesTaken().size());
        assertEquals(3, game.getPlayers().get(3).getMarksTaken().size());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildMoveAction(new Point(1, 1))));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildReload(Weapon.LOCK_RIFLE, null)));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
    }

    @RepeatedTest(value = 100)
    void testMachineGun() {
        GameImpl game = createGameImpl(Game.Type.FIVE_SIX);
        for (Player player : game.getPlayers()) {
            while(player.getPowerUps().size() > 0) player.removePowerUp(player.getPowerUps().get(0));
        }
        ArrayList<UUID> basicTargets = new ArrayList<>();
        ArrayList<UUID> firstTargets = new ArrayList<>();
        ArrayList<UUID> secondTargets = new ArrayList<>();
        game.getPlayers().forEach(e -> e.setPosition(new Point(0, 3)));
        game.getPlayers().forEach(e -> e.addWeapon(Weapon.MACHINE_GUN));
        //Turno 1, giocatore 1
        basicTargets.add(game.getPlayers().get(1).getUuid());
        basicTargets.add(game.getPlayers().get(2).getUuid());
        firstTargets.add(game.getPlayers().get(1).getUuid());
        secondTargets.add(game.getPlayers().get(2).getUuid());
        secondTargets.add(game.getPlayers().get(3).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.MACHINE_GUN, game.getActualPlayer().getPosition(),
                null, false, 3, basicTargets, null, firstTargets, null,
                secondTargets, null)));
        assertEquals(2, game.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(1).getMarksTaken().size());
        assertEquals(2, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(2).getMarksTaken().size());
        assertEquals(1, game.getPlayers().get(3).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(3).getMarksTaken().size());
        assertEquals(2, game.getActualPlayer().getColoredCubes(AmmoCard.Color.YELLOW));
        assertEquals(2, game.getActualPlayer().getColoredCubes(AmmoCard.Color.BLUE));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildMoveAction(new Point(2, 2))));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildReload(Weapon.MACHINE_GUN, null)));
        assertEquals(2, game.getActualPlayer().getColoredCubes(AmmoCard.Color.RED));
        assertEquals(1, game.getActualPlayer().getColoredCubes(AmmoCard.Color.BLUE));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        basicTargets.clear();
        firstTargets.clear();
        secondTargets.clear();
        //Turno 1, giocatore 2
        basicTargets.add(game.getPlayers().get(1).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.MACHINE_GUN, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, null, firstTargets, null,
                secondTargets, null)));
        basicTargets.clear();
        basicTargets.add(game.getPlayers().get(3).getUuid());
        basicTargets.add(game.getPlayers().get(3).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.MACHINE_GUN, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, null, firstTargets, null,
                secondTargets, null)));
        assertEquals(2, game.getPlayers().get(3).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(3).getMarksTaken().size());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildMoveAction(new Point(2, 3))));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildReload(Weapon.MACHINE_GUN, null)));
        assertEquals(2, game.getActualPlayer().getColoredCubes(AmmoCard.Color.RED));
        assertEquals(2, game.getActualPlayer().getColoredCubes(AmmoCard.Color.BLUE));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        basicTargets.clear();
        firstTargets.clear();
        secondTargets.clear();
        //Turno 1, giocatore 3
        basicTargets.add(game.getPlayers().get(4).getUuid());
        basicTargets.add(game.getPlayers().get(3).getUuid());
        firstTargets.add(game.getPlayers().get(2).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.MACHINE_GUN, game.getActualPlayer().getPosition(),
                null, false, 1, basicTargets, null, firstTargets, null,
                secondTargets, null)));
        basicTargets.clear();
        firstTargets.clear();
    }

    @RepeatedTest(value = 100)
    void testThor() {
        GameImpl game = createGameImpl(Game.Type.SIX_FIVE);
        for (Player player : game.getPlayers())
            while(player.getPowerUps().size() > 0) player.removePowerUp(player.getPowerUps().get(0));
        ArrayList<UUID> basicTargets = new ArrayList<>();
        ArrayList<UUID> firstTargets = new ArrayList<>();
        ArrayList<UUID> secondTargets = new ArrayList<>();
        game.getPlayers().forEach(e -> e.setPosition(new Point(2, 1)));
        game.getPlayers().forEach(e -> e.addWeapon(Weapon.THOR));
        //Turno 1, Giocatore 1
        game.getPlayers().get(1).setPosition(new Point(1, 2));
        game.getPlayers().get(2).setPosition(new Point(0, 1));
        game.getPlayers().get(3).setPosition(new Point(1, 0));
        basicTargets.add(game.getPlayers().get(1).getUuid());
        firstTargets.add(game.getPlayers().get(2).getUuid());
        secondTargets.add(game.getPlayers().get(3).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.THOR, game.getActualPlayer().getPosition(),
                null, false, 3, basicTargets, null, firstTargets, null,
                secondTargets, null)));
        assertEquals(2, game.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(1).getMarksTaken().size());
        assertEquals(1, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(2).getMarksTaken().size());
        assertEquals(2, game.getPlayers().get(3).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(3).getMarksTaken().size());
        assertEquals(1, game.getActualPlayer().getColoredCubes(AmmoCard.Color.BLUE));
    }

    @RepeatedTest(value = 100)
    void testPlasmaGun() {
        GameImpl game = createGameImpl(Game.Type.SIX_SIX);
        for (Player player : game.getPlayers())
            while(player.getPowerUps().size() > 0) player.removePowerUp(player.getPowerUps().get(0));
        ArrayList<UUID> basicTargets = new ArrayList<>();
        ArrayList<UUID> firstTargets = new ArrayList<>();
        ArrayList<UUID> secondTargets = new ArrayList<>();
        Point basicTargetPoint = null;
        Point firstTargetPoint = null;
        Point secondTargetPoint = null;
        game.getPlayers().get(0).setPosition(new Point(1, 2));
        game.getPlayers().get(1).setPosition(new Point(0, 1));
        game.getPlayers().get(2).setPosition(new Point(1, 1));
        game.getPlayers().get(3).setPosition(new Point(1, 2));
        game.getPlayers().get(4).setPosition(new Point(1, 2));
        game.getPlayers().forEach(e -> e.addWeapon(Weapon.PLASMA_GUN));
        //Turno 1, G0
        basicTargets.add(game.getPlayers().get(2).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.PLASMA_GUN, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(new Point(1, 2), game.getActualPlayer().getPosition());
        basicTargetPoint = new Point(0, 1);
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.PLASMA_GUN, game.getActualPlayer().getPosition(),
                null, false, 3, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(2, game.getActualPlayer().getColoredCubes(AmmoCard.Color.BLUE));
        assertEquals(3, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(basicTargetPoint, game.getActualPlayer().getPosition());
        rechargingPlayers(game);
        basicTargets.clear();
        basicTargets.add(game.getPlayers().get(3).getUuid());
        basicTargetPoint = new Point(0, 0);
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.PLASMA_GUN, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.clear();
        basicTargets.add(game.getPlayers().get(2).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.PLASMA_GUN, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(basicTargetPoint, game.getActualPlayer().getPosition());
    }

    @RepeatedTest(value = 100)
    void testWhisper() {
        GameImpl game = createGameImpl(Game.Type.FIVE_FIVE);
        for (Player player : game.getPlayers())
            while(player.getPowerUps().size() > 0) player.removePowerUp(player.getPowerUps().get(0));
        ArrayList<UUID> basicTargets = new ArrayList<>();
        ArrayList<UUID> firstTargets = new ArrayList<>();
        ArrayList<UUID> secondTargets = new ArrayList<>();
        Point basicTargetPoint = null;
        Point firstTargetPoint = null;
        Point secondTargetPoint = null;
        game.getPlayers().get(0).setPosition(new Point(1, 0));
        game.getPlayers().get(1).setPosition(new Point(0, 0));
        game.getPlayers().get(2).setPosition(new Point(0, 1));
        game.getPlayers().get(3).setPosition(new Point(1, 1));
        game.getPlayers().get(4).setPosition(new Point(1, 2));
        game.getPlayers().forEach(e -> e.addWeapon(Weapon.WHISPER));
        //Turno 1, G0
        basicTargets.add(game.getPlayers().get(1).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.WHISPER, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.clear();
        basicTargets.add(game.getPlayers().get(2).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.WHISPER, game.getActualPlayer().getPosition(),
                null, false, 1, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(3, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(1, game.getPlayers().get(2).getMarksTaken().size());
        rechargingPlayers(game);
        basicTargets.clear();
        basicTargets.add(game.getPlayers().get(3).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.WHISPER, game.getActualPlayer().getPosition(),
                null, false, 2, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.clear();
        basicTargets.add(game.getPlayers().get(4).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.WHISPER, game.getActualPlayer().getPosition(),
                null, false, 3, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(3, game.getPlayers().get(4).getDamagesTaken().size());
        assertEquals(1, game.getPlayers().get(4).getMarksTaken().size());
    }

    @RepeatedTest(value = 100)
    void testElectroscythe() {
        GameImpl game = createGameImpl(Game.Type.FIVE_SIX);
        for (Player player : game.getPlayers())
            while(player.getPowerUps().size() > 0) player.removePowerUp(player.getPowerUps().get(0));
        ArrayList<UUID> basicTargets = new ArrayList<>();
        ArrayList<UUID> firstTargets = new ArrayList<>();
        ArrayList<UUID> secondTargets = new ArrayList<>();
        Point basicTargetPoint = null;
        Point firstTargetPoint = null;
        Point secondTargetPoint = null;
        game.getPlayers().get(0).setPosition(new Point(0, 3));
        game.getPlayers().get(1).setPosition(new Point(0, 3));
        game.getPlayers().get(2).setPosition(new Point(0, 3));
        game.getPlayers().get(3).setPosition(new Point(0, 3));
        game.getPlayers().get(4).setPosition(new Point(0, 3));
        game.getPlayers().forEach(e -> e.addWeapon(Weapon.ELECTROSCYTHE));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.ELECTROSCYTHE, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertTrue(game.getPlayers().parallelStream().filter(e -> !e.equals(game.getActualPlayer()))
                .allMatch(e -> e.getDamagesTaken().size() == 1));
        rechargingPlayers(game);
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.ELECTROSCYTHE, game.getActualPlayer().getPosition(),
                null, true, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertTrue(game.getPlayers().parallelStream().filter(e -> !e.equals(game.getActualPlayer()))
                .allMatch(e -> e.getDamagesTaken().size() == 3));
        assertEquals(2, game.getActualPlayer().getColoredCubes(AmmoCard.Color.RED));
        assertEquals(2, game.getActualPlayer().getColoredCubes(AmmoCard.Color.BLUE));
        assertEquals(3, game.getActualPlayer().getColoredCubes(AmmoCard.Color.YELLOW));
    }

    @RepeatedTest(value = 100)
    void testTractorBeam() {
        GameImpl game = createGameImpl(Game.Type.SIX_FIVE);
        for (Player player : game.getPlayers())
            while(player.getPowerUps().size() > 0) player.removePowerUp(player.getPowerUps().get(0));
        ArrayList<UUID> basicTargets = new ArrayList<>();
        ArrayList<UUID> firstTargets = new ArrayList<>();
        ArrayList<UUID> secondTargets = new ArrayList<>();
        Point basicTargetPoint = null;
        Point firstTargetPoint = null;
        Point secondTargetPoint = null;
        game.getPlayers().get(0).setPosition(new Point(2, 3));
        game.getPlayers().get(1).setPosition(new Point(1, 3));
        game.getPlayers().get(2).setPosition(new Point(1, 2));
        game.getPlayers().get(3).setPosition(new Point(0, 1));
        game.getPlayers().get(4).setPosition(new Point(1, 1));
        game.getPlayers().forEach(e -> e.addWeapon(Weapon.TRACTOR_BEAM));
        basicTargets.add(game.getPlayers().get(1).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.TRACTOR_BEAM, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(new Point(1, 3), game.getPlayers().get(1).getPosition());
        assertEquals(1, game.getPlayers().get(1).getDamagesTaken().size());
        basicTargets.clear();
        rechargingPlayers(game);
        basicTargets.add(game.getPlayers().get(3).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.TRACTOR_BEAM, game.getActualPlayer().getPosition(),
                null, true, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.clear();
        basicTargets.add(game.getPlayers().get(2).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.TRACTOR_BEAM, game.getActualPlayer().getPosition(),
                null, true, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(3, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(game.getActualPlayer().getPosition(), game.getPlayers().get(2).getPosition());
        assertEquals(2, game.getActualPlayer().getColoredCubes(AmmoCard.Color.RED));
        assertEquals(2, game.getActualPlayer().getColoredCubes(AmmoCard.Color.YELLOW));
        assertEquals(3, game.getActualPlayer().getColoredCubes(AmmoCard.Color.BLUE));
        rechargingPlayers(game);
        basicTargets.clear();
    }

    @RepeatedTest(value = 100)
    void testVortexCannon() {
        GameImpl game = createGameImpl(Game.Type.SIX_SIX);
        for (Player player : game.getPlayers())
            while(player.getPowerUps().size() > 0) player.removePowerUp(player.getPowerUps().get(0));
        ArrayList<UUID> basicTargets = new ArrayList<>();
        ArrayList<UUID> firstTargets = new ArrayList<>();
        ArrayList<UUID> secondTargets = new ArrayList<>();
        Point basicTargetPoint = null;
        Point firstTargetPoint = null;
        Point secondTargetPoint = null;
        game.getPlayers().get(0).setPosition(new Point(2, 3));
        game.getPlayers().get(1).setPosition(new Point(1, 3));
        game.getPlayers().get(2).setPosition(new Point(0, 2));
        game.getPlayers().get(3).setPosition(new Point(2, 2));
        game.getPlayers().get(4).setPosition(new Point(1, 2));
        game.getPlayers().forEach(e -> e.addWeapon(Weapon.VORTEX_CANNON));
        basicTargets.add(game.getPlayers().get(1).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.VORTEX_CANNON, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargetPoint = new Point(0, 1);
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.VORTEX_CANNON, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargetPoint = new Point(1, 2);
        firstTargets.add(game.getPlayers().get(1).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.VORTEX_CANNON, game.getActualPlayer().getPosition(),
                null, false, 1, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        firstTargets.clear();
        firstTargets.add(game.getPlayers().get(2).getUuid());
        firstTargets.add(game.getPlayers().get(3).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.VORTEX_CANNON, game.getActualPlayer().getPosition(),
                null, false, 1, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(basicTargetPoint, game.getPlayers().get(1).getPosition());
        assertEquals(basicTargetPoint, game.getPlayers().get(2).getPosition());
        assertEquals(basicTargetPoint, game.getPlayers().get(3).getPosition());
        assertEquals(2, game.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(1, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(1, game.getPlayers().get(3).getDamagesTaken().size());
        assertEquals(2, game.getActualPlayer().getColoredCubes(AmmoCard.Color.RED));
    }

    @RepeatedTest(value = 100)
    void testFurnace() {
        GameImpl game = createGameImpl(Game.Type.FIVE_SIX);
        for (Player player : game.getPlayers())
            while(player.getPowerUps().size() > 0) player.removePowerUp(player.getPowerUps().get(0));
        ArrayList<UUID> basicTargets = new ArrayList<>();
        ArrayList<UUID> firstTargets = new ArrayList<>();
        ArrayList<UUID> secondTargets = new ArrayList<>();
        Point basicTargetPoint = null;
        Point firstTargetPoint = null;
        Point secondTargetPoint = null;
        game.getPlayers().get(0).setPosition(new Point(1, 0));
        game.getPlayers().get(1).setPosition(new Point(0, 2));
        game.getPlayers().get(2).setPosition(new Point(0, 1));
        game.getPlayers().get(3).setPosition(new Point(1, 1));
        game.getPlayers().get(4).setPosition(new Point(1, 1));
        game.getPlayers().forEach(e -> e.addWeapon(Weapon.FURNACE));
        basicTargetPoint = new Point(2, 0);
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.FURNACE, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargetPoint = new Point(1, 1);
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.FURNACE, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.FURNACE, game.getActualPlayer().getPosition(),
                null, true, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(1, game.getPlayers().get(3).getDamagesTaken().size());
        assertEquals(1, game.getPlayers().get(4).getDamagesTaken().size());
        assertEquals(1, game.getPlayers().get(3).getMarksTaken().size());
        assertEquals(1, game.getPlayers().get(4).getMarksTaken().size());
        rechargingPlayers(game);
        basicTargetPoint = new Point(0, 0);
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.FURNACE, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(1, game.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(1, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(1).getMarksTaken().size());
        assertEquals(0, game.getPlayers().get(2).getMarksTaken().size());
    }

    @RepeatedTest(value = 100)
    void testHeatseeker() {
        GameImpl game = createGameImpl(Game.Type.SIX_FIVE);
        for (Player player : game.getPlayers())
            while(player.getPowerUps().size() > 0) player.removePowerUp(player.getPowerUps().get(0));
        ArrayList<UUID> basicTargets = new ArrayList<>();
        ArrayList<UUID> firstTargets = new ArrayList<>();
        ArrayList<UUID> secondTargets = new ArrayList<>();
        Point basicTargetPoint = null;
        Point firstTargetPoint = null;
        Point secondTargetPoint = null;
        game.getPlayers().get(0).setPosition(new Point(1, 0));
        game.getPlayers().get(1).setPosition(new Point(2, 1));
        game.getPlayers().get(2).setPosition(new Point(0, 1));
        game.getPlayers().get(3).setPosition(new Point(0, 0));
        game.getPlayers().get(4).setPosition(new Point(1, 1));
        game.getPlayers().forEach(e -> e.addWeapon(Weapon.HEATSEEKER));
        basicTargets.add(game.getPlayers().get(1).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.HEATSEEKER, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(0, game.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(1).getMarksTaken().size());
        basicTargets.clear();
        basicTargets.add(game.getPlayers().get(2).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.HEATSEEKER, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(3, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(2).getMarksTaken().size());
        basicTargets.clear();
        rechargingPlayers(game);
        basicTargets.add(game.getPlayers().get(3).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.HEATSEEKER, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(0, game.getPlayers().get(3).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(3).getMarksTaken().size());
        basicTargets.clear();
        basicTargets.add(game.getPlayers().get(4).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.HEATSEEKER, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(3, game.getPlayers().get(4).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(4).getMarksTaken().size());
        basicTargets.clear();
    }

    @RepeatedTest(value = 100)
    void testHellion() {
        GameImpl game = createGameImpl(Game.Type.SIX_SIX);
        for (Player player : game.getPlayers())
            while(player.getPowerUps().size() > 0) player.removePowerUp(player.getPowerUps().get(0));
        ArrayList<UUID> basicTargets = new ArrayList<>();
        ArrayList<UUID> firstTargets = new ArrayList<>();
        ArrayList<UUID> secondTargets = new ArrayList<>();
        Point basicTargetPoint = null;
        Point firstTargetPoint = null;
        Point secondTargetPoint = null;
        game.getPlayers().get(0).setPosition(new Point(0, 0));
        game.getPlayers().get(1).setPosition(new Point(0, 0));
        game.getPlayers().get(2).setPosition(new Point(1, 0));
        game.getPlayers().get(3).setPosition(new Point(1, 2));
        game.getPlayers().get(4).setPosition(new Point(0, 2));
        game.getPlayers().forEach(e -> e.addWeapon(Weapon.HELLION));
        basicTargets.add(game.getPlayers().get(1).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.HELLION, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.clear();
        assertEquals(0, game.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(1).getMarksTaken().size());
        basicTargets.add(game.getPlayers().get(2).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.HELLION, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.clear();
        assertEquals(1, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(1, game.getPlayers().get(2).getMarksTaken().size());
        rechargingPlayers(game);
        basicTargets.add(game.getPlayers().get(3).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.HELLION, game.getActualPlayer().getPosition(),
                null, true, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.clear();
        assertEquals(0, game.getPlayers().get(3).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(3).getMarksTaken().size());
        basicTargets.add(game.getPlayers().get(4).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.HELLION, game.getActualPlayer().getPosition(),
                null, true, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.clear();
        assertEquals(1, game.getPlayers().get(4).getDamagesTaken().size());
        assertEquals(2, game.getPlayers().get(4).getMarksTaken().size());
        assertEquals(2, game.getActualPlayer().getColoredCubes(AmmoCard.Color.RED));
    }

    @RepeatedTest(value = 100)
    void testFlamethrower() {
        GameImpl game = createGameImpl(Game.Type.FIVE_FIVE);
        for (Player player : game.getPlayers())
            while(player.getPowerUps().size() > 0) player.removePowerUp(player.getPowerUps().get(0));
        ArrayList<UUID> basicTargets = new ArrayList<>();
        ArrayList<UUID> firstTargets = new ArrayList<>();
        ArrayList<UUID> secondTargets = new ArrayList<>();
        Point basicTargetPoint = null;
        Point firstTargetPoint = null;
        Point secondTargetPoint = null;
        game.getPlayers().get(0).setPosition(new Point(1, 1));
        game.getPlayers().get(1).setPosition(new Point(1, 2));
        game.getPlayers().get(2).setPosition(new Point(1, 3));
        game.getPlayers().get(3).setPosition(new Point(2, 2));
        game.getPlayers().get(4).setPosition(new Point(1, 2));
        game.getPlayers().forEach(e -> e.addWeapon(Weapon.FLAMETHROWER));
        basicTargets.add(game.getPlayers().get(1).getUuid());
        basicTargets.add(game.getPlayers().get(3).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.FLAMETHROWER, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.FLAMETHROWER, game.getActualPlayer().getPosition(),
                null, true, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.clear();
        basicTargets.add(game.getPlayers().get(1).getUuid());
        basicTargets.add(game.getPlayers().get(2).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.FLAMETHROWER, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(1, game.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(1, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(1).getMarksTaken().size());
        assertEquals(0, game.getPlayers().get(2).getMarksTaken().size());
        basicTargets.clear();
        rechargingPlayers(game);
        basicTargets.add(game.getPlayers().get(3).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.FLAMETHROWER, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.clear();
        basicTargetPoint = new Point(1, 3);
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.FLAMETHROWER, game.getActualPlayer().getPosition(),
                null, true, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(3, game.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(2, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(2, game.getPlayers().get(4).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(1).getMarksTaken().size());
        assertEquals(0, game.getPlayers().get(2).getMarksTaken().size());
        assertEquals(0, game.getPlayers().get(4).getMarksTaken().size());
        assertEquals(1, game.getActualPlayer().getColoredCubes(AmmoCard.Color.YELLOW));
    }

    @RepeatedTest(value = 100)
    void testGrenadeLauncher() {
        GameImpl game = createGameImpl(Game.Type.FIVE_SIX);
        for (Player player : game.getPlayers())
            while(player.getPowerUps().size() > 0) player.removePowerUp(player.getPowerUps().get(0));
        ArrayList<UUID> basicTargets = new ArrayList<>();
        ArrayList<UUID> firstTargets = new ArrayList<>();
        ArrayList<UUID> secondTargets = new ArrayList<>();
        Point basicTargetPoint = null;
        Point firstTargetPoint = null;
        Point secondTargetPoint = null;
        game.getPlayers().get(0).setPosition(new Point(1, 1));
        game.getPlayers().get(1).setPosition(new Point(1, 1));
        game.getPlayers().get(2).setPosition(new Point(0, 1));
        game.getPlayers().get(3).setPosition(new Point(1, 0));
        game.getPlayers().get(4).setPosition(new Point(1, 0));
        game.getPlayers().forEach(e -> e.addWeapon(Weapon.GRENADE_LAUNCHER));
        basicTargets.add(game.getPlayers().get(1).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.GRENADE_LAUNCHER, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(1, game.getPlayers().get(1).getDamagesTaken().size());
        basicTargets.clear();
        rechargingPlayers(game);
        basicTargets.add(game.getPlayers().get(2).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.GRENADE_LAUNCHER, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.clear();
        basicTargets.add(game.getPlayers().get(1).getUuid());
        basicTargetPoint = new Point(1, 0);
        firstTargetPoint = new Point(1, 0);
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.GRENADE_LAUNCHER, game.getActualPlayer().getPosition(),
                null, false, 1, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(0, game.getPlayers().get(0).getDamagesTaken().size());
        assertEquals(3, game.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(1, game.getPlayers().get(3).getDamagesTaken().size());
        assertEquals(1, game.getPlayers().get(4).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(0).getMarksTaken().size());
        assertEquals(0, game.getPlayers().get(1).getMarksTaken().size());
        assertEquals(0, game.getPlayers().get(2).getMarksTaken().size());
        assertEquals(0, game.getPlayers().get(3).getMarksTaken().size());
        assertEquals(0, game.getPlayers().get(4).getMarksTaken().size());
        assertEquals(2, game.getActualPlayer().getColoredCubes(AmmoCard.Color.RED));
    }

    @RepeatedTest(value = 100)
    void testRocketLauncher() {
        GameImpl game = createGameImpl(Game.Type.SIX_SIX);
        for (Player player : game.getPlayers())
            while(player.getPowerUps().size() > 0) player.removePowerUp(player.getPowerUps().get(0));
        ArrayList<UUID> basicTargets = new ArrayList<>();
        ArrayList<UUID> firstTargets = new ArrayList<>();
        ArrayList<UUID> secondTargets = new ArrayList<>();
        Point basicTargetPoint = null;
        Point firstTargetPoint = null;
        Point secondTargetPoint = null;
        game.getPlayers().get(0).setPosition(new Point(1, 0));
        game.getPlayers().get(1).setPosition(new Point(1, 1));
        game.getPlayers().get(2).setPosition(new Point(1, 3));
        game.getPlayers().get(3).setPosition(new Point(0, 0));
        game.getPlayers().get(4).setPosition(new Point(1, 0));
        game.getPlayers().forEach(e -> e.addWeapon(Weapon.ROCKET_LAUNCHER));
        basicTargets.add(game.getPlayers().get(4).getUuid());
        basicTargetPoint = new Point(0, 0);
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.ROCKET_LAUNCHER, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        firstTargetPoint = new Point(2, 0);
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.ROCKET_LAUNCHER, game.getActualPlayer().getPosition(),
                null, false, 1, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.clear();
        basicTargetPoint = null;
        firstTargetPoint = null;
        rechargingPlayers(game);
        assertEquals(new Point(2, 0), game.getPlayers().get(0).getPosition());
        assertEquals(new Point(0, 0), game.getPlayers().get(4).getPosition());
        assertEquals(2, game.getPlayers().get(4).getDamagesTaken().size());
        basicTargets.add(game.getPlayers().get(3).getUuid());
        basicTargetPoint = new Point(0, 1);
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.ROCKET_LAUNCHER, game.getActualPlayer().getPosition(),
                null, false, 2, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(0, game.getPlayers().get(0).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(3, game.getPlayers().get(3).getDamagesTaken().size());
        assertEquals(3, game.getPlayers().get(4).getDamagesTaken().size());
        assertEquals(basicTargetPoint, game.getPlayers().get(3).getPosition());
    }

    @RepeatedTest(value = 100)
    void testRailgun() {
        GameImpl game = createGameImpl(Game.Type.SIX_FIVE);
        for (Player player : game.getPlayers())
            while(player.getPowerUps().size() > 0) player.removePowerUp(player.getPowerUps().get(0));
        ArrayList<UUID> basicTargets = new ArrayList<>();
        ArrayList<UUID> firstTargets = new ArrayList<>();
        ArrayList<UUID> secondTargets = new ArrayList<>();
        Point basicTargetPoint = null;
        Point firstTargetPoint = null;
        Point secondTargetPoint = null;
        game.getPlayers().get(0).setPosition(new Point(1, 0));
        game.getPlayers().get(1).setPosition(new Point(1, 1));
        game.getPlayers().get(2).setPosition(new Point(1, 3));
        game.getPlayers().get(3).setPosition(new Point(0, 0));
        game.getPlayers().get(4).setPosition(new Point(2, 3));
        game.getPlayers().forEach(e -> e.addWeapon(Weapon.RAILGUN));
        basicTargets.add(game.getPlayers().get(4).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.RAILGUN, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.RAILGUN, game.getActualPlayer().getPosition(),
                null, true, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.add(game.getPlayers().get(1).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.RAILGUN, game.getActualPlayer().getPosition(),
                null, true, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.clear();
        basicTargets.add(game.getPlayers().get(1).getUuid());
        basicTargets.add(game.getPlayers().get(2).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.RAILGUN, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.RAILGUN, game.getActualPlayer().getPosition(),
                null, true, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(2, game.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(2, game.getPlayers().get(2).getDamagesTaken().size());
        rechargingPlayers(game);
        basicTargets.clear();
        basicTargets.add(game.getPlayers().get(3).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.RAILGUN, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(3, game.getPlayers().get(3).getDamagesTaken().size());
    }

    @RepeatedTest(value = 100)
    void testCyberblade() {
        GameImpl game = createGameImpl(Game.Type.SIX_SIX);
        for (Player player : game.getPlayers())
            while(player.getPowerUps().size() > 0) player.removePowerUp(player.getPowerUps().get(0));
        ArrayList<UUID> basicTargets = new ArrayList<>();
        ArrayList<UUID> firstTargets = new ArrayList<>();
        ArrayList<UUID> secondTargets = new ArrayList<>();
        Point basicTargetPoint = null;
        Point firstTargetPoint = null;
        Point secondTargetPoint = null;
        game.getPlayers().get(0).setPosition(new Point(1, 2));
        game.getPlayers().get(1).setPosition(new Point(1, 2));
        game.getPlayers().get(2).setPosition(new Point(0, 3));
        game.getPlayers().get(3).setPosition(new Point(0, 2));
        game.getPlayers().get(4).setPosition(new Point(0, 2));
        game.getPlayers().forEach(e -> e.addWeapon(Weapon.CYBERBLADE));
        basicTargets.add(game.getPlayers().get(2).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.CYBERBLADE, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.clear();
        basicTargets.add(game.getPlayers().get(3).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.CYBERBLADE, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargetPoint = new Point(0, 2);
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.CYBERBLADE, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(2, game.getPlayers().get(3).getDamagesTaken().size());
        assertEquals(basicTargetPoint, game.getActualPlayer().getPosition());
        rechargingPlayers(game);
        basicTargets.clear();
        basicTargets.add(game.getPlayers().get(3).getUuid());
        basicTargetPoint = new Point(0, 3);
        secondTargets.add(game.getPlayers().get(4).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.CYBERBLADE, game.getActualPlayer().getPosition(),
                null, false, 2, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(4, game.getPlayers().get(3).getDamagesTaken().size());
        assertEquals(2, game.getPlayers().get(4).getDamagesTaken().size());
        assertEquals(basicTargetPoint, game.getActualPlayer().getPosition());
        assertEquals(2, game.getActualPlayer().getColoredCubes(AmmoCard.Color.YELLOW));
    }

    @RepeatedTest(value = 100)
    void testZX2() {
        GameImpl game = createGameImpl(Game.Type.FIVE_FIVE);
        for (Player player : game.getPlayers())
            while(player.getPowerUps().size() > 0) player.removePowerUp(player.getPowerUps().get(0));
        ArrayList<UUID> basicTargets = new ArrayList<>();
        ArrayList<UUID> firstTargets = new ArrayList<>();
        ArrayList<UUID> secondTargets = new ArrayList<>();
        Point basicTargetPoint = null;
        Point firstTargetPoint = null;
        Point secondTargetPoint = null;
        game.getPlayers().get(0).setPosition(new Point(2, 1));
        game.getPlayers().get(1).setPosition(new Point(1, 2));
        game.getPlayers().get(2).setPosition(new Point(2, 2));
        game.getPlayers().get(3).setPosition(new Point(1, 0));
        game.getPlayers().get(4).setPosition(new Point(0, 2));
        game.getPlayers().forEach(e -> e.addWeapon(Weapon.ZX2));
        basicTargets.add(game.getPlayers().get(4).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.ZX2, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.clear();
        basicTargets.add(game.getPlayers().get(3).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.ZX2, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(1, game.getPlayers().get(3).getDamagesTaken().size());
        assertEquals(2, game.getPlayers().get(3).getMarksTaken().size());
        rechargingPlayers(game);
        basicTargets.clear();
        basicTargets.add(game.getPlayers().get(1).getUuid());
        basicTargets.add(game.getPlayers().get(2).getUuid());
        basicTargets.add(game.getPlayers().get(3).getUuid());
        basicTargets.add(game.getPlayers().get(4).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.ZX2, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.ZX2, game.getActualPlayer().getPosition(),
                null, true, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.remove(game.getPlayers().get(4).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.ZX2, game.getActualPlayer().getPosition(),
                null, true, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(0, game.getPlayers().get(0).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(0).getMarksTaken().size());
        assertEquals(0, game.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(1, game.getPlayers().get(1).getMarksTaken().size());
        assertEquals(0, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(1, game.getPlayers().get(2).getMarksTaken().size());
        assertEquals(1, game.getPlayers().get(3).getDamagesTaken().size());
        assertEquals(3, game.getPlayers().get(3).getMarksTaken().size());
        assertEquals(0, game.getPlayers().get(4).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(4).getMarksTaken().size());
    }

    @RepeatedTest(value = 100)
    void testShotgun() {
        GameImpl game = createGameImpl(Game.Type.FIVE_SIX);
        for (Player player : game.getPlayers())
            while(player.getPowerUps().size() > 0) player.removePowerUp(player.getPowerUps().get(0));
        ArrayList<UUID> basicTargets = new ArrayList<>();
        ArrayList<UUID> firstTargets = new ArrayList<>();
        ArrayList<UUID> secondTargets = new ArrayList<>();
        Point basicTargetPoint = null;
        Point firstTargetPoint = null;
        Point secondTargetPoint = null;
        game.getPlayers().get(0).setPosition(new Point(0, 3));
        game.getPlayers().get(1).setPosition(new Point(0, 2));
        game.getPlayers().get(2).setPosition(new Point(0, 3));
        game.getPlayers().get(3).setPosition(new Point(2, 3));
        game.getPlayers().get(4).setPosition(new Point(1, 1));
        game.getPlayers().forEach(e -> e.addWeapon(Weapon.SHOTGUN));
        basicTargets.add(game.getPlayers().get(4).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.SHOTGUN, game.getActualPlayer().getPosition(),
                null, true, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.clear();
        basicTargets.add(game.getPlayers().get(1).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.SHOTGUN, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.SHOTGUN, game.getActualPlayer().getPosition(),
                null, true, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(2, game.getPlayers().get(1).getDamagesTaken().size());
        basicTargets.clear();
        rechargingPlayers(game);
        basicTargets.add(game.getPlayers().get(2).getUuid());
        basicTargetPoint = new Point(0, 2);
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.SHOTGUN, game.getActualPlayer().getPosition(),
                null, true, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.SHOTGUN, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(3, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(basicTargetPoint, game.getPlayers().get(2).getPosition());
    }

    @RepeatedTest(value = 100)
    void testPowerGlove() {
        GameImpl game = createGameImpl(Game.Type.SIX_SIX);
        for (Player player : game.getPlayers())
            while(player.getPowerUps().size() > 0) player.removePowerUp(player.getPowerUps().get(0));
        ArrayList<UUID> basicTargets = new ArrayList<>();
        ArrayList<UUID> firstTargets = new ArrayList<>();
        ArrayList<UUID> secondTargets = new ArrayList<>();
        Point basicTargetPoint = null;
        Point firstTargetPoint = null;
        Point secondTargetPoint = null;
        game.getPlayers().get(0).setPosition(new Point(2, 2));
        game.getPlayers().get(1).setPosition(new Point(2, 2));
        game.getPlayers().get(2).setPosition(new Point(0, 3));
        game.getPlayers().get(3).setPosition(new Point(0, 2));
        game.getPlayers().get(4).setPosition(new Point(1, 2));
        game.getPlayers().forEach(e -> e.addWeapon(Weapon.POWER_GLOVE));
        basicTargets.add(game.getPlayers().get(2).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.POWER_GLOVE, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.clear();
        basicTargets.add(game.getPlayers().get(1).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.POWER_GLOVE, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.clear();
        basicTargets.add(game.getPlayers().get(3).getUuid());
        basicTargets.add(game.getPlayers().get(4).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.POWER_GLOVE, game.getActualPlayer().getPosition(),
                null, true, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargetPoint = new Point(0, 2);
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.POWER_GLOVE, game.getActualPlayer().getPosition(),
                null, true, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.clear();
        rechargingPlayers(game);
        basicTargetPoint = null;
        basicTargets.add(game.getPlayers().get(2).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.POWER_GLOVE, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(0, game.getPlayers().get(0).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(0).getMarksTaken().size());
        assertEquals(0, game.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(1).getMarksTaken().size());
        assertEquals(1, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(2, game.getPlayers().get(2).getMarksTaken().size());
        assertEquals(2, game.getPlayers().get(3).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(3).getMarksTaken().size());
        assertEquals(2, game.getPlayers().get(4).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(4).getMarksTaken().size());
        assertEquals(new Point(0, 3), game.getPlayers().get(0).getPosition());
        assertEquals(new Point(2, 2), game.getPlayers().get(1).getPosition());
        assertEquals(new Point(0, 3), game.getPlayers().get(2).getPosition());
        assertEquals(new Point(0, 2), game.getPlayers().get(3).getPosition());
        assertEquals(new Point(1, 2), game.getPlayers().get(4).getPosition());
    }

    @RepeatedTest(value = 100)
    void testShockwave() {
        GameImpl game = createGameImpl(Game.Type.SIX_FIVE);
        for (Player player : game.getPlayers())
            while(player.getPowerUps().size() > 0) player.removePowerUp(player.getPowerUps().get(0));
        ArrayList<UUID> basicTargets = new ArrayList<>();
        ArrayList<UUID> firstTargets = new ArrayList<>();
        ArrayList<UUID> secondTargets = new ArrayList<>();
        Point basicTargetPoint = null;
        Point firstTargetPoint = null;
        Point secondTargetPoint = null;
        game.getPlayers().get(0).setPosition(new Point(2, 1));
        game.getPlayers().get(1).setPosition(new Point(2, 0));
        game.getPlayers().get(2).setPosition(new Point(1, 1));
        game.getPlayers().get(3).setPosition(new Point(2, 2));
        game.getPlayers().get(4).setPosition(new Point(2, 1));
        game.getPlayers().forEach(e -> e.addWeapon(Weapon.SHOCKWAVE));
        basicTargets.add(game.getPlayers().get(1).getUuid());
        basicTargets.add(game.getPlayers().get(2).getUuid());
        basicTargets.add(game.getPlayers().get(4).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.SHOCKWAVE, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.remove(game.getPlayers().get(4).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.SHOCKWAVE, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(0, game.getPlayers().get(0).getDamagesTaken().size());
        assertEquals(1, game.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(1, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(3).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(4).getDamagesTaken().size());
        rechargingPlayers(game);
        basicTargets.clear();
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.SHOCKWAVE, game.getActualPlayer().getPosition(),
                null, true, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(0, game.getPlayers().get(0).getDamagesTaken().size());
        assertEquals(2, game.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(2, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(1, game.getPlayers().get(3).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(4).getDamagesTaken().size());
        assertEquals(2, game.getPlayers().get(0).getColoredCubes(AmmoCard.Color.YELLOW));
    }

    @RepeatedTest(value = 100)
    void testSledgehammer() {
        GameImpl game = createGameImpl(Game.Type.SIX_SIX);
        for (Player player : game.getPlayers())
            while(player.getPowerUps().size() > 0) player.removePowerUp(player.getPowerUps().get(0));
        ArrayList<UUID> basicTargets = new ArrayList<>();
        ArrayList<UUID> firstTargets = new ArrayList<>();
        ArrayList<UUID> secondTargets = new ArrayList<>();
        Point basicTargetPoint = null;
        Point firstTargetPoint = null;
        Point secondTargetPoint = null;
        game.getPlayers().get(0).setPosition(new Point(2, 1));
        game.getPlayers().get(1).setPosition(new Point(2, 0));
        game.getPlayers().get(2).setPosition(new Point(1, 1));
        game.getPlayers().get(3).setPosition(new Point(2, 1));
        game.getPlayers().get(4).setPosition(new Point(2, 3));
        game.getPlayers().forEach(e -> e.addWeapon(Weapon.SLEDGEHAMMER));
        basicTargets.add(game.getPlayers().get(1).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.SLEDGEHAMMER, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.clear();
        basicTargetPoint = new Point(2, 0);
        basicTargets.add(game.getPlayers().get(2).getUuid());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.SLEDGEHAMMER, game.getActualPlayer().getPosition(),
                null, true, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargets.clear();
        basicTargetPoint = null;
        basicTargets.add(game.getPlayers().get(3).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.SLEDGEHAMMER, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        rechargingPlayers(game);
        assertEquals(2, game.getPlayers().get(3).getDamagesTaken().size());
        basicTargetPoint = new Point(1, 0);
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.SLEDGEHAMMER, game.getActualPlayer().getPosition(),
                null, true, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        basicTargetPoint = new Point(2, 3);
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.SLEDGEHAMMER, game.getActualPlayer().getPosition(),
                null, true, 0, basicTargets, basicTargetPoint, firstTargets, firstTargetPoint,
                secondTargets, secondTargetPoint)));
        assertEquals(0, game.getPlayers().get(0).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(5, game.getPlayers().get(3).getDamagesTaken().size());
        assertEquals(0, game.getPlayers().get(4).getDamagesTaken().size());
        assertEquals(basicTargetPoint, game.getPlayers().get(3).getPosition());
    }

    @Test
    GameImpl createGameImpl(@NotNull Game.Type type) {
        String gameName = "NomePartita";
        User creator = new User("God");
        ArrayList<User> possibleUserPlayer = new ArrayList<>();
        possibleUserPlayer.add(new User("Luca"));
        possibleUserPlayer.add(new User("Federico"));
        possibleUserPlayer.add(new User("Lore"));
        possibleUserPlayer.add(new User("Tia"));
        Room room = new Room(gameName, creator);
        room.setGameType(type);
        room.setSkulls(5);
        while (room.getUsers().size() < 5) room.addUser(possibleUserPlayer.get(room.getUsers().size() - 1));
        return GameImpl.Creator.newGame(room);
    }

    void rechargingPlayers(@NotNull GameImpl game) {
        game.getPlayers().forEach(e -> {
            for (AmmoCard.Color color : AmmoCard.Color.values())
                while(e.getColoredCubes(color) < 3) e.removeColoredCubes(color, -1);
            e.reloadWeapon(e.getWeapons().get(0));
        });
    }
}