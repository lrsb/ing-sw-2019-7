package it.polimi.ingsw.socket;

import org.jetbrains.annotations.NotNull;

public interface AdrenalineSocketListener {
    void onNewPacket(@NotNull AdrenalinePacket packet);
    void onClose(@NotNull AdrenalineSocket socket);
}
