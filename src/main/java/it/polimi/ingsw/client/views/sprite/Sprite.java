package it.polimi.ingsw.client.views.sprite;

import it.polimi.ingsw.client.views.Displayable;
import it.polimi.ingsw.client.views.interpolator.Interpolator;
import it.polimi.ingsw.client.views.interpolator.exceptions.TimestampOutOfRangeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

public class Sprite implements Displayable {
    private int x;
    private int y;

    private @NotNull Dimension dimension;
    private @NotNull BufferedImage image;
    private @Nullable String tag;

    private boolean hidden = false;
    private boolean draggable = false;
    private boolean clickable = true;

    private @Nullable SpriteListener spriteListener;
    private @Nullable Interpolator interpolator;

    public Sprite(int x, int y, @NotNull Dimension dimension, @NotNull Displayable displayable) throws IOException {
        this.x = x;
        this.y = y;
        this.dimension = dimension;
        this.image = displayable.getImage();
    }

    @Override
    public @NotNull BufferedImage getImage() {
        return image;
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

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
        updated();
    }

    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
        updated();
    }

    public void moveTo(@NotNull Point point) {
        this.x = point.x;
        this.y = point.y;
        updated();
    }

    public void moveTo(@NotNull Interpolator interpolator) {
        this.interpolator = interpolator;
        updated();
    }

    public @NotNull Dimension getDimension() {
        return dimension;
    }

    public void setDimension(@NotNull Dimension dimension) {
        this.dimension = dimension;
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
            if (interpolator.getEndMillis() < System.currentTimeMillis()) {
                x = interpolator.getEndPoint().x;
                y = interpolator.getEndPoint().y;
                interpolator.onInterpolationCompleted();
                interpolator = null;
            } else try {
                var point = interpolator.interpolate(System.currentTimeMillis());
                x = point.x;
                y = point.y;
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