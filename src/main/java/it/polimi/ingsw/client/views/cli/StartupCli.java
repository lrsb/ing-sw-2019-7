package it.polimi.ingsw.client.views.cli;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.Server;
import it.polimi.ingsw.client.network.ClientRestImpl;
import it.polimi.ingsw.client.network.ClientRmiImpl;
import it.polimi.ingsw.client.network.ClientSocketImpl;
import it.polimi.ingsw.client.others.Preferences;
import it.polimi.ingsw.client.views.cli.base.Segue;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.User;
import it.polimi.ingsw.common.network.exceptions.UserRemoteException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;
import java.util.stream.Collectors;

public class StartupCli {
    public static @NotNull Scanner in = new Scanner(System.in);

    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows"))
                Runtime.getRuntime().exec("cls");
            else Runtime.getRuntime().exec("clear");
        } catch (IOException ignored) {
        }
    }

    public static @NotNull Segue connType() {
        System.out.println("Ciao, benvenuto in Adrenalina, come vuoi effettuare la connessione?");
        System.out.println("1: SOCKET");
        System.out.println("2: RMI");
        System.out.println("3: HTTP");
        var input = Integer.parseInt(in.nextLine());
        System.out.println("Done! Qual è il nome del tuo hostname?");
        var ip = in.nextLine();
        try {
            switch (input) {
                case 1:
                    Client.API = new ClientSocketImpl(ip);
                    break;
                case 2:
                    Client.API = new ClientRmiImpl(LocateRegistry.getRegistry(ip, Server.RMI_PORT).lookup(Server.RMI_NAME));
                    break;
                case 3:
                    Client.API = new ClientRestImpl(ip);
                    break;
                default:
                    return Segue.of("connType");
            }
        } catch (Exception e) {
            System.out.println("Problema con la connessione all'host " + ip);
            return Segue.of("connType");
        }
        if (Preferences.isLoggedIn()) return Segue.of("mainMenu", null);
        else return Segue.of("login");
    }

    public static @NotNull Segue login() {
        System.out.println("Inserisci il tuo nickname");
        var nickname = in.nextLine();
        System.out.println("Inserisci la tua passowrd");
        var psw = in.nextLine();
        try {
            Preferences.setToken(Client.API.authUser(nickname, psw));
            return Segue.of("mainMenu");

        } catch (UserRemoteException e) {

            System.out.println("Nickname e/o password errate!!");
            return Segue.of("login");
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
            return Segue.of("login");
        }
    }

    public static @NotNull Segue mainMenu() {
        System.out.println("Adrenalina");
        System.out.println(" ");
        System.out.println("1: nuova partita");
        System.out.println("2: elenco partite");
        var input = Integer.parseInt(in.nextLine());
        switch (input){
            case 1:
                return Segue.of("newGame");
            case 2:
                return Segue.of("joinGame");
        }
        return Segue.of("mainMenu");
    }

    public static @NotNull Segue newGame() {
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
        var gameSelection = in.nextInt();
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
        if (Preferences.getToken() != null) try {
            var room = Client.API.createRoom(Preferences.getToken(),gameName, timeOut, gameType);
            System.out.println("gioco creato correttamente!");
            return Segue.of("lobby", room.getUuid());
        } catch (RemoteException e) {
            System.out.println("errore nella creazione della partita");
            e.printStackTrace();
            return Segue.of("newGame");
        }
        return Segue.of("newGame");
    }

    public static @NotNull Segue joinGame() {
        if (Preferences.getOptionalToken().isEmpty()) return Segue.of("login");
        try {
            var rooms = Client.API.getRooms(Preferences.getOptionalToken().get());
            System.out.println("_____________________________________________________________________________");
            System.out.println();
            System.out.printf("%10s %15s %30s", "PARTITA", "N. GIOCATORI", "GIOCATORI NELLA LOBBY");
            System.out.println();
            System.out.println("_____________________________________________________________________________");
            System.out.println();

            System.out.println(rooms.parallelStream().map(e ->
                    rooms.indexOf(e) + " " + String.format("%s %15s %40s",
                            e.getName().substring(0, Math.min(e.getName().length(), 10)),
                            e.getUsers().size() + "/5",
                            "(" + e.getUsers().parallelStream().map(User::getNickname).collect(Collectors.joining(", ")) + ")"))
                    .collect(Collectors.joining("\n")));

            System.out.println("inserisci il nome della partita o '*' per tornare al menù principale");
            var input = in.nextLine();

            if (input.equals("*")) {
                return Segue.of("mainMenu");
            } else {
                if (Preferences.getOptionalToken().isPresent()) {
                    try {
                        var number = Integer.parseInt(input);
                        if (number < 0 || number > rooms.size() - 1) {
                            clearConsole();
                            System.out.println("non esiste una partita assegnata a questo numero, ritenta!");
                            return Segue.of("joinGame");
                        }
                        Client.API.joinRoom(Preferences.getOptionalToken().get(), rooms.get(number).getUuid());
                    } catch (NumberFormatException ex) {
                        System.out.println("Questo non è un numero!");
                        return Segue.of("joinGame");
                    } catch (UserRemoteException ex) {
                        return Segue.of("login");
                    } catch (RemoteException ex) {
                        System.out.println(ex.getMessage());
                        return Segue.of("joinGame");
                    }
                } else {
                    return Segue.of("login");
                }
            }

        } catch (RemoteException e) {
            System.out.println(e.getMessage());
            return Segue.of("login");
        }
        return Segue.of("joinGame");
    }

    public static @NotNull Segue start() {
        return Segue.of("connType");
    }

    public static @NotNull Segue main(String[] args) {
        return Segue.of("start");
    }

}