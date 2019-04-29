package it.polimi.ingsw.common.models;

import it.polimi.ingsw.server.models.GameImpl;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

class WeaponTest {
    @Test
    void testAllWeapons() {
        for (var weapon : Weapon.Name.values()) {
            var users = Collections.nCopies(5, (Player) null).parallelStream().map(e -> new User(UUID.randomUUID().toString())).collect(Collectors.toList());
            var game = GameImpl.Creator.newGame(UUID.randomUUID(), users);
            weapon.build(game, false);//.basicFire();
        }
    }
}