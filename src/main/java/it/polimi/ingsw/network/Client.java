package it.polimi.ingsw.network;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String args[]) throws IOException {
        System.out.println("Enter Adrenaline Server IP address: ");
        Scanner stdin = new Scanner(System.in);
        String IPServer = stdin.next();
        System.out.println("Enter Adrenaline Port: ");
        int PortGame = stdin.nextInt();
        System.out.println("OK...connecting to IP: " + IPServer + " Port: " + PortGame);
        Socket socketClient = null;
        BufferedReader brClient = null;
        PrintWriter pwClient = null;
        try{
            socketClient = new Socket(IPServer, PortGame);
            InputStreamReader isrClient = new InputStreamReader(socketClient.getInputStream());
            brClient = new BufferedReader(isrClient);
            OutputStreamWriter oswClient = new OutputStreamWriter(socketClient.getOutputStream());
            BufferedWriter bwClient = new BufferedWriter(oswClient);
            pwClient = new PrintWriter(bwClient);
            String Msg = null;
            System.out.println("Talking to the Adrenaline Server...");
            while(true) {
                System.out.println("You: ");
                Msg = stdin.next();
                pwClient.println(Msg);
                pwClient.flush();
                if(Msg.equals("END")) break;
                System.out.println("Waiting for an answer...");
                Msg = brClient.readLine();
                System.out.println("A.Server: " + Msg);
            }
        } catch (IOException IOE){
            System.err.println("Connection Failed.");
            System.exit(1);
        } finally {
            System.out.println("Closing channel.");
            brClient.close();
            pwClient.close();
            socketClient.close();
            System.out.println("Channel is closed.");
        }
    }
}
