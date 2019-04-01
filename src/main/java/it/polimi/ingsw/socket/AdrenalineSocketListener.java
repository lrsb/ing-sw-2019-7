package it.polimi.ingsw.socket;

import org.jetbrains.annotations.NotNull;

public interface AdrenalineSocketListener {
    void onNewObject(@NotNull AdrenalinePacket object);

    void onClose(@NotNull AdrenalineSocket socket);
}
