package it.polimi.ingsw.client.views.sprite;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The type Sprite board.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class SpriteBoard extends JPanel implements SpriteListener, Closeable {
    private static final int FRAMERATE = 60;
    private final @NotNull ArrayList<Sprite> sprites = new ArrayList<>();
    private final @Nullable AtomicBoolean needRepaint = new AtomicBoolean(true);
    private @Nullable BufferedImage background;
    private @Nullable SpriteBoardListener boardListener;
    private boolean closed = false;

    /**
     * Instantiates a new Sprite board.
     *
     * @param background the background
     */
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

    /**
     * Is retina boolean.
     *
     * @return the boolean
     */
    protected static boolean isRetina() {
        var isRetina = false;
        GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        try {
            //TODO: Illegal reflective access
            var field = graphicsDevice.getClass().getDeclaredField("scale");
            if (field != null) {
                field.setAccessible(true);
                Object scale = field.get(graphicsDevice);
                if (scale instanceof Integer && (Integer) scale == 2) isRetina = true;
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
        return isRetina;
    }

    /**
     * Add sprite.
     *
     * @param sprite the sprite
     */
    public void addSprite(@NotNull Sprite sprite) {
        sprites.add(sprite);
        sprite.setSpriteListener(this);
        repaint();
    }

    /**
     * Remove sprite sprite.
     *
     * @param sprite the sprite
     * @return the sprite
     */
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
        var isRetina = isRetina();
        if (isRetina) graphics.scale(0.5, 0.5);
        if (background != null)
            graphics.drawImage(background, 0, 0, isRetina ? getWidth() * 2 : getWidth(), isRetina ? getHeight() * 2 : getHeight(), this);
        sprites.parallelStream().map(e -> {
            var image = e.getBufferedImage();
            var width = isRetina ? e.getDimension().width * 2 : e.getDimension().width;
            var heigth = isRetina ? e.getDimension().height * 2 : e.getDimension().height;
            if (e.getRotation() != Sprite.Rotation.ZERO) {
                var at = new AffineTransform();
                var dst = new BufferedImage(e.getRotation() != Sprite.Rotation.PI ? image.getHeight() : image.getWidth(),
                        e.getRotation() != Sprite.Rotation.PI ? image.getWidth() : image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                at.rotate(e.getRotation().getDegrees(), image.getWidth() / 2, image.getHeight() / 2);
                if (e.getRotation() != Sprite.Rotation.PI) {
                    at.translate((e.getRotation() == Sprite.Rotation.HALF_PI ? image.getWidth() - image.getHeight() : image.getHeight() - image.getWidth()) / 2,
                            (e.getRotation() == Sprite.Rotation.HALF_PI ? image.getWidth() - image.getHeight() : image.getHeight() - image.getWidth()) / 2);
                    var tempWidth = width;
                    width = heigth;
                    heigth = tempWidth;
                }
                var op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                op.filter(image, dst);
                image = dst;
            }
            return new Pepsi(isRetina ? e.getX() * 2 : e.getX(), isRetina ? e.getY() * 2 : e.getY(), width, heigth, image);
        }).forEachOrdered(e -> graphics.drawImage(e.bufferedImage, e.x, e.y, e.width, e.height, this));
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void onSpriteUpdated(@NotNull Sprite sprite) {
        repaint();
    }

    /**
     * Sets board listener.
     *
     * @param boardListener the board listener
     */
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

    private static class Pepsi {
        private int x;
        private int y;
        private int width;
        private int height;
        private @NotNull BufferedImage bufferedImage;

        @Contract(pure = true)
        private Pepsi(int x, int y, int width, int height, @NotNull BufferedImage bufferedImage) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.bufferedImage = bufferedImage;
        }
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
