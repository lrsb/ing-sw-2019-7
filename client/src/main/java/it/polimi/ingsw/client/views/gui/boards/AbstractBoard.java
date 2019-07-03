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
import java.util.List;

public abstract class AbstractBoard extends SpriteBoard implements SpriteBoardListener {
    private @Nullable GameBoardListener gameBoardListener;
    private @NotNull Game game;

    protected AbstractBoard(@NotNull Game game, @Nullable BufferedImage background) {
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
            var tag = sprite.getTag();
            if (tag.contains(";")) tag = tag.split(";")[0];
            var ints = tag.substring(2).split(",");
            sprite.moveTo(new LinearPointInterpolator(sprite.getPosition(), new Point(Integer.parseInt(ints[0]), Integer.parseInt(ints[1])), 250) {
            });
        }
    }

    @Override
    public void onSpriteClicked(@NotNull Sprite sprite) {
    }

    @Override
    public void onSpriteHovered(@NotNull List<Sprite> sprites) {
    }

    @Override
    public void onBoardClicked(@NotNull Point point) {

    }

    public void setBoardListener(@Nullable GameBoardListener boardListener) {
        this.gameBoardListener = boardListener;
    }

    public @Nullable GameBoardListener getGameBoardListener() {
        return gameBoardListener;
    }
}
