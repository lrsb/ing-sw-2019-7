package it.polimi.ingsw.graphics.sprite;

import it.polimi.ingsw.graphics.Displayable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class Sprite implements Displayable {
    private float x;
    private float y;
    private Dimension dimension;
    private BufferedImage image;
    private boolean hidden = false;
    private boolean draggable = false;
    private boolean clickable = true;
    @Nullable
    private SpriteListener spriteListener;

    public Sprite(float x, float y, Dimension dimension, @NotNull Displayable displayable) throws IOException {
        this.x = x;
        this.y = y;
        this.dimension = dimension;
        this.image = displayable.getImage();
    }

    @Override
    public BufferedImage getImage() {
        return image;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
        updated();
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
        updated();
    }

    public void move(float dx, float dy) {
        x += dx;
        y += dy;
        updated();
    }

    public void moveTo(float x, float y) {
        this.x = x;
        this.y = y;
        updated();
    }

    public void moveTo(float x, float y, long inMillis) {
        var xdt = Math.abs(this.x - x) / inMillis * 20;
        var ydt = Math.abs(this.y - y) / inMillis * 20;
        var timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                move(xdt, ydt);
            }
        }, 0, 20);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
                moveTo(x, y);
            }
        }, inMillis);
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
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
        return draggable;
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

    @Nullable
    public SpriteListener getSpriteListener() {
        return spriteListener;
    }

    public void setSpriteListener(@Nullable SpriteListener spriteListener) {
        this.spriteListener = spriteListener;
    }

    private void updated() {
        Optional.ofNullable(spriteListener).ifPresent(e -> e.onSpriteUpdated(this));
    }
}