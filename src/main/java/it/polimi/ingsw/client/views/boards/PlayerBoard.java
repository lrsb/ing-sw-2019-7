package it.polimi.ingsw.client.views.boards;

import it.polimi.ingsw.client.others.Utils;
import it.polimi.ingsw.client.views.sprite.Sprite;
import it.polimi.ingsw.common.models.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;

/**
 * The type Player board.
 */
public class PlayerBoard extends AbstractBoard {
    private @NotNull Player player;

    /**
     * Instantiates a new Player board.
     *
     * @param dimension the dimension
     * @param player    the player
     * @throws IOException the io exception
     */
    public PlayerBoard(@NotNull Dimension dimension, @NotNull Player player) throws IOException {
        super(dimension, Utils.readJpgImage(Player.class, "D-Struct-OrFrontBoard"));
        this.player = player;
    }

    @Override
    public void onSpriteClicked(@NotNull Sprite sprite) {

    }

    @Override
    public void onSpriteDragged(@NotNull Sprite sprite) {

    }
}
