package it.polimi.ingsw.common.network;

import it.polimi.ingsw.common.models.*;
import org.jetbrains.annotations.NotNull;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

/**
 * Interface for all communication methods.
 * The scope of this interface is to provide a stateless access to a remote server that implements the business logic of the game.
 * All calls locks the current thread until a response is arrived.
 * All implementations must implement this behaviour.
 */
public interface API extends Remote {

    /**
     * When a {@link User} wants to login in.
     *
     * @param nickname The user nickname
     * @param password The user password.
     * @return The uuid and a token to use when another api is called. The token doesn't expire but is unique for each user. So is possible to steal a session 🤷‍♂️.
     * @throws RemoteException A {@link it.polimi.ingsw.common.network.exceptions.UserRemoteException} is thrown when there was a problem with the login.
     */
    @NotNull User.Auth authUser(@NotNull String nickname, @NotNull String password) throws RemoteException;

    /**
     * Create a new user. The user must have a unique nickname otherwise a exception is thrown.
     *
     * @param nickname the nickname
     * @param password the password
     * @return The uuid and a token to use when another api is called. The token doesn't expire but is unique for each user. So is possible to steal a session 🤷‍♂️.
     * @throws RemoteException Thrown when the nickname is already present.
     */
    @NotNull User.Auth createUser(@NotNull String nickname, @NotNull String password) throws RemoteException;

    /**
     * Gets active game if the user have one.
     *
     * @param token the token
     * @return the active game
     * @throws RemoteException Thrown when the user isn't playing any game.
     */
    @NotNull Game getActiveGame(@NotNull String token) throws RemoteException;

    /**
     * Gets rooms. A room is a place where users wait to begin a game
     *
     * @param token the token
     * @return the rooms
     * @throws RemoteException A {@link it.polimi.ingsw.common.network.exceptions.UserRemoteException} is thrown when there was a problem with the token.
     *                         A {@link RemoteException} is thrown when there was a problem with the server.
     */
    @NotNull List<Room> getRooms(@NotNull String token) throws RemoteException;

    /**
     * Join a room.
     *
     * @param token    the token
     * @param roomUuid the room uuid
     * @return the updated room
     * @throws RemoteException A {@link it.polimi.ingsw.common.network.exceptions.UserRemoteException} is thrown when there was a problem with the token.
     *                         A {@link RemoteException} is thrown when the room is full or there was a problem with the server.
     */
    @NotNull Room joinRoom(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException;

    /**
     * Create a new room.
     *
     * @param token the token
     * @param room  The room. Fields name, skulls, gameType, actionTimeout are mandatory, others are ignored.
     * @return the room
     * @throws RemoteException A {@link it.polimi.ingsw.common.network.exceptions.UserRemoteException} is thrown when there was a problem with the token.
     *                         A {@link RemoteException} is thrown when there was a problem with the server.
     */
    @NotNull Room createRoom(@NotNull String token, @NotNull Room room) throws RemoteException;

    /**
     * Exit a room.
     *
     * @param token    the token
     * @param roomUuid the room uuid
     * @throws RemoteException A {@link it.polimi.ingsw.common.network.exceptions.UserRemoteException} is thrown when there was a problem with the token.
     *                         A {@link RemoteException} is thrown when there was a problem with the server.
     */
    void quitRoom(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException;

    /**
     * Starts a game, only the user that created the room can start before the timer expires.
     *
     * @param token    the token
     * @param roomUuid the room uuid
     * @throws RemoteException A {@link it.polimi.ingsw.common.network.exceptions.UserRemoteException} is thrown when there was a problem with the token.
     *                         A {@link RemoteException} is thrown when there was a problem with the server.
     */
    void startGame(@NotNull String token, @NotNull UUID roomUuid) throws RemoteException;

    /**
     * Quit a game. TODO
     *
     * @param token    the token
     * @param gameUuid the game uuid
     * @throws RemoteException A {@link it.polimi.ingsw.common.network.exceptions.UserRemoteException} is thrown when there was a problem with the token.
     *                         A {@link RemoteException} is thrown when there was a problem with the server.
     */
    void quitGame(@NotNull String token, @NotNull UUID gameUuid) throws RemoteException;

    /**
     * Do action. TODO
     *
     * @param token  the token
     * @param action the action
     * @return true if the action is valid, otherwise false.
     * @throws RemoteException A {@link it.polimi.ingsw.common.network.exceptions.UserRemoteException} is thrown when there was a problem with the token.
     *                         A {@link RemoteException} is thrown when there was a problem with the server.
     */
    boolean doAction(@NotNull String token, @NotNull Action action) throws RemoteException;

    /**
     * Sends in game message
     *
     * @param token   the token
     * @param message the message. Fields gameUuid, message are mandatory, others are ignored.
     * @throws RemoteException A {@link it.polimi.ingsw.common.network.exceptions.UserRemoteException} is thrown when there was a problem with the token.
     *                         A {@link RemoteException} is thrown when there was a problem with the server.
     */
    void sendMessage(@NotNull String token, @NotNull Message message) throws RemoteException;

    /**
     * Add a listener. The listener is unique for each user. A listener receive objects from server.
     *
     * @param token    the token
     * @param listener the listener
     * @throws RemoteException A {@link it.polimi.ingsw.common.network.exceptions.UserRemoteException} is thrown when there was a problem with the token.
     *                         A {@link RemoteException} is thrown when there was a problem with the server.
     */
    void addListener(@NotNull String token, @NotNull Listener listener) throws RemoteException;

    /**
     * Remove the user listener.
     *
     * @param token the token
     * @throws RemoteException A {@link it.polimi.ingsw.common.network.exceptions.UserRemoteException} is thrown when there was a problem with the token.
     *                         A {@link RemoteException} is thrown when there was a problem with the server.
     */
    void removeListener(@NotNull String token) throws RemoteException;
}