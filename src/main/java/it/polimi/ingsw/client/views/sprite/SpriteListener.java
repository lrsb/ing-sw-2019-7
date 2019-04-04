package it.polimi.ingsw.client.views.sprite;

import org.jetbrains.annotations.NotNull;

public interface SpriteListener {
    void onSpriteUpdated(@NotNull Sprite sprite);

    void autoRemove(Sprite sprite);
}
