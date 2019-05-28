package it.polimi.ingsw.client.views.gui.sprite;

import it.polimi.ingsw.client.views.gui.sprite.exceptions.TimestampOutOfRangeException;
import it.polimi.ingsw.client.views.gui.sprite.fadeinterpolators.FadeInterpolator;
import it.polimi.ingsw.client.views.gui.sprite.pointinterpolators.PointInterpolator;
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
    private @Nullable Object associatedObject;

    private boolean hidden = false;
    private boolean draggable = false;
    private boolean clickable = true;
    private double fade = 1;
    private @Nullable SpriteListener spriteListener;
    private @Nullable PointInterpolator pointInterpolator;
    private @Nullable FadeInterpolator fadeInterpolator;

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

    public void moveTo(@NotNull PointInterpolator pointInterpolator) {
        this.pointInterpolator = pointInterpolator;
        updated();
    }

    public void fade(@NotNull FadeInterpolator fadeInterpolator) {
        this.fadeInterpolator = fadeInterpolator;
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isDraggable() {
        return draggable && pointInterpolator == null;
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

    @Nullable SpriteListener getSpriteListener() {
        return spriteListener;
    }

    void setSpriteListener(@Nullable SpriteListener spriteListener) {
        this.spriteListener = spriteListener;
    }

    public @NotNull Point getPosition() {
        return new Point(x, y);
    }

    private void setPosition(@NotNull Point position) {
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
        if (pointInterpolator != null) {
            if (System.currentTimeMillis() > pointInterpolator.getEndMillis()) {
                setPosition(pointInterpolator.getEndPoint());
                pointInterpolator.onInterpolationCompleted();
                pointInterpolator = null;
            } else try {
                setPosition(pointInterpolator.interpolate(System.currentTimeMillis()));
            } catch (TimestampOutOfRangeException e) {
                e.printStackTrace();
                pointInterpolator.onInterpolationCompleted();
                pointInterpolator = null;
            }
            Optional.ofNullable(spriteListener).ifPresent(e -> e.onSpriteUpdated(this));
        }
        if (fadeInterpolator != null) {
            if (System.currentTimeMillis() > fadeInterpolator.getEndMillis()) {
                setFade(fadeInterpolator.getEndFade());
                fadeInterpolator.onInterpolationCompleted();
                fadeInterpolator = null;
            } else try {
                setFade(fadeInterpolator.interpolate(System.currentTimeMillis()));
            } catch (TimestampOutOfRangeException e) {
                e.printStackTrace();
                fadeInterpolator.onInterpolationCompleted();
                fadeInterpolator = null;
            }
            Optional.ofNullable(spriteListener).ifPresent(e -> e.onSpriteUpdated(this));
        }
    }

    private void updated() {
        Optional.ofNullable(spriteListener).ifPresent(e -> e.onSpriteUpdated(this));
    }

    public @NotNull Rotation getRotation() {
        return rotation;
    }

    public void setRotation(@NotNull Rotation rotation) {
        this.rotation = rotation;
        updated();
    }

    public double getFade() {
        return fade;
    }

    public void setFade(double fade) {
        if (fade > 1 || fade < 0) return;
        this.fade = fade;
        updated();
    }

    public @Nullable Object getAssociatedObject() {
        return associatedObject;
    }

    public void setAssociatedObject(@Nullable Object associatedObject) {
        this.associatedObject = associatedObject;
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