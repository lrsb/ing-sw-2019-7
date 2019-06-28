package it.polimi.ingsw.server.models;

import it.polimi.ingsw.common.models.*;
import it.polimi.ingsw.common.models.exceptions.ActionDeniedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class GameImplTest {
    @Test
    void testRunGame() {
        var room = new Room("", new User(""));
        Collections.nCopies(5, null).parallelStream().map(f -> new User(UUID.randomUUID().toString())).collect(Collectors.toList()).forEach(room::addUser);
        GameImpl gameImplTest = GameImpl.Creator.newGame(room);
        assert (gameImplTest.getPlayers().size() >= 3 && gameImplTest.getPlayers().size() <= 5) : "Wrong number of players";
        for (Player player : gameImplTest.getPlayers()) {
            for (AmmoCard.Color color : AmmoCard.Color.values()) {
                assertEquals(3, player.getColoredCubes(color));
                assertNull(player.getPosition());
                assertEquals(0, player.getPoints());
                assertEquals(0, player.getDamagesTaken().size());
                assertEquals(0, player.getMarksTaken().size());
                assertEquals(player.equals(gameImplTest.getActualPlayer()) ? 2 : 0, player.getPowerUps().size());
                assertEquals(8, player.getMaximumPoints());
            }
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                var cell = gameImplTest.getCell(new Point(i, j));
                assertTrue(cell == null || cell.isSpawnPoint() && cell.getAmmoCard() == null && gameImplTest.getWeapons(cell.getColor()).size() == 3 ||
                        !cell.isSpawnPoint() && cell.getAmmoCard() != null);
            }
        }
    }

    @Test
    void testStartCondition() {
        GameImpl gameImpl = createGameImpl(Game.Type.FIVE_FIVE);
        for (Player player : gameImpl.getPlayers()) {
            assertNull(player.getPosition());
            for (AmmoCard.Color color : AmmoCard.Color.values()) {
                assertEquals(3, player.getColoredCubes(color));
            }
            assertEquals(0, player.getWeaponsSize());
            assertEquals(player.equals(gameImpl.getActualPlayer()) ? 2 : 0, player.getPowerUps().size());
        }
        for (int i = 0; i < gameImpl.getPlayers().size(); i++) {
            assertEquals(gameImpl.getPlayers().get(i), gameImpl.getActualPlayer());
            assertEquals(2, gameImpl.getActualPlayer().getPowerUps().size());
            AmmoCard.Color expectedColor = gameImpl.getActualPlayer().getPowerUps().get(0).getAmmoColor();
            assertTrue(gameImpl.doAction(reborningAction(gameImpl.getActualPlayer().getPowerUps().get(0), gameImpl)));
            assertEquals(1, gameImpl.getActualPlayer().getPowerUps().size());
            assertTrue(gameImpl.getCell(gameImpl.getActualPlayer().getPosition()).isSpawnPoint());
            assertEquals(expectedColor.toString(), gameImpl.getCell(gameImpl.getActualPlayer().getPosition()).getColor().toString());
            assertTrue(gameImpl.doAction(Action.Builder.create(gameImpl.getUuid()).buildNextTurn()));
        }
        //Set posizioni giocatori
        gameImpl.getPlayers().get(0).setPosition(new Point(0, 0));
        gameImpl.getPlayers().get(1).setPosition(new Point(1, 2));
        gameImpl.getPlayers().get(2).setPosition(new Point(2, 2));
        gameImpl.getPlayers().get(3).setPosition(new Point(2, 2));
        gameImpl.getPlayers().get(4).setPosition(new Point(2, 3));
        //tolgo powerUps
        for (Player player : gameImpl.getPlayers()) {
            PowerUp powerUp = player.getPowerUps().get(0);
            player.removePowerUp(powerUp);
            assertEquals(0, player.getPowerUps().size());
        }
        //Turno 1, giocatore 1
        assertEquals(2, gameImpl.getRemainedActions());
        gameImpl.getActualPlayer().addWeapon(Weapon.LOCK_RIFLE);
        ArrayList<UUID> targets = new ArrayList<>();
        targets.add(gameImpl.getPlayers().get(1).getUuid());
        assertTrue(gameImpl.doAction(Action.Builder.create(gameImpl.getUuid()).buildFireAction(Weapon.LOCK_RIFLE, new Point(0, 0),
                null, false, 0, targets, null, new ArrayList<>(),
                null, new ArrayList<>(), null)));
        assertEquals(1, gameImpl.getRemainedActions());
        assertEquals(2, gameImpl.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(1, gameImpl.getPlayers().get(1).getMarksTaken().size());
        assertTrue(gameImpl.getPlayers().get(1).getDamagesTaken().contains(gameImpl.getActualPlayer().getUuid()));
        assertFalse(gameImpl.getActualPlayer().isALoadedGun(Weapon.LOCK_RIFLE));
        assertFalse(gameImpl.doAction(Action.Builder.create(gameImpl.getUuid()).buildReload(Weapon.LOCK_RIFLE, null)));
        assertFalse(gameImpl.doAction(Action.Builder.create(gameImpl.getUuid()).buildAmmoCardGrabAction(new Point(1, 0))));
        assertEquals(1, gameImpl.getRemainedActions());
        assertTrue(gameImpl.doAction(Action.Builder.create(gameImpl.getUuid()).buildMoveAction(new Point(2, 1))));
        assertEquals(new Point(2, 1), gameImpl.getActualPlayer().getPosition());
        assertTrue(gameImpl.doAction(Action.Builder.create(gameImpl.getUuid()).buildReload(Weapon.LOCK_RIFLE, null)));
        assertTrue(gameImpl.getActualPlayer().isALoadedGun(Weapon.LOCK_RIFLE));
        assertEquals(1, gameImpl.getActualPlayer().getColoredCubes(AmmoCard.Color.BLUE));
        assertTrue(gameImpl.doAction(Action.Builder.create(gameImpl.getUuid()).buildNextTurn()));
        //Turno 1, giocatore 2
        assertEquals(gameImpl.getPlayers().get(1), gameImpl.getActualPlayer());
        gameImpl.getActualPlayer().addWeapon(Weapon.ELECTROSCYTHE);
        assertTrue(gameImpl.doAction(Action.Builder.create(gameImpl.getUuid()).buildMoveAction(new Point(2, 2))));
        targets.clear();
        assertTrue(gameImpl.doAction(Action.Builder.create(gameImpl.getUuid()).buildFireAction(Weapon.ELECTROSCYTHE, new Point(2, 2),
                null, true, 0, targets, null, new ArrayList<>(),
                null, new ArrayList<>(), null)));
        assertEquals(2, gameImpl.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(2, gameImpl.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(2, gameImpl.getPlayers().get(3).getDamagesTaken().size());
        assertTrue(gameImpl.getPlayers().get(2).getDamagesTaken().contains(gameImpl.getActualPlayer().getUuid()));
        assertTrue(gameImpl.getPlayers().get(3).getDamagesTaken().contains(gameImpl.getActualPlayer().getUuid()));
        assertTrue(gameImpl.doAction(Action.Builder.create(gameImpl.getUuid()).buildReload(Weapon.ELECTROSCYTHE, null)));
        assertEquals(1, gameImpl.getActualPlayer().getColoredCubes(AmmoCard.Color.BLUE));
        assertEquals(2, gameImpl.getActualPlayer().getColoredCubes(AmmoCard.Color.RED));
    }

    @RepeatedTest(value = 100)
    void testGrabItems() {
        GameImpl game = createGameImpl(Game.Type.FIVE_FIVE);
        game.getActualPlayer().setPosition(new Point(0, 0));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildWeaponGrabAction(new Point(1, 0), game.getWeapons(Cell.Color.RED).get(0), null, null)));
        assertEquals(2, game.getWeapons(Cell.Color.RED).size());
        assertEquals(1, game.getActualPlayer().getWeaponsSize());
        for (AmmoCard.Color color : AmmoCard.Color.values())
            assertEquals(3 - game.getActualPlayer().getWeapons().get(0).getGrabCost(color), game.getActualPlayer().getColoredCubes(color));
        assertEquals(new Point(1, 0), game.getActualPlayer().getPosition());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildWeaponGrabAction(new Point(1, 0), game.getWeapons(Cell.Color.BLUE).get(0), null, null)));
        assertEquals(2, game.getWeapons(Cell.Color.RED).size());
        assertEquals(3, game.getWeapons(Cell.Color.BLUE).size());
        assertEquals(1, game.getActualPlayer().getWeaponsSize());
        assertEquals(new Point(1, 0), game.getActualPlayer().getPosition());
        for (AmmoCard.Color color : AmmoCard.Color.values())
            assertEquals(3 - game.getActualPlayer().getWeapons().get(0).getGrabCost(color), game.getActualPlayer().getColoredCubes(color));
        game.getPlayers().get(1).setPosition(new Point(0, 1));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        assertEquals(3, game.getWeapons(Cell.Color.RED).size());
        assertFalse(game.doAction(Action.Builder.create(game.getUuid()).buildAmmoCardGrabAction(new Point(0, 2))));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildWeaponGrabAction(new Point(0, 2), game.getWeapons(Cell.Color.BLUE).get(0), null, null)));
        for (AmmoCard.Color color: AmmoCard.Color.values())
            game.getActualPlayer().removeColoredCubes(color, game.getActualPlayer().getColoredCubes(color));
        while (!game.getActualPlayer().getPowerUps().isEmpty()) game.getActualPlayer().removePowerUp(game.getActualPlayer().getPowerUps().get(0));
        AmmoCard ammoCard = game.getCell(new Point(0, 1)).getAmmoCard();
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildAmmoCardGrabAction(new Point(0, 1))));
        assertEquals(ammoCard.getType().equals(AmmoCard.Type.POWER_UP) ? 1 : 0, game.getActualPlayer().getPowerUps().size());
        switch (ammoCard.getType()) {
            case POWER_UP:
                assertTrue(game.getActualPlayer().getColoredCubes(ammoCard.getLeft()) == 1 &&
                        game.getActualPlayer().getColoredCubes(ammoCard.getRight()) == 1 ||
                        game.getActualPlayer().getColoredCubes(ammoCard.getLeft()) == 2 &&
                                game.getActualPlayer().getColoredCubes(ammoCard.getRight()) == 2);
                break;
            case RED:
                assertEquals(1, game.getActualPlayer().getColoredCubes(AmmoCard.Color.RED));
                assertEquals(2, game.getActualPlayer().getColoredCubes(ammoCard.getLeft()));
                assertEquals(2, game.getActualPlayer().getColoredCubes(ammoCard.getRight()));
                break;
            case YELLOW:
                assertEquals(1, game.getActualPlayer().getColoredCubes(AmmoCard.Color.YELLOW));
                assertEquals(2, game.getActualPlayer().getColoredCubes(ammoCard.getLeft()));
                assertEquals(2, game.getActualPlayer().getColoredCubes(ammoCard.getRight()));
                break;
            case BLUE:
                assertEquals(1, game.getActualPlayer().getColoredCubes(AmmoCard.Color.BLUE));
                assertEquals(2, game.getActualPlayer().getColoredCubes(ammoCard.getLeft()));
                assertEquals(2, game.getActualPlayer().getColoredCubes(ammoCard.getRight()));
                break;
            default:
                break;
        }
        assertEquals(3, game.getActualPlayer().getPowerUps().size() +
                game.getActualPlayer().getColoredCubes(AmmoCard.Color.RED) +
                game.getActualPlayer().getColoredCubes(AmmoCard.Color.YELLOW) +
                game.getActualPlayer().getColoredCubes(AmmoCard.Color.BLUE));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        assertTrue(game.doAction(reborningAction(game.getActualPlayer().getPowerUps().get(0), game)));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildWeaponGrabAction(game.getActualPlayer().getPosition(),
                game.getWeapons(game.getCell(game.getActualPlayer().getPosition()).getColor()).get(0),
                null, game.getActualPlayer().getPowerUps())));
        assertTrue(game.getActualPlayer().getPowerUps().isEmpty() ||
                game.getActualPlayer().getPowerUps().size() == 1 ||
                game.getActualPlayer().getPowerUps().size() == 2);
        assertTrue(game.getActualPlayer().getPowerUps().parallelStream()
                .allMatch(e -> game.getActualPlayer().getWeapons().get(0).getGrabCost(e.getAmmoColor()) == 0 &&
                        game.getActualPlayer().getColoredCubes(e.getAmmoColor()) == 3));
    }

    @RepeatedTest(value = 100)
    void testPoints() {
        GameImpl game = createGameImpl(Game.Type.SIX_SIX);
        for (Player player : game.getPlayers()) {
            player.setPosition(new Point(2, 3));
            player.addWeapon(Weapon.LOCK_RIFLE);
        }
        //Turno1
        easyFire(game, game.getPlayers().get(1));
        easyFire(game, game.getPlayers().get(4));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        easyFire(game, game.getPlayers().get(3));
        easyFire(game, game.getPlayers().get(3));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        easyFire(game, game.getPlayers().get(0));
        easyFire(game, game.getPlayers().get(4));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        easyFire(game, game.getPlayers().get(2));
        easyFire(game, game.getPlayers().get(1));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        easyFire(game, game.getPlayers().get(3));
        easyFire(game, game.getPlayers().get(0));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        //Riepilogo1
        assertEquals(4, game.getPlayers().get(0).getDamagesTaken().size());
        assertEquals(2, game.getPlayers().get(0).getMarksTaken().size());
        assertEquals(0, game.getPlayers().get(0).getPoints());
        assertEquals(4, game.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(2, game.getPlayers().get(1).getMarksTaken().size());
        assertEquals(0, game.getPlayers().get(1).getPoints());
        assertEquals(2, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(1, game.getPlayers().get(2).getMarksTaken().size());
        assertEquals(0, game.getPlayers().get(2).getPoints());
        assertEquals(7, game.getPlayers().get(3).getDamagesTaken().size());
        assertEquals(2, game.getPlayers().get(3).getMarksTaken().size());
        assertEquals(0, game.getPlayers().get(3).getPoints());
        assertEquals(4, game.getPlayers().get(4).getDamagesTaken().size());
        assertEquals(2, game.getPlayers().get(4).getMarksTaken().size());
        assertEquals(0, game.getPlayers().get(4).getPoints());
        assertEquals(5, game.getSkulls());
        //Turno2
        easyFire(game, game.getPlayers().get(3));
        easyFire(game, game.getPlayers().get(2));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        easyFire(game, game.getPlayers().get(4));
        easyFire(game, game.getPlayers().get(3));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildReborn(
                game.getActualPlayer().getPowerUps().get(0).getType(),
                game.getActualPlayer().getPowerUps().get(0).getAmmoColor())));
        game.getPlayers().get(3).setPosition(new Point(2, 3));
        easyFire(game, game.getPlayers().get(0));
        easyFire(game, game.getPlayers().get(0));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        easyFire(game, game.getPlayers().get(2));
        easyFire(game, game.getPlayers().get(1));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        easyFire(game, game.getPlayers().get(1));
        easyFire(game, game.getPlayers().get(2));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        //Riepilogo2
        assertEquals(10, game.getPlayers().get(0).getDamagesTaken().size());
        assertEquals(2, game.getPlayers().get(0).getMarksTaken().size());
        assertEquals(4, game.getPlayers().get(0).getPoints());
        assertEquals(10, game.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(3, game.getPlayers().get(1).getMarksTaken().size());
        assertEquals(9, game.getPlayers().get(1).getPoints());
        assertEquals(9, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(3, game.getPlayers().get(2).getMarksTaken().size());
        assertEquals(0, game.getPlayers().get(2).getPoints());
        assertEquals(0, game.getPlayers().get(3).getDamagesTaken().size());
        assertEquals(3, game.getPlayers().get(3).getMarksTaken().size());
        assertEquals(0, game.getPlayers().get(3).getPoints());
        assertEquals(6, game.getPlayers().get(4).getDamagesTaken().size());
        assertEquals(3, game.getPlayers().get(4).getMarksTaken().size());
        assertEquals(6, game.getPlayers().get(4).getPoints());
        assertEquals(4, game.getSkulls());
        //Turno 3
        easyFire(game, game.getPlayers().get(4));
        easyFire(game, game.getPlayers().get(4));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildReborn(
                game.getActualPlayer().getPowerUps().get(0).getType(),
                game.getActualPlayer().getPowerUps().get(0).getAmmoColor())));
        game.getPlayers().get(4).setPosition(new Point(2, 3));
        easyFire(game, game.getPlayers().get(2));
        easyFire(game, game.getPlayers().get(0));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildReborn(
                game.getActualPlayer().getPowerUps().get(0).getType(),
                game.getActualPlayer().getPowerUps().get(0).getAmmoColor())));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildReborn(
                game.getActualPlayer().getPowerUps().get(0).getType(),
                game.getActualPlayer().getPowerUps().get(0).getAmmoColor())));
        game.getPlayers().get(0).setPosition(new Point(2, 3));
        game.getPlayers().get(2).setPosition(new Point(2, 3));
        easyFire(game, game.getPlayers().get(3));
        easyFire(game, game.getPlayers().get(3));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        easyFire(game, game.getPlayers().get(1));
        easyFire(game, game.getPlayers().get(1));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildReborn(
                game.getActualPlayer().getPowerUps().get(0).getType(),
                game.getActualPlayer().getPowerUps().get(0).getAmmoColor())));
        game.getPlayers().get(1).setPosition(new Point(2, 3));
        easyFire(game, game.getPlayers().get(1));
        easyFire(game, game.getPlayers().get(2));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        assertTrue(game.isLastTurn());
        //Riepilogo3
        assertEquals(0, game.getPlayers().get(0).getDamagesTaken().size());
        assertEquals(4, game.getPlayers().get(0).getMarksTaken().size());
        assertEquals(26, game.getPlayers().get(0).getPoints());
        assertEquals(2, game.getPlayers().get(0).getMaximumPoints());
        assertEquals(3, game.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(4, game.getPlayers().get(1).getMarksTaken().size());
        assertEquals(20, game.getPlayers().get(1).getPoints());
        assertEquals(2, game.getPlayers().get(1).getMaximumPoints());
        assertEquals(3, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(4, game.getPlayers().get(2).getMarksTaken().size());
        assertEquals(15, game.getPlayers().get(2).getPoints());
        assertEquals(2, game.getPlayers().get(2).getMaximumPoints());
        assertEquals(5, game.getPlayers().get(3).getDamagesTaken().size());
        assertEquals(5, game.getPlayers().get(3).getMarksTaken().size());
        assertEquals(17, game.getPlayers().get(3).getPoints());
        assertEquals(6, game.getPlayers().get(3).getMaximumPoints());
        assertEquals(0, game.getPlayers().get(4).getDamagesTaken().size());
        assertEquals(3, game.getPlayers().get(4).getMarksTaken().size());
        assertEquals(20, game.getPlayers().get(4).getPoints());
        assertEquals(2, game.getPlayers().get(1).getMaximumPoints());
        assertEquals(0, game.getSkulls());
        assertTrue(game.getPlayers().get(0).isEasyBoard());
        assertTrue(game.getPlayers().get(1).isEasyBoard());
        assertTrue(game.getPlayers().get(2).isEasyBoard());
        assertFalse(game.getPlayers().get(3).isEasyBoard());
        assertTrue(game.getPlayers().get(4).isEasyBoard());
        //LastTurn
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        assertEquals(32, game.getPlayers().get(0).getPoints());
        assertEquals(28, game.getPlayers().get(1).getPoints());
        assertEquals(22, game.getPlayers().get(2).getPoints());
        assertEquals(21, game.getPlayers().get(3).getPoints());
        assertEquals(26, game.getPlayers().get(4).getPoints());
        ArrayList<ArrayList<UUID>> ranking = game.getRanking();
        assertEquals(game.getPlayers().get(0).getUuid(), ranking.get(0).get(0));
        assertEquals(game.getPlayers().get(1).getUuid(), ranking.get(1).get(0));
        assertEquals(game.getPlayers().get(4).getUuid(), ranking.get(2).get(0));
        assertEquals(game.getPlayers().get(2).getUuid(), ranking.get(3).get(0));
        assertEquals(game.getPlayers().get(3).getUuid(), ranking.get(4).get(0));
    }

    @Test
    void testTagback() {
        GameImpl game = createGameImpl(Game.Type.SIX_SIX);
        game.getPlayers().get(0).addWeapon(Weapon.LOCK_RIFLE);
        game.getPlayers().get(0).addWeapon(Weapon.MACHINE_GUN);
        game.getPlayers().get(1).addPowerUp(new PowerUp(AmmoCard.Color.RED, PowerUp.Type.TAGBACK_GRENADE));
        game.getPlayers().get(2).addPowerUp(new PowerUp(AmmoCard.Color.YELLOW, PowerUp.Type.TAGBACK_GRENADE));
        for (Player player : game.getPlayers()) {
            player.setPosition(new Point(0, 0));
        }
        ArrayList<UUID> basicTargets = new ArrayList<>();
        ArrayList<UUID> firstTargets = new ArrayList<>();
        ArrayList<UUID> secondTargets = new ArrayList<>();
        Point basicPoint = null;
        Point firstPoint = null;
        Point secondPoint = null;
        basicTargets.add(game.getPlayers().get(1).getUuid());
        firstTargets.add(game.getPlayers().get(2).getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.LOCK_RIFLE, game.getActualPlayer().getPosition(),
                null, false, 0, basicTargets, basicPoint, firstTargets,
                firstPoint, secondTargets, secondPoint)));
        assertEquals(game.getPlayers().get(1), game.getActualPlayer());
        basicTargets.clear();
        firstTargets.clear();
        basicTargets.add(game.getPlayers().get(1).getUuid());
        basicTargets.add(game.getPlayers().get(2).getUuid());
        secondTargets.add(game.getPlayers().get(3).getUuid());
        assertThrows(ActionDeniedException.class, () -> game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.MACHINE_GUN, game.getActualPlayer().getPosition(),
                null, false, 2, basicTargets, basicPoint, firstTargets,
                firstPoint, secondTargets, secondPoint)));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        assertEquals(game.getPlayers().get(0), game.getActualPlayer());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(Weapon.MACHINE_GUN, game.getActualPlayer().getPosition(),
                null, false, 2, basicTargets, basicPoint, firstTargets,
                firstPoint, secondTargets, secondPoint)));
        //per usarlo dovrei riuscire a inserire nell exitCard le carte che voglio
        /*
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildUsePowerUp(PowerUp.Type.TAGBACK_GRENADE, AmmoCard.Color.RED, null, null)));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildUsePowerUp(PowerUp.Type.TAGBACK_GRENADE, AmmoCard.Color.RED, null, null)));
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));
        assertEquals(game.getPlayers().get(1), game.getActualPlayer());
        assertEquals(0, game.getPlayers().get(1).getPowerUps().size());
        assertEquals(0, game.getPlayers().get(2).getPowerUps().size());
        assertEquals(3, game.getPlayers().get(1).getDamagesTaken().size());
        assertEquals(1, game.getPlayers().get(2).getDamagesTaken().size());
        assertEquals(2, game.getPlayers().get(0).getMarksTaken().size());
        assertEquals(1, game.getPlayers().get(1).getMarksTaken().size());
        assertEquals(1, game.getPlayers().get(2).getMarksTaken().size());
        assertTrue(game.getPlayers().get(1).getDamagesTaken().contains(game.getPlayers().get(0).getUuid()));
        assertTrue(game.getPlayers().get(2).getDamagesTaken().contains(game.getPlayers().get(0).getUuid()));
        assertTrue(game.getPlayers().get(0).getMarksTaken().contains(game.getPlayers().get(1).getUuid()));
        assertTrue(game.getPlayers().get(0).getMarksTaken().contains(game.getPlayers().get(2).getUuid()));
        assertTrue(game.getPlayers().get(1).getMarksTaken().contains(game.getPlayers().get(0).getUuid()));
        assertTrue(game.getPlayers().get(2).getMarksTaken().contains(game.getPlayers().get(0).getUuid()));*/
    }

    @Test
    void testPrintBoard() {
        for (Game.Type type : Game.Type.values()) {
            System.out.println(type);
            Game game = createGameImpl(type);
            for (Player player : game.getPlayers()) {
                player.setPosition(new Point(1, 0));
            }
            GameCli gameCli = new GameCli();
            gameCli.buildBoard(game);
            gameCli.game(game);
        }
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

    void cubesTotalRecharging(@NotNull GameImpl game) {
        for (Player player : game.getPlayers()) {
            for (AmmoCard.Color color : AmmoCard.Color.values()) {
                while (player.getColoredCubes(color) < 3) player.removeColoredCubes(color, -1);
                assertEquals(3, player.getColoredCubes(color));
            }
            while (player.getPowerUps().size() > 0) player.removePowerUp(player.getPowerUps().get(0));
            assertEquals(0, player.getPowerUps().size());
            player.removeWeapon(player.getWeapons().get(0));
            player.addWeapon(Weapon.LOCK_RIFLE);
        }

    }

    void easyFire(@NotNull GameImpl game, @NotNull Player target) {
        ArrayList<UUID> basicTarget = new ArrayList<>();
        basicTarget.add(target.getUuid());
        assertTrue(game.doAction(Action.Builder.create(game.getUuid()).buildFireAction(game.getActualPlayer().getWeapons().get(0),
                game.getActualPlayer().getPosition(), null, false, 0, basicTarget,
                null, new ArrayList<>(), null, new ArrayList<>(), null)));
        cubesTotalRecharging(game);
    }

    Action reborningAction(@NotNull PowerUp toThrow, @NotNull GameImpl game) {
        return Action.Builder.create(game.getUuid()).buildReborn(toThrow.getType(), toThrow.getAmmoColor());
    }

    class TypeCell {
        private Character character;
        private Cell.Color color;

        public Cell.Color getColor() {
            return color;
        }

        public void setColor(Cell.Color color) {
            this.color = color;
        }

        public Character getCharacter() {
            return character;
        }

        public void setCharacter(@NotNull Character character) {
            this.character = character;
            this.color = Cell.Color.WHITE;
        }

        public void setAll(@NotNull Character character, @NotNull Cell.Color color) {
            this.color = color;
            this.character = character;
        }

    }

    class GameCli {
        private TypeCell[][] buildBoard(@NotNull Game game) {
            var cells = game.getCells();
            var board = new TypeCell[Game.MAX_Y * 18][Game.MAX_X * 36];
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    var cellCli = buildCell(cells[i][j]);
                    for (int k = 0; k < cellCli.length; k++) {
                        System.arraycopy(cellCli[k], 0, board[(18 * i) + k], (36 * j), cellCli[k].length);
                    }
                }
            }
            return board;
        }

        TypeCell[][] buildCell(@Nullable Cell cell) {
            var cellCli = new TypeCell[18][36];
            for (int i = 0; i < 18; i++) for (int j = 0; j < 36; j++) {
                cellCli[i][j] = new TypeCell();
                cellCli[i][j].setCharacter(' ');
            }
            if (cell != null) {
                var northBound = cell.getBounds().getType(Bounds.Direction.N);
                var southBound = cell.getBounds().getType(Bounds.Direction.S);
                var westBound = cell.getBounds().getType(Bounds.Direction.W);
                var eastBound = cell.getBounds().getType(Bounds.Direction.E);
                switch (northBound) {
                    case SAME_ROOM: {
                        for (int i = 1; i < 35; i++) cellCli[0][i].setAll(' ', cell.getColor());
                        cellCli[0][0].setAll('╔', cell.getColor());
                        cellCli[0][35].setAll('╗', cell.getColor());
                        break;
                    }
                    case DOOR: {
                        for (int i = 1; i < 35; i++) cellCli[0][i].setAll('=', cell.getColor());
                        cellCli[0][0].setAll('╔', cell.getColor());
                        cellCli[0][16].setAll('╡', cell.getColor());
                        cellCli[0][17].setAll(' ', cell.getColor());
                        cellCli[0][18].setAll(' ', cell.getColor());
                        cellCli[0][19].setAll(' ', cell.getColor());
                        cellCli[0][20].setAll('╞', cell.getColor());
                        cellCli[0][35].setAll('╗', cell.getColor());
                        break;
                    }
                    case WALL: {
                        for (int i = 1; i < 35; i++) cellCli[0][i].setAll('=', cell.getColor());
                        cellCli[0][0].setAll('╔', cell.getColor());
                        cellCli[0][35].setAll('╗', cell.getColor());
                        break;
                    }
                }

                switch (southBound) {
                    case SAME_ROOM: {
                        for (int i = 1; i < 35; i++) cellCli[17][i].setAll(' ', cell.getColor());
                        cellCli[17][0].setAll('╚', cell.getColor());
                        cellCli[17][35].setAll('╝', cell.getColor());
                        break;
                    }
                    case DOOR: {
                        for (int i = 1; i < 35; i++) cellCli[17][i].setAll('=', cell.getColor());
                        cellCli[17][0].setAll('╚', cell.getColor());
                        cellCli[17][16].setAll('╡', cell.getColor());
                        cellCli[17][17].setAll(' ', cell.getColor());
                        cellCli[17][18].setAll(' ', cell.getColor());
                        cellCli[17][19].setAll(' ', cell.getColor());
                        cellCli[17][20].setAll('╞', cell.getColor());
                        cellCli[17][35].setAll('╝', cell.getColor());
                        break;
                    }
                    case WALL: {
                        for (int i = 1; i < 35; i++) cellCli[17][i].setAll('=', cell.getColor());
                        cellCli[17][0].setAll('╚', cell.getColor());
                        cellCli[17][35].setAll('╝', cell.getColor());
                        break;
                    }
                }
                switch (eastBound) {
                    case SAME_ROOM: {
                        for (int i = 1; i < 17; i++) cellCli[i][35].setAll(' ', cell.getColor());
                        break;
                    }
                    case DOOR: {
                        for (int i = 1; i < 17; i++) cellCli[i][35].setAll('║', cell.getColor());
                        cellCli[6][35].setAll('╨', cell.getColor());
                        cellCli[7][35].setAll(' ', cell.getColor());
                        cellCli[8][35].setAll(' ', cell.getColor());
                        cellCli[9][35].setAll(' ', cell.getColor());
                        cellCli[10][35].setAll('╥', cell.getColor());
                        break;
                    }
                    case WALL: {
                        for (int i = 1; i < 17; i++) cellCli[i][35].setAll('║', cell.getColor());
                        break;
                    }
                }
                switch (westBound) {
                    case SAME_ROOM: {
                        for (int i = 1; i < 17; i++) cellCli[i][0].setAll(' ', cell.getColor());
                        break;
                    }
                    case DOOR: {
                        for (int i = 1; i < 17; i++) cellCli[i][0].setAll('║', cell.getColor());
                        cellCli[6][0].setAll('╨', cell.getColor());
                        cellCli[7][0].setAll(' ', cell.getColor());
                        cellCli[8][0].setAll(' ', cell.getColor());
                        cellCli[9][0].setAll(' ', cell.getColor());
                        cellCli[10][0].setAll('╥', cell.getColor());
                        break;
                    }
                    case WALL: {
                        for (int i = 1; i < 17; i++) cellCli[i][0].setAll('║', cell.getColor());
                        break;
                    }
                }
                if (cell.isSpawnPoint()) {
                    cellCli[2][2].setCharacter('S');
                    cellCli[2][4].setCharacter('P');
                } else {
                    if (cell.getAmmoCard() != null) {
                        switch (cell.getAmmoCard().getLeft()) {
                            case RED: {
                                cellCli[3][3].setAll('R', Cell.Color.RED);
                                break;
                            }
                            case BLUE: {
                                cellCli[3][3].setAll('B', Cell.Color.BLUE);
                                break;
                            }
                            case YELLOW: {
                                cellCli[3][3].setAll('Y', Cell.Color.YELLOW);
                                break;
                            }
                        }
                    }
                    if (cell.getAmmoCard() != null) {
                        switch (cell.getAmmoCard().getType()) {
                            case RED: {
                                cellCli[2][4].setAll('R', Cell.Color.RED);
                                break;
                            }
                            case BLUE: {
                                cellCli[2][4].setAll('B', Cell.Color.BLUE);
                                break;
                            }
                            case YELLOW: {
                                cellCli[2][4].setAll('Y', Cell.Color.YELLOW);
                                break;
                            }
                            case POWER_UP: {
                                cellCli[2][4].setAll('P', Cell.Color.WHITE);
                                break;
                            }
                        }
                    }
                    if (cell.getAmmoCard() != null) {
                        switch (cell.getAmmoCard().getRight()) {
                            case RED: {
                                cellCli[3][5].setAll('R', Cell.Color.RED);
                                break;
                            }
                            case BLUE: {
                                cellCli[3][5].setAll('B', Cell.Color.BLUE);
                                break;
                            }
                            case YELLOW: {
                                cellCli[3][5].setAll('Y', Cell.Color.YELLOW);
                                break;
                            }
                        }
                    }
                }
            }
            return cellCli;
        }

        void game(Game game) {
            var board = buildBoard(game); // come prendo game?
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    System.out.print(board[i][j].getColor().escape());
                    System.out.print(board[i][j].getCharacter());
                }
                System.out.println();
            }
            return;
        }
    }
}
