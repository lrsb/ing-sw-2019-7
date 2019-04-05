package it.polimi.ingsw.common.network.rmi;

import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.network.BaseAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.rmi.RemoteException;
import java.util.UUID;

public interface RmiAPI extends BaseAPI {
    @Nullable Game waitGameUpdate(@NotNull String token, @NotNull UUID gameUuid) throws RemoteException;

    @Nullable Room waitRoomUpdate(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException;
}