package it.polimi.ingsw.common.network.socket;

import org.jetbrains.annotations.NotNull;

/**
 * The interface Adrenaline socket listener.
 */
public interface AdrenalineSocketListener {
    /**
     * On new packet.
     *
     * @param socket the socket
     * @param packet the packet
     */
    void onNewPacket(@NotNull AdrenalineSocket socket, @NotNull AdrenalinePacket packet);

    /**
     * On close.
     *
     * @param socket the socket
     */
    void onClose(@NotNull AdrenalineSocket socket);
}
