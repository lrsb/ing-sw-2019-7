package it.polimi.ingsw.socket;

import org.jetbrains.annotations.NotNull;

public interface AdrenalineServerSocketListener {
    void onNewSocket(@NotNull AdrenalineSocket adrenalineSocket);
}