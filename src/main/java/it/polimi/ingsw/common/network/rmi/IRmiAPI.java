package it.polimi.ingsw.common.network.rmi;

import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

public interface IRmiAPI extends Remote {
    @Nullable List<Room> getRooms(@NotNull String token) throws RemoteException;

    @Nullable Room joinRoom(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException;

    @Nullable Room createRoom(@NotNull String token, @NotNull String name) throws RemoteException;

    @Nullable UUID startGame(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException;

    void doMove(@NotNull String token, @NotNull Object move) throws RemoteException;

    @Nullable Game waitGameUpdate(@NotNull String token, @NotNull UUID gameUuid) throws RemoteException;

    @Nullable Room waitRoomUpdate(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException;
}