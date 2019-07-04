package it.polimi.ingsw.common.models;

import it.polimi.ingsw.common.others.Displayable;
import it.polimi.ingsw.common.others.Utils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static it.polimi.ingsw.common.models.AmmoCard.Color.*;

public enum Weapon implements Displayable {
    LOCK_RIFLE(BLUE, new int[]{0, 0, 1}), MACHINE_GUN(BLUE, new int[]{1, 0, 0}),
    THOR(BLUE, new int[]{1, 0, 0}), PLASMA_GUN(BLUE, new int[]{0, 1, 0}),
    WHISPER(BLUE, new int[]{0, 1, 1}), ELECTROSCYTHE(BLUE, new int[]{0, 0, 0}),
    TRACTOR_BEAM(BLUE, new int[]{0, 0, 0}), VORTEX_CANNON(RED, new int[]{0, 0, 1}),
    FURNACE(RED, new int[]{0, 0, 1}), HEATSEEKER(RED, new int[]{1, 1, 0}),
    HELLION(RED, new int[]{0, 1, 0}), FLAMETHROWER(RED, new int[]{0, 0, 0}),
    GRENADE_LAUNCHER(RED, new int[]{0, 0, 0}), ROCKET_LAUNCHER(RED, new int[]{1, 0, 0}),
    RAILGUN(YELLOW, new int[]{0, 1, 1}), CYBERBLADE(YELLOW, new int[]{1, 0, 0}),
    ZX2(YELLOW, new int[]{1, 0, 0}), SHOTGUN(YELLOW, new int[]{0, 1, 0}),
    POWER_GLOVE(YELLOW, new int[]{0, 0, 1}), SHOCKWAVE(YELLOW, new int[]{0, 0, 0}),
    SLEDGEHAMMER(YELLOW, new int[]{0, 0, 0});

    private final @NotNull AmmoCard.Color color;
    private final @NotNull int[] grabCost;

    @Contract(pure = true)
    Weapon(@NotNull AmmoCard.Color color, @NotNull int[] grabCost) {
        this.color = color;
        this.grabCost = grabCost;
    }

    /**
     *
     * @return the color of the weapon
     */
    @Contract(pure = true)
    public @NotNull AmmoCard.Color getColor() {
        return color;
    }

    /**
     *
     * @param color one of the cubes color
     * @return the cost to grab this weapon relatives to @color
     */
    @Contract(pure = true)
    public int getGrabCost(@NotNull AmmoCard.Color color) {
        return grabCost[color.getIndex()];
    }

    @Override
    public @NotNull BufferedImage getFrontImage() throws IOException {
        return Utils.readPngImage(Weapon.class, toString());
    }

    @Override
    public @NotNull BufferedImage getBackImage() throws IOException {
        return Utils.readPngImage(Weapon.class, "back");
    }
}