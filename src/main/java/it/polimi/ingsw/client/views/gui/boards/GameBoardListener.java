package it.polimi.ingsw.client.views.gui.boards;

import org.jetbrains.annotations.Nullable;

import java.awt.*;

public interface GameBoardListener {
    void spriteSelected(@Nullable Object data, @Nullable Point point);

    boolean spriteMoved(@Nullable Object data, @Nullable Point point);
}