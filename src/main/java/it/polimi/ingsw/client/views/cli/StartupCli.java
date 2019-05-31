package it.polimi.ingsw.client.views.cli;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.Server;
import it.polimi.ingsw.client.network.ClientRestImpl;
import it.polimi.ingsw.client.network.ClientRmiImpl;
import it.polimi.ingsw.client.network.ClientSocketImpl;
import it.polimi.ingsw.client.others.Preferences;
import it.polimi.ingsw.client.views.cli.base.Segue;
import it.polimi.ingsw.common.network.exceptions.UserRemoteException;
import org.jetbrains.annotations.NotNull;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class StartupCli {
    public static @NotNull Scanner in = new Scanner(System.in);


    public static @NotNull Segue connType() {
        System.out.println("Ciao, benvenuto in Adrenalina, come vuoi effettuare la connessione?");
        System.out.println("1: SOCKET");
        System.out.println("2: RMI");
        System.out.println("3: HTTP");
        var input = Integer.parseInt(in.nextLine());
        if (input > 3) {
            System.out.println("Scelta non valida dio bastone!");
            return Segue.of("connType");
        }
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
            }
        } catch (Exception e) {
            System.out.println("Problema con la connessione all'host " + ip);
            return Segue.of("connType");
        }
        if (Preferences.isLoggedIn()) return Segue.of("mainMenu", PregameCli.class);
        else return Segue.of("login");
    }

    public static @NotNull Segue login() {
        System.out.println("Inserisci il tuo nickname o * per registrarti");
        var nickname = in.nextLine();
        System.out.println("Inserisci la tua passowrd");
        var psw = in.nextLine();
        if (nickname.equals("*")) return Segue.of("sigup");
        else {
            try {
                Preferences.setToken(Client.API.authUser(nickname, psw));
                return Segue.of("mainMenu", PregameCli.class);

            } catch (UserRemoteException e) {
                System.out.println("Nickname e/o password errate!!");
                return Segue.of("login");
            } catch (RemoteException e) {
                System.out.println(e.getMessage());
                return Segue.of("login");
            }
        }
    }

    public static @NotNull Segue Signup() {
        System.out.println("Inserisci il nickname col quale ti vuoi registrare");
        var nickname = in.nextLine();
        System.out.println("Inserisci la password con la quale ti vuoi registrare");
        var password = in.nextLine();
        try {
            Preferences.setToken(Client.API.createUser(nickname, password));
            return Segue.of("mainMenu", PregameCli.class);
        } catch (UserRemoteException e) {
            System.out.println("Nickname già preso, prendine uno differente");
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        }
        return Segue.of("signup");
    }

    public static @NotNull Segue start() {
        return Segue.of("connType");
    }

    public static @NotNull Segue main(String[] args) {
        return Segue.of("start");
    }

}