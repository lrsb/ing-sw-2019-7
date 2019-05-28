package it.polimi.ingsw.client.views.gui.boards;

import it.polimi.ingsw.client.views.gui.sprite.Sprite;
import it.polimi.ingsw.client.views.gui.sprite.SpriteBoard;
import it.polimi.ingsw.client.views.gui.sprite.SpriteBoardListener;
import it.polimi.ingsw.client.views.gui.sprite.pointinterpolators.LinearPointInterpolator;
import it.polimi.ingsw.common.models.Game;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class AbstractBoard extends SpriteBoard implements SpriteBoardListener {
    @Nullable GameBoardListener gameBoardListener;
    private @NotNull Game game;

    public AbstractBoard(@NotNull Game game, @Nullable BufferedImage background) {
        super(background);
        this.game = game;
        setBoardListener(this);
    }

    public @NotNull Game getGame() {
        return game;
    }

    public void setGame(@NotNull Game game) throws IOException {
        this.game = game;
    }

    @Override
    public void onSpriteDragged(@NotNull Sprite sprite) {
        if (sprite.getTag() != null) if (sprite.getTag().startsWith("p:")) {
            var ints = sprite.getTag().substring(2).split(",");
            sprite.moveTo(new LinearPointInterpolator(sprite.getPosition(), new Point(Integer.parseInt(ints[0]), Integer.parseInt(ints[1])), 250) {
            });
        }
    }

    public void setBoardListener(@Nullable GameBoardListener boardListener) {
        this.gameBoardListener = boardListener;
    }
}
