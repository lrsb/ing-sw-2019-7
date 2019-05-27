package it.polimi.ingsw.client.views.cli;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.Server;
import it.polimi.ingsw.client.network.ClientRestImpl;
import it.polimi.ingsw.client.network.ClientRmiImpl;
import it.polimi.ingsw.client.network.ClientSocketImpl;
import it.polimi.ingsw.client.others.Preferences;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.List;
import java.util.Scanner;

public class Cli {
    private static@NotNull Scanner in = new Scanner(System.in);

    public final static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (final Exception e) {
            //  Handle any exceptions.
        }
    }

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
                    System.out.println("Problema con la connessione all'host " + ip);
                    connType();
                }
                break;

            case 2:
                try{
                    Client.API = new ClientRmiImpl(LocateRegistry.getRegistry(ip, Server.RMI_PORT).lookup(Server.RMI_NAME));
                }catch (RemoteException | NotBoundException e){
                    System.out.println("Problema con la connessione all'host " + ip);
                    connType();
                }
                break;

            case 3:
                try{
                    Client.API = new ClientRestImpl(ip);
                    Client.API.getRooms("");
                }catch (RemoteException e){
                    System.out.println("Problema con la connessione all'host " + ip);
                    connType();
                }
                break;
        }
        if (Preferences.isLoggedIn()) {
            clearConsole();
            mainMenu();
        }
        clearConsole();
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
                login();
            } else {
                Preferences.setToken(token);
                clearConsole();
                mainMenu();
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
            System.out.println("Problemi col server!!");
        }
    }

    private static void mainMenu(){
        System.out.println("Adrenalina");
        System.out.println(" ");
        System.out.println("1: nuova partita");
        System.out.println("2: elenco partite");
        @NotNull var input = in.nextInt();
        switch (input){
            case 1:
                clearConsole();
                newGame();
                break;
            case 2:
                clearConsole();
                joinGame();
                break;
        }
    }
    private static void newGame(){
        clearConsole();
        System.out.println("NUOVA PARTITA");
        System.out.println(" ");
        System.out.println("Inserisci il nome della partita");
        @NotNull var gameName = in.nextLine();
        System.out.println("Inserisci il tempo di timeout");
        @NotNull var timeOut = in.nextInt();
        System.out.println("Scegli il tipo di campo che vuoi avere:");
        System.out.println("1: 5 - 5");
        System.out.println("2: 5 - 6");
        System.out.println("3: 6 - 5");
        System.out.println("4: 6 - 6");
        @NotNull var gameSelection = in.nextInt();
        var gameType = Game.Type.FIVE_FIVE;

        switch (gameSelection){
            case 1:
                gameType = Game.Type.FIVE_FIVE;
                break;
            case 2:
                gameType = Game.Type.FIVE_SIX;
                break;
            case 3:
                gameType = Game.Type.SIX_FIVE;
                break;
            case 4:
                gameType = Game.Type.SIX_SIX;
                break;
        }
        try {
            var room = Client.API.createRoom(Preferences.getToken(),gameName, timeOut, gameType);
            System.out.println("gioco creato correttamente!");
        } catch (RemoteException e) {
            System.out.println("errore nella creazione della partita");
            newGame();
            e.printStackTrace();
        }

    }

    private static List<Room> update() {
            try {
                var roomList = Client.API.getRooms(Preferences.getToken());
                if (roomList == null) {
                    System.out.println("Problemi col server!!");
                    return null;
                }
                var rooms = roomList;
                return rooms;
            } catch (RemoteException ex) {
                ex.printStackTrace();
                System.out.println("Problemi col server!!");
            }
        return null;
    }

    private static void joinGame(){
    var rooms = update();
        System.out.println("_____________________________________________________________________________");
        System.out.println("");
        System.out.printf("%10s %15s %30s","PARTITA", "N. GIOCATORI", "GIOCATORI NELLA LOBBY");
        System.out.println();
        System.out.println("_____________________________________________________________________________");
        System.out.println("");

        for(var room: rooms ){

            System.out.printf("%30s %30s %30s",room.getName(), room.getUsers().size() + "/5", getUsersString(room));
            System.out.println();
        }

        System.out.println("inserisci il nome della partita o '*' per tornare al menù principale");
        var input = in.nextLine();
        switch (input){
            case "*":
                clearConsole();
                mainMenu();
                break;
            default:
                for (var room:rooms){
                    if(room.getName().equalsIgnoreCase(input)){
                        try{
                            Client.API.joinRoom(Preferences.getToken(), room.getUuid());
                        }catch(RemoteException ex){
                            clearConsole();
                            System.out.println("non sono riuscito ad autenticarti, sei stato reindirizzato alla schermata di login");
                            login();
                        }
                        clearConsole();
                    }
                }

        }

    }

    private static String getUsersString(Room rooms){
        String playerList = "(";
        for (int i=0; i<rooms.getUsers().size();i++){
            playerList =playerList + rooms.getUsers().get(i).getNickname();
            if(i != (rooms.getUsers().size()-1)){
                playerList = playerList + ", ";
            }
        }
        playerList = playerList + ")";
        return playerList;
    }

    public static void start() throws RemoteException {
        connType();
    }

    public static void main(String[] args) throws RemoteException {
        start();
    }

}