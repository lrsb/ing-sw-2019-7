package it.polimi.ingsw.client.views.gui.sprite;

import it.polimi.ingsw.client.views.gui.sprite.interpolators.Interpolator;
import it.polimi.ingsw.client.views.gui.sprite.interpolators.exceptions.TimestampOutOfRangeException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

public class Sprite {
    private int x;
    private int y;
    private int width;
    private int height;

    private @NotNull Rotation rotation = Rotation.ZERO;

    private @NotNull BufferedImage bufferedImage;
    private @Nullable String tag;

    private boolean hidden = false;
    private boolean draggable = false;
    private boolean clickable = true;

    private @Nullable SpriteListener spriteListener;
    private @Nullable Interpolator interpolator;

    @Contract(pure = true)
    public Sprite(int x, int y, int width, int height, @NotNull BufferedImage bufferedImage) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bufferedImage = bufferedImage;
    }

    public @NotNull BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        updated();
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        updated();
    }

    public void translate(int dx, int dy) {
        x += dx;
        y += dy;
        updated();
    }

    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
        updated();
    }

    public void moveTo(@NotNull Interpolator interpolator) {
        this.interpolator = interpolator;
        updated();
    }

    public @NotNull Dimension getDimension() {
        return new Dimension(width, height);
    }

    public void setDimension(@NotNull Dimension dimension) {
        this.width = dimension.width;
        this.height = dimension.height;
        updated();
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isDraggable() {
        return draggable && interpolator == null;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public @Nullable SpriteListener getSpriteListener() {
        return spriteListener;
    }

    public void setSpriteListener(@Nullable SpriteListener spriteListener) {
        this.spriteListener = spriteListener;
    }

    public @NotNull Point getPosition() {
        return new Point(x, y);
    }

    public void setPosition(@NotNull Point position) {
        this.x = position.x;
        this.y = position.y;
    }

    public void remove() {
        Optional.ofNullable(spriteListener).ifPresent(e -> e.autoRemove(this));
    }

    public @Nullable String getTag() {
        return tag;
    }

    public void setTag(@Nullable String tag) {
        this.tag = tag;
    }

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

    public @NotNull Rotation getRotation() {
        return rotation;
    }

    public void setRotation(@NotNull Rotation rotation) {
        this.rotation = rotation;
    }

    public enum Rotation {
        ZERO(0), HALF_PI(Math.PI / 2), PI(Math.PI), THREE_HALF_PI(3 * Math.PI / 2);

        private double degrees;

        @Contract(pure = true)
        Rotation(double degrees) {
            this.degrees = degrees;
        }

        @Contract(pure = true)
        public double getDegrees() {
            return degrees;
        }
    }
}