package it.polimi.ingsw.client.views.sprite;

import it.polimi.ingsw.client.views.sprite.interpolators.Interpolator;
import it.polimi.ingsw.client.views.sprite.interpolators.exceptions.TimestampOutOfRangeException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

/**
 * The type Sprite.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Sprite {
    private int x;
    private int y;
    private int width;
    private int height;

    private @NotNull BufferedImage bufferedImage;
    private @Nullable String tag;

    private boolean hidden = false;
    private boolean draggable = false;
    private boolean clickable = true;

    private @Nullable SpriteListener spriteListener;
    private @Nullable Interpolator interpolator;

    /**
     * Instantiates a new Sprite.
     *
     * @param x             the x
     * @param y             the y
     * @param width         the width
     * @param height        the height
     * @param bufferedImage the buffered image
     */
    @Contract(pure = true)
    public Sprite(int x, int y, int width, int height, @NotNull BufferedImage bufferedImage) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bufferedImage = bufferedImage;
    }

    /**
     * Instantiates a new Sprite.
     *
     * @param position      the position
     * @param dimension     the dimension
     * @param bufferedImage the buffered image
     */
    @Contract(pure = true)
    public Sprite(@NotNull Point position, @NotNull Dimension dimension, @NotNull BufferedImage bufferedImage) {
        this.x = position.x;
        this.y = position.y;
        this.width = dimension.width;
        this.height = dimension.height;
        this.bufferedImage = bufferedImage;
    }

    /**
     * Gets buffered image.
     *
     * @return the buffered image
     */
    public @NotNull BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    /**
     * Gets x.
     *
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * Sets x.
     *
     * @param x the x
     */
    public void setX(int x) {
        this.x = x;
        updated();
    }

    /**
     * Gets y.
     *
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * Sets y.
     *
     * @param y the y
     */
    public void setY(int y) {
        this.y = y;
        updated();
    }

    /**
     * Translate.
     *
     * @param dx the dx
     * @param dy the dy
     */
    public void translate(int dx, int dy) {
        x += dx;
        y += dy;
        updated();
    }

    /**
     * Move to.
     *
     * @param x the x
     * @param y the y
     */
    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
        updated();
    }

    /**
     * Move to.
     *
     * @param interpolator the interpolator
     */
    public void moveTo(@NotNull Interpolator interpolator) {
        this.interpolator = interpolator;
        updated();
    }

    /**
     * Gets dimension.
     *
     * @return the dimension
     */
    public @NotNull Dimension getDimension() {
        return new Dimension(width, height);
    }

    /**
     * Sets dimension.
     *
     * @param dimension the dimension
     */
    public void setDimension(@NotNull Dimension dimension) {
        this.width = dimension.width;
        this.height = dimension.height;
        updated();
    }

    /**
     * Is hidden boolean.
     *
     * @return the boolean
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Sets hidden.
     *
     * @param hidden the hidden
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * Is draggable boolean.
     *
     * @return the boolean
     */
    public boolean isDraggable() {
        return draggable && interpolator == null;
    }

    /**
     * Sets draggable.
     *
     * @param draggable the draggable
     */
    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    /**
     * Is clickable boolean.
     *
     * @return the boolean
     */
    public boolean isClickable() {
        return clickable;
    }

    /**
     * Sets clickable.
     *
     * @param clickable the clickable
     */
    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    /**
     * Gets sprite listener.
     *
     * @return the sprite listener
     */
    public @Nullable SpriteListener getSpriteListener() {
        return spriteListener;
    }

    /**
     * Sets sprite listener.
     *
     * @param spriteListener the sprite listener
     */
    public void setSpriteListener(@Nullable SpriteListener spriteListener) {
        this.spriteListener = spriteListener;
    }

    /**
     * Gets position.
     *
     * @return the position
     */
    public @NotNull Point getPosition() {
        return new Point(x, y);
    }

    /**
     * Sets position.
     *
     * @param position the position
     */
    public void setPosition(@NotNull Point position) {
        this.x = position.x;
        this.y = position.y;
    }

    /**
     * Remove.
     */
    public void remove() {
        Optional.ofNullable(spriteListener).ifPresent(e -> e.autoRemove(this));
    }

    /**
     * Gets tag.
     *
     * @return the tag
     */
    public @Nullable String getTag() {
        return tag;
    }

    /**
     * Sets tag.
     *
     * @param tag the tag
     */
    public void setTag(@Nullable String tag) {
        this.tag = tag;
    }

    /**
     * Interpolate.
     */
    void interpolate() {
        if (interpolator != null) {
            if (System.currentTimeMillis() > interpolator.getEndMillis()) {
                setPosition(interpolator.getEndPoint());
                interpolator.onInterpolationCompleted();
                interpolator = null;
            } else try {
                setPosition(interpolator.interpolate(System.currentTimeMillis()));
            } catch (TimestampOutOfRangeException timestampOutOfRangeException) {
                timestampOutOfRangeException.printStackTrace();
                interpolator.onInterpolationCompleted();
                interpolator = null;
            }
            Optional.ofNullable(spriteListener).ifPresent(e -> e.onSpriteUpdated(this));
        }
    }

    private void updated() {
        interpolate();
        Optional.ofNullable(spriteListener).ifPresent(e -> e.onSpriteUpdated(this));
    }
}