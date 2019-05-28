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
import java.util.concurrent.atomic.AtomicBoolean;

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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        var graphics = (Graphics2D) g;
        var isRetina = Utils.isRetina();
        if (isRetina) graphics.scale(0.5, 0.5);
        if (background != null)
            graphics.drawImage(background, 0, 0, isRetina ? getWidth() * 2 : getWidth(), isRetina ? getHeight() * 2 : getHeight(), this);
        //noinspection unchecked
        var sprites = (ArrayList<Sprite>) this.sprites.clone();
        sprites.parallelStream().filter(e -> !e.isHidden() && e.getFade() != 0).forEachOrdered(e -> {
            var x = isRetina ? e.getX() * 2 : e.getX();
            var y = isRetina ? e.getY() * 2 : e.getY();
            var width = isRetina ? e.getDimension().width * 2 : e.getDimension().width;
            var height = isRetina ? e.getDimension().height * 2 : e.getDimension().height;
            var op = new AffineTransformOp(new AffineTransform(), AffineTransformOp.TYPE_BILINEAR);
            if (e.getRotation() != Sprite.Rotation.ZERO) {
                var at = new AffineTransform();
                at.rotate(e.getRotation().getDegrees(), x + width / 2, y + height / 2);
                if (e.getRotation() != Sprite.Rotation.PI) {
                    var translation = (width - height) / 2;
                    if (e.getRotation() == Sprite.Rotation.THREE_HALF_PI) translation *= -1;
                    at.translate(translation, translation);
                }
                op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            }
            graphics.setTransform(op.getTransform());
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) e.getFade()));
            graphics.drawImage(e.getBufferedImage(), x, y, width, height, this);
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
                relativePoint = new Point(event.getX() - g.getX(), event.getY() - g.getY());
            });
        }

        @Override
        public void mouseDragged(MouseEvent event) {
            Optional.ofNullable(draggingSprite).ifPresent(e -> {
                dragged = true;
                if (relativePoint != null) e.moveTo(event.getX() - relativePoint.x, event.getY() - relativePoint.y);
            });
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
            if (sprite.getRotation() == Sprite.Rotation.HALF_PI || sprite.getRotation() == Sprite.Rotation.THREE_HALF_PI)
                return sprite.getX() <= event.getX() && event.getX() <= sprite.getX() + sprite.getDimension().height &&
                        sprite.getY() <= event.getY() && event.getY() <= sprite.getY() + sprite.getDimension().width;
            return sprite.getX() <= event.getX() && event.getX() <= sprite.getX() + sprite.getDimension().width &&
                    sprite.getY() <= event.getY() && event.getY() <= sprite.getY() + sprite.getDimension().height;
        }
    }
}
