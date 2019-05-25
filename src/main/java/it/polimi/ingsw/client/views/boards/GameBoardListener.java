package it.polimi.ingsw.client.views.boards;

import it.polimi.ingsw.common.models.Action;
import org.jetbrains.annotations.NotNull;

public interface GameBoardListener {
    void doAction(@NotNull Action action);
}
