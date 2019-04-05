package it.polimi.ingsw.common.network;

import it.polimi.ingsw.common.models.Game;
import org.jetbrains.annotations.NotNull;

public interface GameListener {
    void onGameUpdated(@NotNull Game update);
}
