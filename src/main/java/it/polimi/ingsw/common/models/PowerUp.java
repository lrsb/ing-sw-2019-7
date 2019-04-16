package it.polimi.ingsw.common.models;

import it.polimi.ingsw.client.views.sprite.Displayable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PowerUp implements Displayable {
    private @NotNull AmmoCard.Color ammoColor;
    private @NotNull Type type;
    private @Nullable BufferedImage bufferedImage;

    @Contract(pure = true)
    public PowerUp(@NotNull AmmoCard.Color ammoColor, @NotNull Type type) {
        this.ammoColor = ammoColor;
        this.type = type;
    }

    @Contract(pure = true)
    public @NotNull AmmoCard.Color getAmmoColor() {
        return ammoColor;
    }

    @Contract(pure = true)
    public @NotNull Type getType() {
        return type;
    }

    @Override
    public @NotNull BufferedImage getImage() throws IOException {
        if (bufferedImage == null)
            bufferedImage = ImageIO.read(AmmoCard.class.getResourceAsStream("PowerUp/" + type.name().substring(0, 3) +
                    ammoColor.name().substring(0, 1) + ".png"));
        return bufferedImage;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PowerUp) return ammoColor == ((PowerUp) obj).ammoColor && type == ((PowerUp) obj).type;
        else return false;
    }

    enum Type {
        TARGETING_SCOPE, NEWTON, TAGBACK_GRANADE, TELEPORTER
    }
}