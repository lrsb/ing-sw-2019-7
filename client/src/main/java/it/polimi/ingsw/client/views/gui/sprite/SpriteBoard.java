package it.polimi.ingsw.client.views.gui.sprite;

import it.polimi.ingsw.client.others.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class SpriteBoard extends JPanel implements SpriteListener, AutoCloseable {
    private static final int FRAMERATE = 60;
    private final @Nullable AtomicBoolean needRepaint = new AtomicBoolean(true);
    private final @NotNull ArrayList<Sprite> sprites = new ArrayList<>();

    private @Nullable BufferedImage background;
    private @Nullable SpriteBoardListener boardListener;
    private boolean closed = false;

    public SpriteBoard(@Nullable BufferedImage background) {
        this.background = background;
        var mouseListener = new SpriteMouseAdapter();
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        new Thread(() -> {
            try {
                while (!closed) {
                    //noinspection ForLoopReplaceableByForEach
                    for (int i = 0; i < sprites.size(); i++) sprites.get(i).interpolate();
                    if (needRepaint != null && needRepaint.get()) {
                        super.repaint();
                        needRepaint.set(false);
                    }
                    Thread.sleep(1000 / FRAMERATE);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    protected @NotNull ArrayList<Sprite> getSprites() {
        return sprites;
    }

    public void addSprite(@NotNull Sprite sprite) {
        sprites.add(sprite);
        sprite.setSpriteListener(this);
        repaint();
    }

    public void addAllSprite(@NotNull java.util.List<Sprite> sprites) {
        this.sprites.addAll(sprites);
        sprites.forEach(e -> e.setSpriteListener(this));
        repaint();
    }

    public @Nullable Sprite removeSprite(@NotNull Sprite sprite) {
        var removed = sprites.remove(sprite);
        if (removed) {
            sprite.setSpriteListener(null);
            repaint();
        }
        return removed ? sprite : null;
    }

    public void removeAllSprites() {
        sprites.parallelStream().forEach(e -> e.setSpriteListener(null));
        sprites.clear();
    }

    private @NotNull Point transformPoint(@NotNull Point point) {
        var backgroundDimension = getBackgroundDimension();
        return backgroundDimension == null ? point :
                new Point((int) (point.x * backgroundDimension.getWidth() / 1200 + Math.abs(getWidth() - backgroundDimension.width) / 2),
                        (int) (point.y * backgroundDimension.getHeight() / 844 + Math.abs(getHeight() - backgroundDimension.height) / 2));
    }

    private @NotNull Point reverseTransformPoint(@NotNull Point point) {
        var backgroundDimension = getBackgroundDimension();
        return backgroundDimension == null ? point :
                new Point((int) ((point.x - Math.abs(getWidth() - backgroundDimension.width) / 2) * 1200 / backgroundDimension.getWidth()),
                        (int) ((point.y - Math.abs(getHeight() - backgroundDimension.height) / 2) * 844 / backgroundDimension.getHeight()));
    }

    private @NotNull Dimension transformDimension(@NotNull Dimension dimension, boolean swapAxis) {
        var backgroundDimension = getBackgroundDimension();
        return backgroundDimension == null || background == null ? dimension :
                new Dimension((int) (dimension.width * (swapAxis ? backgroundDimension.getHeight() / background.getHeight() : backgroundDimension.getWidth() / background.getWidth())),
                        (int) (dimension.height * (swapAxis ? backgroundDimension.getWidth() / background.getWidth() : backgroundDimension.getHeight() / background.getHeight())));
    }

    private @Nullable Double getBackgroundFormFactor() {
        return background == null ? null : (double) background.getWidth() / background.getHeight();
    }

    private @Nullable Dimension getBackgroundDimension() {
        var formFactor = getBackgroundFormFactor();
        if (formFactor == null) return null;
        var frameFormFactor = (double) getWidth() / getHeight();
        int height, width;
        if (formFactor < frameFormFactor) {
            width = (int) (formFactor * getHeight());
            height = (int) (width / formFactor);
        } else {
            height = (int) (getWidth() / formFactor);
            width = (int) (formFactor * height);
        }
        return new Dimension(width, height);
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag)
            Executors.newSingleThreadScheduledExecutor().schedule((Runnable) super::repaint, 400, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        var graphics = (Graphics2D) g;
        var isRetina = Utils.isRetina();
        if (isRetina) graphics.scale(0.5, 0.5);
        var backgroundDimension = getBackgroundDimension();
        if (backgroundDimension != null) {
            var origin = transformPoint(new Point(0, 0));
            graphics.drawImage(background, isRetina ? origin.x * 2 : origin.x, isRetina ? origin.y * 2 : origin.y,
                    isRetina ? backgroundDimension.width * 2 : backgroundDimension.width,
                    isRetina ? backgroundDimension.height * 2 : backgroundDimension.height, this);
        }
        //noinspection unchecked
        var sprites = (ArrayList<Sprite>) this.sprites.clone();
        sprites.parallelStream().filter(e -> !e.isHidden() && e.getFade() != 0).forEachOrdered(e -> {
            var point = transformPoint(e.getPosition());
            var dimension = transformDimension(e.getDimension(),
                    e.getRotation() == Sprite.Rotation.HALF_PI || e.getRotation() == Sprite.Rotation.THREE_HALF_PI);
            if (isRetina) {
                point.x *= 2;
                point.y *= 2;
                dimension.width *= 2;
                dimension.height *= 2;
            }
            var at = new AffineTransform();
            if (e.getRotation() != Sprite.Rotation.ZERO) {
                at.rotate(e.getRotation().getDegrees(), point.x + dimension.width / 2, point.y + dimension.height / 2);
                if (e.getRotation() != Sprite.Rotation.PI) {
                    var translation = (dimension.width - dimension.height) / 2;
                    if (e.getRotation() == Sprite.Rotation.THREE_HALF_PI) translation *= -1;
                    at.translate(translation, translation);
                }
            }
            var op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            graphics.setTransform(op.getTransform());
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) e.getFade()));
            graphics.drawImage(e.getBufferedImage(), point.x, point.y, dimension.width, dimension.height, this);
        });
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void onSpriteUpdated(@NotNull Sprite sprite) {
        repaint();
    }

    public void setBoardListener(@Nullable SpriteBoardListener boardListener) {
        this.boardListener = boardListener;
    }

    @Override
    public void autoRemove(@NotNull Sprite sprite) {
        sprites.remove(sprite);
        repaint();
    }

    public void setBackground(@NotNull BufferedImage bufferedImage) {
        background = bufferedImage;
    }

    @Override
    public void repaint() {
        if (needRepaint != null) needRepaint.set(true);
    }

    @Override
    public void close() {
        removeAllSprites();
        closed = true;
    }

    private class SpriteMouseAdapter extends MouseInputAdapter {
        private @Nullable Sprite draggingSprite;
        private @Nullable Point relativePoint;
        private boolean dragged;

        @Override
        public void mouseClicked(MouseEvent event) {
            if (boardListener != null)
                sprites.parallelStream().filter(e -> !e.isHidden() && e.isClickable()).filter(f -> contained(f, event)).findFirst()
                        .ifPresent(g -> boardListener.onSpriteClicked(g));
        }

        @Override
        public void mousePressed(MouseEvent event) {
            sprites.parallelStream().filter(e -> !e.isHidden() && e.isDraggable())
                    .filter(f -> contained(f, event)).findFirst().ifPresent(g -> {
                if (g.isClickable()) {
                    sprites.remove(g);
                    sprites.add(g);
                    repaint();
                }
                draggingSprite = g;
                var reversedPoint = reverseTransformPoint(event.getPoint());
                relativePoint = new Point(reversedPoint.x - g.getX(), reversedPoint.y - g.getY());
            });
        }

        @Override
        public void mouseDragged(MouseEvent event) {
            Optional.ofNullable(draggingSprite).ifPresent(e -> {
                dragged = true;
                if (relativePoint != null) {
                    var reversedPoint = reverseTransformPoint(new Point(event.getX(), event.getY()));
                    e.moveTo(reversedPoint.x - relativePoint.x, reversedPoint.y - relativePoint.y);
                }
            });
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            Optional.ofNullable(boardListener)
                    .ifPresent(l -> l.onSpriteHovered(sprites.parallelStream().filter(f -> contained(f, e)).collect(Collectors.toList())));
        }

        @Override
        public void mouseReleased(MouseEvent event) {
            Optional.ofNullable(boardListener).ifPresent(e -> {
                if (dragged) Optional.ofNullable(draggingSprite).ifPresent(f -> e.onSpriteDragged(draggingSprite));
            });
            dragged = false;
            draggingSprite = null;
        }

        private boolean contained(@NotNull Sprite sprite, @NotNull MouseEvent event) {
            var spritePoint = transformPoint(sprite.getPosition());
            var spriteDimension = transformDimension(sprite.getDimension(),
                    sprite.getRotation() == Sprite.Rotation.HALF_PI || sprite.getRotation() == Sprite.Rotation.THREE_HALF_PI);
            if (sprite.getRotation() == Sprite.Rotation.HALF_PI || sprite.getRotation() == Sprite.Rotation.THREE_HALF_PI)
                return spritePoint.x <= event.getX() && event.getX() <= spritePoint.x + spriteDimension.height &&
                        spritePoint.y <= event.getY() && event.getY() <= spritePoint.y + spriteDimension.width;
            return spritePoint.x <= event.getX() && event.getX() <= spritePoint.x + spriteDimension.width &&
                    spritePoint.y <= event.getY() && event.getY() <= spritePoint.y + spriteDimension.height;
        }
    }
}