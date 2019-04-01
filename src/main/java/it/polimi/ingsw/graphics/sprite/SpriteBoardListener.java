package it.polimi.ingsw.graphics.sprite;

import org.jetbrains.annotations.NotNull;

public interface SpriteBoardListener {
    void onSpriteClicked(@NotNull Sprite sprite);

    void onSpriteDragged(@NotNull Sprite sprite);
}
