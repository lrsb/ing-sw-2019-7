package it.polimi.ingsw.server.network;

import it.polimi.ingsw.common.network.socket.AdrenalineSocket;
import it.polimi.ingsw.server.Server;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Adrenaline server socket.
 */
public class AdrenalineServerSocket extends ServerSocket {
    private static final @NotNull Logger logger = Logger.getLogger("AdrenalineServerSocket");

    public AdrenalineServerSocket(@NotNull AdrenalineServerSocketListener listener) throws IOException {
        super(Server.SOCKET_PORT);
        new Thread(() -> {
            while (!isClosed()) try {
                var socket = accept();
                socket.bindStreams();
                listener.onNewSocket(socket);
            } catch (IOException e) {
                logger.log(Level.INFO, e.getMessage(), e);
            }
        }).start();
    }

    @Override
    public AdrenalineSocket accept() throws IOException {
        if (isClosed()) throw new SocketException("Socket is closed");
        if (!isBound()) throw new SocketException("Socket is not bound yet");
        var s = new AdrenalineSocket(null);
        implAccept(s);
        return s;
    }
}