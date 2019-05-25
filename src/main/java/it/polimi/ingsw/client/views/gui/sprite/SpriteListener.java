package it.polimi.ingsw.client.views.gui.sprite;

import org.jetbrains.annotations.NotNull;

public interface SpriteListener {
    void onSpriteUpdated(@NotNull Sprite sprite);

    void autoRemove(@NotNull Sprite sprite);
}