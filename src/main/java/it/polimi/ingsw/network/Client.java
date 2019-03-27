package it.polimi.ingsw.network;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private PrintWriter out;
    private String Frase;
    private String IPServer;
    private int PortGame;
    public void main(String args[]) throws IOException {
        System.out.println("Enter Adrenaline Server IP address: ");
        Scanner stdin = new Scanner(System.in);
        IPServer = stdin.next();
        System.out.println("Enter Adrenaline Port: ");
        PortGame = stdin.nextInt();
        System.out.println("OK...connecting to IP: " + IPServer + " Port: " + PortGame);
        Socket sc = new Socket(IPServer, PortGame);
        InputStreamReader isr = new InputStreamReader(sc.getInputStream());
        BufferedReader br = new BufferedReader(isr);
        out = new PrintWriter(sc.getOutputStream());
        System.out.println("Talking to the Adrenaline Server...");
        while(true) {
            System.out.println("You: ");
            Frase = stdin.next();
            out.println(Frase);
            out.flush();
            System.out.println("Waiting for an answer...");
            Frase = br.readLine();
            System.out.println("A.Server: " + Frase);
        }
    }
}
