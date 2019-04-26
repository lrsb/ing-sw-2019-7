package it.polimi.ingsw.client.views.sprite;

import it.polimi.ingsw.client.views.sprite.interpolators.Interpolator;
import it.polimi.ingsw.client.views.sprite.interpolators.exceptions.TimestampOutOfRangeException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Sprite {
    private @NotNull Point position;
    private @NotNull Dimension dimension;
    private @NotNull BufferedImage bufferedImage;
    private @Nullable String tag;

    private boolean hidden = false;
    private boolean draggable = false;
    private boolean clickable = true;

    private @Nullable SpriteListener spriteListener;
    private @Nullable Interpolator interpolator;

    @Contract(pure = true)
    public Sprite(int x, int y, int width, int height, @NotNull Displayable displayable) throws IOException {
        this.position = new Point(x, y);
        this.dimension = new Dimension(width, height);
        this.bufferedImage = displayable.getImage();
    }

    @Contract(pure = true)
    public Sprite(@NotNull Point position, @NotNull Dimension dimension, @NotNull Displayable displayable) throws IOException {
        this.position = position;
        this.dimension = dimension;
        this.bufferedImage = displayable.getImage();
    }

    public @NotNull BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public int getX() {
        return position.x;
    }

    public void setX(int x) {
        position.x = x;
        updated();
    }

    public int getY() {
        return position.y;
    }

    public void setY(int y) {
        position.y = y;
        updated();
    }

    public void translate(int dx, int dy) {
        position.translate(dx, dy);
        updated();
    }

    public void moveTo(int x, int y) {
        position.move(x, y);
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
        return new Point(position);
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
                position.x = interpolator.getEndPoint().x;
                position.y = interpolator.getEndPoint().y;
                interpolator.onInterpolationCompleted();
                interpolator = null;
            } else try {
                var point = interpolator.interpolate(System.currentTimeMillis());
                position.x = point.x;
                position.y = point.y;
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