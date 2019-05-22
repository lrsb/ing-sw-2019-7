package it.polimi.ingsw.client.views.sprite;

import org.jetbrains.annotations.NotNull;

/**
 * The interface Sprite listener.
 */
public interface SpriteListener {
    /**
     * On sprite updated.
     *
     * @param sprite the sprite
     */
    void onSpriteUpdated(@NotNull Sprite sprite);

    /**
     * Auto remove.
     *
     * @param sprite the sprite
     */
    void autoRemove(@NotNull Sprite sprite);
}