package it.polimi.ingsw.common.network.socket;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketImpl;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static it.polimi.ingsw.Server.SOCKET_PORT;

public class AdrenalineSocket extends Socket {
    private static final @NotNull ExecutorService executorService = Executors.newCachedThreadPool();
    private @Nullable AdrenalineSocketListener listener;
    private @Nullable Scanner scanner;
    private @Nullable PrintWriter writer;

    public AdrenalineSocket(SocketImpl socketImpl) throws SocketException {
        super(socketImpl);
    }

    public AdrenalineSocket(@NotNull String ip, @NotNull AdrenalineSocketListener listener) throws IOException {
        super(ip, SOCKET_PORT);
        this.listener = listener;
        bindStreams();
    }

    public void bindStreams() throws IOException {
        scanner = new Scanner(new BufferedReader(new InputStreamReader(getInputStream())));
        writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(getOutputStream())));
        executorService.submit(() -> {
            while (!isClosed()) if (!isClosed() && writer.checkError() || !scanner.hasNextLine()) try {
                close();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
            else Optional.ofNullable(listener)
                        .ifPresent(e -> e.onNewPacket(this, new Gson().fromJson(scanner.nextLine(), AdrenalinePacket.class)));
        });
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

    @Override
    public synchronized void close() throws IOException {
        Optional.ofNullable(listener).ifPresent(e -> e.onClose(AdrenalineSocket.this));
        super.close();
    }
}