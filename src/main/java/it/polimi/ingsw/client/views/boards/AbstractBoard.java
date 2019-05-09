package it.polimi.ingsw.client.views.boards;

import it.polimi.ingsw.client.views.sprite.SpriteBoard;
import it.polimi.ingsw.client.views.sprite.SpriteBoardListener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class AbstractBoard extends SpriteBoard implements SpriteBoardListener {
    protected final int width;
    protected final int height;
    protected final @NotNull BufferedImage background;
    protected @Nullable GameBoardListener gameBoardListener;

    protected AbstractBoard(@NotNull Dimension dimension, @NotNull BufferedImage background) {
        super(background);
        super.setBoardListener(this);
        this.width = dimension.width;
        this.height = dimension.height;
        this.background = background;
    }

    @Contract(value = "_, _ -> new", pure = true)
    protected @NotNull Point transformPoint(double x, double y) {
        return new Point((int) (x * width), (int) (y * height));
    }

    @Contract(value = "_, _ -> new", pure = true)
    protected @NotNull Dimension transformDim(double width, double height) {
        return new Dimension((int) (width * this.width), (int) (height * this.height));
    }

    //TODO: impl
    protected @NotNull Point nwBoardPoint(int x, int y) {
        //var point = transformPoint()
        return new Point(x * width, y * height);
    }

    public void setBoardListener(@Nullable GameBoardListener boardListener) {
        this.gameBoardListener = boardListener;
    }
}