package it.polimi.ingsw.server.network;

import it.polimi.ingsw.common.network.socket.AdrenalineSocket;
import it.polimi.ingsw.server.Server;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

/**
 * The type Adrenaline server socket.
 */
public class AdrenalineServerSocket extends ServerSocket {
    /**
     * Instantiates a new Adrenaline server socket.
     *
     * @param listener the listener
     * @throws IOException the io exception
     */
    public AdrenalineServerSocket(@NotNull AdrenalineServerSocketListener listener) throws IOException {
        super(Server.SOCKET_PORT);
        new Thread(() -> {
            while (!isClosed()) try {
                var socket = accept();
                socket.bindStreams();
                listener.onNewSocket(socket);
            } catch (IOException e) {
                e.printStackTrace();
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