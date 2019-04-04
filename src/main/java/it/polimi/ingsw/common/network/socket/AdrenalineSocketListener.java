package it.polimi.ingsw.common.network.socket;

import org.jetbrains.annotations.NotNull;

public interface AdrenalineSocketListener {
    void onNewPacket(@NotNull AdrenalineSocket socket, @NotNull AdrenalinePacket packet);
    void onClose(@NotNull AdrenalineSocket socket);
}
