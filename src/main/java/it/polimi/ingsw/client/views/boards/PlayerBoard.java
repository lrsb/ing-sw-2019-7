package it.polimi.ingsw.client.views.boards;

import it.polimi.ingsw.client.views.sprite.Sprite;
import it.polimi.ingsw.common.models.Player;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class PlayerBoard extends AbstractBoard {
    private @NotNull Player player;

    public PlayerBoard(@NotNull Dimension dimension, @NotNull Player player) throws IOException {
        super(dimension);
        this.player = player;
        addSprite(new Sprite(0, 0, new Dimension(width, height), () -> ImageIO.read(Player.class.getResourceAsStream("Player/" + "D-Struct-OrFrontBoard" + ".png"))));
    }
}
