package it.polimi.ingsw.client.views.boards;

import it.polimi.ingsw.client.views.sprite.SpriteBoard;
import it.polimi.ingsw.client.views.sprite.SpriteBoardListener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The type Abstract board.
 */
public abstract class AbstractBoard extends SpriteBoard implements SpriteBoardListener {
    /**
     * The Width.
     */
    protected final int width;
    /**
     * The Height.
     */
    protected final int height;
    private @Nullable GameBoardListener gameBoardListener;

    /**
     * Instantiates a new Abstract board.
     *
     * @param dimension  the dimension
     * @param background the background
     */
    AbstractBoard(@NotNull Dimension dimension, @NotNull BufferedImage background) {
        super(background);
        super.setBoardListener(this);
        this.width = dimension.width;
        this.height = dimension.height;
    }

    /**
     * Transform point point.
     *
     * @param x the x
     * @param y the y
     * @return the point
     */
    @Contract(value = "_, _ -> new", pure = true)
    protected @NotNull Point transformPoint(double x, double y) {
        return new Point((int) (x * width), (int) (y * height));
    }

    /**
     * Transform dim dimension.
     *
     * @param width  the width
     * @param height the height
     * @return the dimension
     */
    @Contract(value = "_, _ -> new", pure = true)
    protected @NotNull Dimension transformDim(double width, double height) {
        return new Dimension((int) (width * this.width), (int) (height * this.height));
    }

    /**
     * Sets board listener.
     *
     * @param boardListener the board listener
     */
    public void setBoardListener(@Nullable GameBoardListener boardListener) {
        this.gameBoardListener = boardListener;
    }
}