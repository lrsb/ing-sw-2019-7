package it.polimi.ingsw.client.views.sprite;

import org.jetbrains.annotations.NotNull;

/**
 * The interface Sprite board listener.
 */
public interface SpriteBoardListener {
    /**
     * On sprite clicked.
     *
     * @param sprite the sprite
     */
    void onSpriteClicked(@NotNull Sprite sprite);

    /**
     * On sprite dragged.
     *
     * @param sprite the sprite
     */
    void onSpriteDragged(@NotNull Sprite sprite);
}