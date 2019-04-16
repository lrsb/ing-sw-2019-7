package it.polimi.ingsw.client.views.boards;

import it.polimi.ingsw.client.views.sprite.Sprite;
import it.polimi.ingsw.client.views.sprite.SpriteBoard;
import it.polimi.ingsw.client.views.sprite.SpriteBoardListener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public abstract class AbstractBoard extends SpriteBoard implements SpriteBoardListener {
    protected final int width;
    protected final int height;
    private @Nullable SpriteBoardListener spriteBoardListener;

    protected AbstractBoard(@NotNull Dimension dimension) {
        super.setBoardListener(this);
        this.width = dimension.width;
        this.height = dimension.height;
    }

    @Contract(value = "_, _ -> new", pure = true)
    protected @NotNull Point transformPoint(double x, double y) {
        return new Point((int) (x * width), (int) (y * height));
    }

    @Contract(value = "_, _ -> new", pure = true)
    protected @NotNull Dimension transformDim(double width, double height) {
        return new Dimension((int) (width * this.width), (int) (height * this.height));
    }

    @Override
    public void onSpriteClicked(@NotNull Sprite sprite) {
        if (spriteBoardListener != null) spriteBoardListener.onSpriteClicked(sprite);
    }

    @Override
    public void onSpriteDragged(@NotNull Sprite sprite) {
        if (spriteBoardListener != null) spriteBoardListener.onSpriteDragged(sprite);
    }

    @Override
    public void setBoardListener(@Nullable SpriteBoardListener boardListener) {
        this.spriteBoardListener = boardListener;
    }
}