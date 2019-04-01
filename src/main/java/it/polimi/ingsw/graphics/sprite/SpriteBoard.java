package it.polimi.ingsw.graphics.sprite;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Optional;

public class SpriteBoard extends JPanel implements SpriteListener {
    private ArrayList<Sprite> sprites = new ArrayList<>();
    @Nullable
    private SpriteBoardListener boardListener;

    public SpriteBoard() {
        var mouseListener = new SpriteMouseAdapter();
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
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

    public void addSprite(Sprite sprite) {
        sprites.add(sprite);
        sprite.setSpriteListener(this);
        repaint();
    }

    @Nullable
    public Sprite removeSprite(Sprite sprite) {
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
        sprites.forEach(e -> graphics.drawImage(e.getImage(), (int) (isRetina() ? e.getX() * 2 : e.getX()), (int) (isRetina() ? e.getY() * 2 : e.getY()), isRetina() ? e.getDimension().width * 2 : e.getDimension().width, isRetina() ? e.getDimension().height * 2 : e.getDimension().height, this));
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void onSpriteUpdated(Sprite sprite) {
        repaint();
    }

    public void setBoardListener(@Nullable SpriteBoardListener boardListener) {
        this.boardListener = boardListener;
    }

    private class SpriteMouseAdapter extends MouseInputAdapter {
        @Nullable
        private Sprite draggingSprite;
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
                sprites.remove(g);
                sprites.add(g);
                repaint();
                draggingSprite = g;
                relativePoint = new Point((int) (event.getX() - g.getX()), (int) (event.getY() - g.getY()));
            });
        }

        @Override
        public void mouseDragged(MouseEvent event) {
            if (draggingSprite != null) {
                dragged = true;
                draggingSprite.moveTo(event.getX() - relativePoint.x, event.getY() - relativePoint.y);
            }
        }

        @Override
        public void mouseReleased(MouseEvent event) {
            Optional.ofNullable(boardListener).ifPresent(e -> {
                if (draggingSprite != null && dragged) e.onSpriteDragged(draggingSprite);
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