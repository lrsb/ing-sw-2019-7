package it.polimi.ingsw.client.views.cli;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.Server;
import it.polimi.ingsw.client.network.ClientRestImpl;
import it.polimi.ingsw.client.network.ClientRmiImpl;
import it.polimi.ingsw.client.network.ClientSocketImpl;
import it.polimi.ingsw.client.others.Preferences;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class Cli {
    private static@NotNull Scanner in = new Scanner(System.in);

    private static void connType() throws RemoteException {
        System.out.println("Ciao,benvenuto in Adrenalina, come vuoi effettuare la connessione?");
        System.out.println("1: SOCKET");
        System.out.println("2: RMI");
        System.out.println("3: HTTP");
        var in = new Scanner(System.in);
        var input = Integer.parseInt(in.nextLine());
        System.out.println("Done! Qual è il nome del tuo hostname?");
        var ip = in.nextLine();
        switch (input) {
            case 1:
                try {
                    Client.API = new ClientSocketImpl(ip);
                } catch (IOException warn){
                    System.out.println("Problema con la connessione all'host" + ip);
                }
                break;

            case 2:
                try{
                    Client.API = new ClientRmiImpl(LocateRegistry.getRegistry(ip, Server.RMI_PORT).lookup(Server.RMI_NAME));
                }catch (RemoteException | NotBoundException e){
                    System.out.println("Problema con la connessione all'host " + ip);
                }
                break;

            case 3:
                try{
                    Client.API = new ClientRestImpl(ip);
                    Client.API.getRooms("");
                }catch (RemoteException e){
                    System.out.println("Problema con la connessione all'host" + ip);
                }
                break;
        }
        if (Preferences.isLoggedIn()) mainmenu();
        login();
    }

    private static void login() {
        System.out.println("Inserisci il tuo nickname");
        var nickname = in.nextLine();
        System.out.println("Inserisci la tua passowrd");
        var psw = in.nextLine();
        try {
            var token = Client.API.authUser(nickname, psw);
            if (token == null) {
                System.out.println("Nickname e/o password errate!!");
            } else {
                Preferences.setToken(token);
                mainmenu();
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
            System.out.println("Problemi col server!!");
        }
    }

    private static void mainmenu(){

    }
    public static void start() throws RemoteException {
        connType();
    }

    public static void main(String[] args) throws RemoteException {
        start();
    }
}