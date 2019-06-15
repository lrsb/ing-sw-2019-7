package it.polimi.ingsw.common.models;

import org.junit.jupiter.api.RepeatedTest;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ActionTest {

    @RepeatedTest(value = 100)
    void testCreateAction() {
        int genericSize;
        Random rand = new Random();
        Action.Type actionTypeTest = Action.Type.values()[rand.nextInt(Action.Type.values().length)];
        Game gameTest = new Game();
        Weapon weaponTest = Weapon.values()[rand.nextInt(Weapon.values().length)];
        Weapon discardedWeaponTest;
        do {
            discardedWeaponTest = Weapon.values()[rand.nextInt(Weapon.values().length)];
        } while(weaponTest.name().equals(discardedWeaponTest.name()));
        Point destinationTest = new Point(rand.nextInt(5), rand.nextInt(4));
        PowerUp.Type powerUpTypeTest = PowerUp.Type.values()[rand.nextInt(PowerUp.Type.values().length)];
        AmmoCard.Color colorTest = AmmoCard.Color.values()[rand.nextInt(AmmoCard.Color.values().length)];
        ArrayList<PowerUp> powerUpPaymentTest = new ArrayList<>();
        genericSize = rand.nextInt(4);
        for (int i = 0; i < genericSize; i++)
            powerUpPaymentTest.add(new PowerUp(AmmoCard.Color.values()[rand.nextInt(AmmoCard.Color.values().length)],
                    PowerUp.Type.values()[rand.nextInt(PowerUp.Type.values().length)]));
        boolean alternativeFireTest = rand.nextBoolean();
        int optionsTest = rand.nextInt(4);
        ArrayList<UUID> basicTargetTest = new ArrayList<>();
        genericSize = rand.nextInt(5);
        for (int i = 0; i < genericSize; i++) basicTargetTest.add(UUID.randomUUID());
        Point basicTargetPointTest = new Point(rand.nextInt(5), rand.nextInt(4));
        ArrayList<UUID> firstAdditionalTargetTest = new ArrayList<>();
        genericSize = rand.nextInt(4);
        for (int i = 0; i < genericSize; i++) firstAdditionalTargetTest.add(UUID.randomUUID());
        Point firstAdditionalTargetPointTest = new Point(rand.nextInt(5), rand.nextInt(4));
        ArrayList<UUID> secondAdditionalTargetTest = new ArrayList<>();
        genericSize = rand.nextInt(4);
        for (int i = 0; i < genericSize; i++) firstAdditionalTargetTest.add(UUID.randomUUID());
        Point secondAdditionalTargetPointTest = new Point(rand.nextInt(5), rand.nextInt(4));
        UUID targetTest = UUID.randomUUID();
        Action.Builder builderTest = Action.Builder.create(gameTest.uuid);
        Action actionTest;
        switch (actionTypeTest) {
            case MOVE:
                actionTest = builderTest.buildMoveAction(destinationTest);
                assertEquals(destinationTest, actionTest.getDestination());
                break;
            case GRAB_AMMOCARD:
                actionTest = builderTest.buildAmmoCardGrabAction(destinationTest);
                assertEquals(destinationTest, actionTest.getDestination());
                break;
            case GRAB_WEAPON:
                actionTest = builderTest.buildWeaponGrabAction(destinationTest, weaponTest, discardedWeaponTest, powerUpPaymentTest);
                assertEquals(destinationTest, actionTest.getDestination());
                assertEquals(weaponTest, actionTest.getWeapon());
                assertEquals(discardedWeaponTest, actionTest.getDiscardedWeapon());
                assertNotEquals(null, actionTest.getPowerUpPayment());
                assertEquals(powerUpPaymentTest.size(), actionTest.getPowerUpPayment().size());
                genericSize = 0;
                while (powerUpPaymentTest.size() > 0) {
                    assert actionTest.getPowerUpPayment().contains(powerUpPaymentTest.get(0));
                    powerUpPaymentTest.remove(0);
                    genericSize++;
                }
                assertEquals(genericSize, actionTest.getPowerUpPayment().size());
                break;
            case FIRE:
                actionTest = builderTest.buildFireAction(weaponTest, destinationTest, powerUpPaymentTest, alternativeFireTest,
                        optionsTest, basicTargetTest, basicTargetPointTest, firstAdditionalTargetTest, firstAdditionalTargetPointTest,
                        secondAdditionalTargetTest, secondAdditionalTargetPointTest);
                assertEquals(weaponTest, actionTest.getWeapon());
                assertEquals(destinationTest, actionTest.getDestination());
                assertEquals(powerUpPaymentTest.size(), actionTest.getPowerUpPayment().size());
                genericSize = 0;
                while (powerUpPaymentTest.size() > 0) {
                    assert actionTest.getPowerUpPayment().contains(powerUpPaymentTest.get(0));
                    powerUpPaymentTest.remove(0);
                    genericSize++;
                }
                assertEquals(genericSize, actionTest.getPowerUpPayment().size());
                assertEquals(alternativeFireTest, actionTest.getAlternativeFire());
                assertEquals(optionsTest, actionTest.getOptions());
                genericSize = 0;
                while (basicTargetTest.size() > 0) {
                    assert actionTest.getBasicTarget().contains(basicTargetTest.get(0));
                    basicTargetTest.remove(0);
                    genericSize++;
                }
                assertEquals(genericSize, actionTest.getBasicTarget().size());
                assertEquals(basicTargetPointTest, actionTest.getBasicTargetPoint());
                genericSize = 0;
                while (firstAdditionalTargetTest.size() > 0) {
                    assert actionTest.getFirstAdditionalTarget().contains(firstAdditionalTargetTest.get(0));
                    firstAdditionalTargetTest.remove(0);
                    genericSize++;
                }
                assertEquals(genericSize, actionTest.getFirstAdditionalTarget().size());
                assertEquals(firstAdditionalTargetPointTest, actionTest.getFirstAdditionalTargetPoint());
                genericSize = 0;
                while (secondAdditionalTargetTest.size() > 0) {
                    assert actionTest.getSecondAdditionalTarget().contains(secondAdditionalTargetTest.get(0));
                    secondAdditionalTargetTest.remove(0);
                    genericSize++;
                }
                assertEquals(genericSize, actionTest.getSecondAdditionalTarget().size());
                assertEquals(secondAdditionalTargetPointTest, actionTest.getSecondAdditionalTargetPoint());
                break;
            case USE_POWER_UP:
                actionTest = builderTest.buildUsePowerUp(powerUpTypeTest, colorTest, destinationTest, targetTest);
                assertEquals(powerUpTypeTest, actionTest.getPowerUpType());
                assertEquals(colorTest, actionTest.getColor());
                assertEquals(destinationTest, actionTest.getDestination());
                assertEquals(targetTest, actionTest.getTarget());
                break;
            case RELOAD:
                actionTest = builderTest.buildReload(weaponTest, powerUpPaymentTest);
                assertEquals(weaponTest, actionTest.getWeapon());
                assertEquals(powerUpPaymentTest.size(), actionTest.getPowerUpPayment().size());
                genericSize = 0;
                while (powerUpPaymentTest.size() > 0) {
                    assert actionTest.getPowerUpPayment().contains(powerUpPaymentTest.get(0));
                    powerUpPaymentTest.remove(0);
                    genericSize++;
                }
                assertEquals(genericSize, actionTest.getPowerUpPayment().size());
                break;
            case REBORN:
                actionTest = builderTest.buildReborn(powerUpTypeTest, colorTest);
                assertEquals(powerUpTypeTest, actionTest.getPowerUpType());
                assertEquals(colorTest, actionTest.getColor());
                break;
            case NEXT_TURN:
                actionTest = builderTest.buildNextTurn();
                break;
        }
    }
}
