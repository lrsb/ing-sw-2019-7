package it.polimi.ingsw.client.views.boards;

import it.polimi.ingsw.client.others.Utils;
import it.polimi.ingsw.client.views.sprite.SpriteBoard;
import it.polimi.ingsw.common.models.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PlayerBoard extends SpriteBoard {
    private @NotNull Player player;

    public PlayerBoard(@NotNull Player player) throws IOException {
        super(Utils.readJpgImage(Player.class, "D-Struct-OrFrontBoard"));
        this.player = player;
    }
}
