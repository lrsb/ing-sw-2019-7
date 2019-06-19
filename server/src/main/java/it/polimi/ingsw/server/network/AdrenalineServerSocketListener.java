package it.polimi.ingsw.server.network;

import it.polimi.ingsw.common.network.socket.AdrenalineSocket;
import org.jetbrains.annotations.NotNull;

public interface AdrenalineServerSocketListener {
    void onNewSocket(@NotNull AdrenalineSocket socket);
}