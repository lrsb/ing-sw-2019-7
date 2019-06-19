package it.polimi.ingsw.server.models;

import it.polimi.ingsw.common.models.*;
import it.polimi.ingsw.server.models.exceptions.CardNotFoundException;
import org.jetbrains.annotations.NotNull;
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

    @Test
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

    @Test
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
        basicTargets.add(game.getPlayers().get(4).getUuid());
        basicTargets.add(game.getPlayers().get(3).getUuid());
        firstTargets.add(game.getPlayers().get(3).getUuid());
        game.getActualPlayer().addPowerUp(new PowerUp(AmmoCard.Color.YELLOW, PowerUp.Type.TELEPORTER));
        assertThrows(CardNotFoundException.class, () -> game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.MACHINE_GUN, game.getActualPlayer().getPosition(),
                game.getActualPlayer().getPowerUps(), false, 1, basicTargets, null, firstTargets, null,
                secondTargets, null)));
        basicTargets.clear();
        firstTargets.clear();
        secondTargets.clear();
    }

    @Test
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
}