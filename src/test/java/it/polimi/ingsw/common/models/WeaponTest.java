package it.polimi.ingsw.common.models;

import it.polimi.ingsw.server.models.GameImpl;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

class WeaponTest {
    @Test
    void testAllWeapons() {
        for (var weaponName : Weapon.Name.values()) {
            var users = Collections.nCopies(5, (Player) null).parallelStream().map(e -> new User(UUID.randomUUID().toString())).collect(Collectors.toList());
            var game = GameImpl.Creator.newGame(UUID.randomUUID(), users);
            game.getPlayers().forEach(e -> e.setPosition(new Point(new SecureRandom().nextInt(3), new SecureRandom().nextInt(3))));
            var weapon = weaponName.build(game, false);
            weapon.addBasicTarget(game.getPlayers().get(3));
            weapon.basicFire();
        }
    }
}