package it.polimi.ingsw.socket;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

public class AdrenalineServerSocket extends ServerSocket {
    public static final int PORT = 0xCAFE;
    private @NotNull Thread thread;

    public AdrenalineServerSocket(@NotNull AdrenalineServerSocketListener listener) throws IOException {
        super(PORT);
        thread = new Thread(() -> {
            while (!isClosed()) try {
                var socket = accept();
                socket.bindStreams();
                listener.onNewSocket(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    @Override
    public AdrenalineSocket accept() throws IOException {
        if (isClosed()) throw new SocketException("Socket is closed");
        if (!isBound()) throw new SocketException("Socket is not bound yet");
        var s = new AdrenalineSocket(null);
        implAccept(s);
        return s;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void close() throws IOException {
        thread.stop();
        super.close();
    }
}