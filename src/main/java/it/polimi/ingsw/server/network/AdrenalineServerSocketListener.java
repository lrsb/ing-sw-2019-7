package it.polimi.ingsw.server.network;

import it.polimi.ingsw.common.network.socket.AdrenalineSocket;
import org.jetbrains.annotations.NotNull;

/**
 * The interface Adrenaline server socket listener.
 */
public interface AdrenalineServerSocketListener {
    /**
     * On new socket.
     *
     * @param socket the socket
     */
    void onNewSocket(@NotNull AdrenalineSocket socket);
}