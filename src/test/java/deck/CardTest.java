package deck;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CardTest {

    @Test
    public void testConstructor() {

        Card c1;

        c1 = new HumanCard();
        assertTrue(c1 instanceof HumanCard);

        c1 = new AlienCard();
        assertTrue(c1 instanceof AlienCard);

    }

}
