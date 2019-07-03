package it.polimi.ingsw.client.views.gui.sprite;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

public interface SpriteBoardListener {
    void onSpriteClicked(@NotNull Sprite sprite);

    void onSpriteDragged(@NotNull Sprite sprite);

    void onSpriteHovered(@NotNull List<Sprite> sprites);

    void onBoardClicked(@NotNull Point point);
}