package it.polimi.ingsw.network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
    public static final int PORT = 1050; // porta al di fuori del range 1-1024 !

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Adrenaline Server started...");
        System.out.println("Server Socket: " + serverSocket);
        Socket clientSocket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            // bloccante finchè non avviene una connessione
            clientSocket = serverSocket.accept();
            System.out.println("Connection accepted: " + clientSocket);
            // creazione stream di input da clientSocket
            InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
            in = new BufferedReader(isr);
            // creazione stream di output su clientSocket
            OutputStreamWriter osw = new OutputStreamWriter(clientSocket.getOutputStream());
            BufferedWriter bw = new BufferedWriter(osw);
            out = new PrintWriter(bw, true);
            //ciclo di ricezione dal client e invio di risposta
            while (true) {
                String str = in.readLine();
                if (str.equals("END")) break;
                System.out.println("Echoing: " + str);
                out.println(str);
            }
        } catch (IOException e) {
            System.err.println("Accept failed");
            System.exit(1);
        } finally {
            System.out.println("Adrenaline Server closing...");
            out.close();
            in.close();
            clientSocket.close();
            serverSocket.close();
            System.out.println("Connection closed.");
        }
    }
}