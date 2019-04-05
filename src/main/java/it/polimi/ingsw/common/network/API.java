package it.polimi.ingsw.common.network;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface API extends BaseAPI {
    void addGameListener(@NotNull String token, @NotNull UUID gameUuid, @NotNull GameListener gameListener);

    void addRoomListener(@NotNull String token, @NotNull UUID roomUuid, @NotNull RoomListener roomListener);

    void removeGameListener(@NotNull String token, @NotNull UUID gameUuid);

    void removeRoomListener(@NotNull String token, @NotNull UUID roomUuid);
}