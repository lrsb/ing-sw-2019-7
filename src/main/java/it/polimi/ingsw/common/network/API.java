package it.polimi.ingsw.common.network;

import it.polimi.ingsw.common.models.Action;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import org.jetbrains.annotations.NotNull;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

/**
 * The interface Api.
 */
public interface API extends Remote {
    /**
     * Auth user string.
     *
     * @param nickname the nickname
     * @param password the password
     * @return the string
     * @throws RemoteException the remote exception
     */
    @NotNull String authUser(@NotNull String nickname, @NotNull String password) throws RemoteException;

    /**
     * Create user string.
     *
     * @param nickname the nickname
     * @param password the password
     * @return the token
     * @throws RemoteException the remote exception
     */
    @NotNull String createUser(@NotNull String nickname, @NotNull String password) throws RemoteException;

    /**
     * Gets active game.
     *
     * @param token the token
     * @return the active game
     * @throws RemoteException the remote exception
     */
    @NotNull Game getActiveGame(@NotNull String token) throws RemoteException;

    /**
     * Gets rooms.
     *
     * @param token the token
     * @return the rooms
     * @throws RemoteException the remote exception
     */
    @NotNull List<Room> getRooms(@NotNull String token) throws RemoteException;

    /**
     * Join room room.
     *
     * @param token    the token
     * @param roomUuid the room uuid
     * @return the room
     * @throws RemoteException the remote exception
     */
    @NotNull Room joinRoom(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException;


    /**
     * Create room room.
     *
     * @param token    the token
     * @param name     the name
     * @return the room
     * @throws RemoteException the remote exception
     */
    @NotNull Room createRoom(@NotNull String token, @NotNull Room room) throws RemoteException;

    void quitRoom(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException;
    /**
     * Start game game.
     *
     * @param token    the token
     * @param roomUuid the room uuid
     * @return the game
     * @throws RemoteException the remote exception
     */
    @NotNull Game startGame(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException;

    /**
     * Do action boolean.
     *
     * @param token  the token
     * @param action the action
     * @return the boolean
     * @throws RemoteException the remote exception
     */
    boolean doAction(@NotNull String token, @NotNull Action action) throws RemoteException;

    /**
     * Add game listener.
     *
     * @param token        the token
     * @param gameUuid     the game uuid
     * @param gameListener the game listener
     * @throws RemoteException the remote exception
     */
    void addGameListener(@NotNull String token, @NotNull UUID gameUuid, @NotNull GameListener gameListener) throws RemoteException;

    /**
     * Add room listener.
     *
     * @param token        the token
     * @param roomUuid     the room uuid
     * @param roomListener the room listener
     * @throws RemoteException the remote exception
     */
    void addRoomListener(@NotNull String token, @NotNull UUID roomUuid, @NotNull RoomListener roomListener) throws RemoteException;

    /**
     * Remove game listener.
     *
     * @param token    the token
     * @param gameUuid the game uuid
     * @throws RemoteException the remote exception
     */
    void removeGameListener(@NotNull String token, @NotNull UUID gameUuid) throws RemoteException;

    /**
     * Remove room listener.
     *
     * @param token    the token
     * @param roomUuid the room uuid
     * @throws RemoteException the remote exception
     */
    void removeRoomListener(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException;

}