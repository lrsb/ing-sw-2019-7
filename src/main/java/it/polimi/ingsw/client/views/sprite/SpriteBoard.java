package it.polimi.ingsw.client.views.sprite;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpriteBoard extends JPanel implements SpriteListener, AutoCloseable {
    private static final int FRAMERATE = 60;
    private final @NotNull ArrayList<Sprite> sprites = new ArrayList<>();
    private @Nullable SpriteBoardListener boardListener;
    private @NotNull AtomicBoolean needRepaint = new AtomicBoolean(true);
    private boolean closed = false;

    public SpriteBoard() {
        var mouseListener = new SpriteMouseAdapter();
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        new Thread(() -> {
            try {
                while (!closed) {
                    //noinspection ForLoopReplaceableByForEach
                    for (int i = 0; i < sprites.size(); i++) sprites.get(i).interpolate();
                    if (needRepaint.get()) {
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

    private static boolean isRetina() {
        var isRetina = false;
        GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        try {
            var field = graphicsDevice.getClass().getDeclaredField("scale");
            if (field != null) {
                field.setAccessible(true);
                Object scale = field.get(graphicsDevice);
                if (scale instanceof Integer && (Integer) scale == 2) isRetina = true;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return isRetina;
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        var graphics = (Graphics2D) g;
        //TODO: Illegal reflective access
        if (isRetina()) graphics.scale(0.5, 0.5);
        sprites.forEach(e -> graphics.drawImage(e.getImage(), isRetina() ? e.getX() * 2 : e.getX(), isRetina() ? e.getY() * 2 : e.getY(), isRetina() ? e.getDimension().width * 2 : e.getDimension().width, isRetina() ? e.getDimension().height * 2 : e.getDimension().height, this));
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
    public void autoRemove(Sprite sprite) {
        sprites.remove(sprite);
        repaint();
    }

    @Override
    public void repaint() {
        //mah
        //noinspection ConstantConditions
        if (needRepaint != null) needRepaint.set(true);
    }

    @Override
    public void close() {
        closed = true;
    }

    private class SpriteMouseAdapter extends MouseInputAdapter {
        private @Nullable Sprite draggingSprite;
        private Point relativePoint;
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
                e.moveTo(event.getX() - relativePoint.x, event.getY() - relativePoint.y);
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
            return sprite.getX() <= event.getX() && event.getX() <= sprite.getX() + sprite.getDimension().width &&
                    sprite.getY() <= event.getY() && event.getY() <= sprite.getY() + sprite.getDimension().height;
        }
    }
}