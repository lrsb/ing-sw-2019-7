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
    private @Nullable AdrenalineSocketListener listener;
    private @Nullable Scanner scanner;
    private @Nullable PrintWriter writer;
    private @Nullable Thread thread;

    AdrenalineSocket(SocketImpl socketImpl) throws SocketException {
        super(socketImpl);
    }

    public AdrenalineSocket(@NotNull String ip, @NotNull AdrenalineSocketListener listener) throws IOException {
        super(ip, AdrenalineServerSocket.PORT);
        this.listener = listener;
        bindStreams();
    }

    void bindStreams() throws IOException {
        if (thread != null) return;
        scanner = new Scanner(new BufferedReader(new InputStreamReader(getInputStream())));
        writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(getOutputStream())));
        thread = new Thread(() -> {
            while (!isClosed()) if (!isClosed() && writer.checkError() || !scanner.hasNextLine()) try {
                close();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
            else Optional.ofNullable(listener)
                        .ifPresent(e -> e.onNewObject(new Gson().fromJson(scanner.nextLine(), AdrenalinePacket.class)));
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