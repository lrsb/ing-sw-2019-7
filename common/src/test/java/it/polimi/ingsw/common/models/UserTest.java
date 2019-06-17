package it.polimi.ingsw.common.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserTest {
    @Test
    void testUser() {
        ArrayList<String> nickNames = new ArrayList<>();
        nickNames.add("Fede");
        nickNames.add("Lore");
        nickNames.add("Tia");
        nickNames.add("Cugola");
        nickNames.add("Vulcan Raven");
        ArrayList<User.Auth> auths = new ArrayList<>();
        nickNames.parallelStream().forEach(e -> auths.add(new User.Auth(new User(e), e.toLowerCase())));
        for (User.Auth auth : auths) {
            assertNotNull(auth.getToken());
            assertNotNull(auth.getUuid());
            assertNotNull(auth.getNickname());
        }
    }
}
