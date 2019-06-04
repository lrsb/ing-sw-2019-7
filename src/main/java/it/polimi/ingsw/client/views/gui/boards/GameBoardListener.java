package it.polimi.ingsw.client.views.gui.boards;

import org.jetbrains.annotations.Nullable;

public interface GameBoardListener {
    void spriteSelected(@Nullable Object data);

    boolean spriteMoved(@Nullable Object data, int x, int y);
}