package it.polimi.ingsw.socket;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketImpl;
import java.util.Optional;
import java.util.Scanner;

public class AdrenalineSocket extends Socket {
    @Nullable
    private AdrenalineSocketListener listener;
    @Nullable
    private Scanner scanner;
    @Nullable
    private PrintWriter writer;
    @Nullable
    private Thread thread;

    AdrenalineSocket(SocketImpl socketImpl) throws SocketException {
        super(socketImpl);
    }

    public AdrenalineSocket(@NotNull String ip, @Nullable AdrenalineSocketListener listener) throws IOException {
        super(ip, AdrenalineServerSocket.PORT);
        this.listener = listener;
        bindStreams();
    }

    void bindStreams() throws IOException {
        scanner = new Scanner(new BufferedReader(new InputStreamReader(getInputStream())));
        writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(getOutputStream())));
        thread = new Thread(() -> {
            while (!isClosed()) if (!isClosed() && writer.checkError() || !scanner.hasNextLine()) try {
                close();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
            else if (listener != null)
                this.listener.onNewObject(new Gson().fromJson(scanner.nextLine(), AdrenalinePacket.class));
        });
        thread.start();
    }

    public void send(AdrenalinePacket object) {
        Optional.ofNullable(writer).ifPresent(e -> {
            e.println(new Gson().toJson(object));
            e.flush();
        });
    }

    public void setAdrenalineSocketListener(@NotNull AdrenalineSocketListener listener) {
        this.listener = listener;
    }

    @SuppressWarnings("deprecation")
    @Override
    public synchronized void close() throws IOException {
        Optional.ofNullable(listener).ifPresent(e -> e.onClose(AdrenalineSocket.this));
        Optional.ofNullable(thread).ifPresent(Thread::stop);
        super.close();
    }
}