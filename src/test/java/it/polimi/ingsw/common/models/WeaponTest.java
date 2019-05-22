package it.polimi.ingsw.common.models;

import it.polimi.ingsw.server.models.GameImpl;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class WeaponTest {
    @Test
    void testAllWeapons() {
        Stream.of(Weapon.Name.values()).forEach(e -> {
            var room = new Room("", new User(""));
            Collections.nCopies(5, null).parallelStream().map(f -> new User(UUID.randomUUID().toString())).collect(Collectors.toList()).forEach(room::addUser);
            var game = GameImpl.Creator.newGame(room);
            game.getPlayers().forEach(f -> f.setPosition(new Point(new SecureRandom().nextInt(3), new SecureRandom().nextInt(3))));
            var weapon = e.build(game, false);
            weapon.addBasicTarget(game.getPlayers().get(3).getUuid());
        });
    }
}