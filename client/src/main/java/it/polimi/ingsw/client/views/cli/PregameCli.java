package it.polimi.ingsw.client.views.cli;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.others.Preferences;
import it.polimi.ingsw.client.views.cli.base.Segue;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.models.User;
import it.polimi.ingsw.common.network.exceptions.UserRemoteException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.stream.Collectors;

/**
 * The class that contains all the views that are displayed before that the game begin
 */
@SuppressWarnings("unused")
public class PregameCli {
    private static Room room;

    /**
     * Display the view that represent the main menu
     *
     * @return The next view
     */
    public static @NotNull Segue mainMenu() {
        System.out.println("Adrenalina");
        System.out.println(" ");
        System.out.println("1: nuova partita");
        System.out.println("2: elenco partite");
        System.out.println("3: logout");
        int input;
        try {
            input = Integer.parseInt(StartupCli.in.nextLine());
        } catch (NumberFormatException e) {
            input = 0;
        }
        switch (input) {
            case 1:
                return Segue.of("newGame");
            case 2:
                return Segue.of("joinGame");
            case 3:
                return Segue.of("login", StartupCli.class);
            default:
                System.out.println("Scegli bene!");
                return Segue.of("mainMenu");
        }
    }

    /**
     * Display the view that allow to create a new game and set up all the settings
     * @return The next view
     */
    public static @NotNull Segue newGame() {
        System.out.println("NUOVA PARTITA");
        System.out.println(" ");
        String gameName;
        int timeout;
        int skulls;
        do {
            System.out.println("Inserisci il nome della partita");
            gameName = StartupCli.in.nextLine();
        } while (gameName.isEmpty());
        do {
            System.out.println("Inserisci il tempo di timeout, deve essere maggiore di 60 secondi e minore di 120");
            timeout = Integer.parseInt(StartupCli.in.nextLine());
        } while (timeout < 60 || timeout > 120);
        do {
            System.out.println("Inserisci il numero di teschi, deve essere compreso fra 5 e 8 inclusi");
            skulls = Integer.parseInt(StartupCli.in.nextLine());
        } while (skulls < 5 || skulls > 8);

        var gameSelection = 0;
        var gameType = Game.Type.FIVE_FIVE;
        do {
            System.out.println("Scegli il tipo di campo che vuoi avere:");
            System.out.println("1: 5 - 5");
            System.out.println("2: 5 - 6");
            System.out.println("3: 6 - 5");
            System.out.println("4: 6 - 6");

            gameSelection = Integer.parseInt(StartupCli.in.nextLine());
            switch (gameSelection) {
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
        } while (gameSelection < 1 || gameSelection > 4);

        if (Preferences.getToken() != null) try {
            var fakeroom = new Room(gameName, new User("pippo"));
            fakeroom.setGameType(gameType);
            fakeroom.setActionTimeout(timeout);
            fakeroom.setSkulls(skulls);
            var room = Client.API.createRoom(Preferences.getToken(), fakeroom);
            System.out.println("gioco creato correttamente!");
            return Segue.of("preLobby", room);
        } catch (RemoteException e) {
            System.out.println("errore nella creazione della partita!");
            e.printStackTrace();
            return Segue.of("newGame");
        }
        return Segue.of("newGame");
    }

    /**
     * Display the view that allow to choose between all the available games that are currently running on the server
     * @return The next view
     */
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

            System.out.println("inserisci l'id della partita o '*' per tornare al menù principale");
            var input = StartupCli.in.nextLine();

            if (input.equals("*")) {
                return Segue.of("mainMenu");
            } else {
                if (Preferences.getOptionalToken().isPresent()) {
                    try {
                        var number = Integer.parseInt(input);
                        if (number < 0 || number > rooms.size() - 1) {
                            System.out.println("non esiste una partita assegnata a questo numero, ritenta!");
                            return Segue.of("joinGame");
                        }
                        var room = Client.API.joinRoom(Preferences.getOptionalToken().get(), rooms.get(number).getUuid());
                        return Segue.of("preLobby", room);
                    } catch (NumberFormatException ex) {
                        System.out.println("Questo non è un numero!");
                        return Segue.of("joinGame");
                    } catch (UserRemoteException ex) {
                        return Segue.of("login", StartupCli.class);
                    } catch (RemoteException ex) {
                        System.out.println(ex.getMessage());
                    }
                } else {
                    return Segue.of("login", StartupCli.class);
                }
            }

        } catch (RemoteException e) {
            System.out.println(e.getMessage());
            return Segue.of("login", StartupCli.class);
        }
        return Segue.of("joinGame");
    }

    /**
     * The view that allow to continually update the lobby
     * @param room the actual room
     * @return The next view
     */
    public static @NotNull Segue preLobby(@NotNull Room room) {
        PregameCli.room = room;
        if (Preferences.getOptionalToken().isEmpty()) return Segue.of("login", StartupCli.class);
        try {
            Client.API.addRoomListener(Preferences.getOptionalToken().get(), room.getUuid(), f -> PregameCli.room = f);
        } catch (UserRemoteException ex) {
            ex.printStackTrace();
            return Segue.of("login", StartupCli.class);
        } catch (RemoteException ex) {
            System.out.println(ex.getMessage());
        }
        return Segue.of("lobby");
    }

    /**
     * The view that allow to pass between the lobby and the game
     * @return The next view
     */
    public static @NotNull Segue postLobby() {
        if (Preferences.getOptionalToken().isEmpty()) return Segue.of("login", StartupCli.class);
        try {
            Client.API.removeRoomListener(Preferences.getOptionalToken().get(), room.getUuid());
            return Segue.of("preGame", GameCli.class, Client.API.getActiveGame(Preferences.getOptionalToken().get()));
        } catch (UserRemoteException ex) {
            ex.printStackTrace();
            return Segue.of("login", StartupCli.class);
        } catch (RemoteException ex) {
            System.out.println(ex.getMessage());
            return Segue.of("joinRoom");
        }
    }

    /**
     * The cli lobby
     * @return The next view
     */
    public static @NotNull Segue lobby() throws IOException, InterruptedException {
        if (Preferences.getOptionalToken().isEmpty()) return Segue.of("login");
        System.out.println("LOBBY");
        System.out.println();
        System.out.println("Nome della partita: " + room.getName());
        System.out.println("Numero di teschi: " + room.getSkulls());
        System.out.println("tipo di mappa: " + room.getGameType().toString());
        System.out.println("Timeout turno: " + room.getActionTimeout());
        System.out.println("Giocatori nella partita: " + room.getUsers().parallelStream().map(User::getNickname).collect(Collectors.joining(", ")));
        if (room.getStartTime() - System.currentTimeMillis() > 0)
            System.out.println("secondi all' avvio:  " + (room.getStartTime() - System.currentTimeMillis()) / 1000);
        if (System.currentTimeMillis() == -1)
            System.out.println("numero di giocatori insufficienti per creare la partita");
        System.out.println("scrivi * per abbandonare la lobby o attendi la partenza della partita");
        if (System.in.available() > 0) {
            if (StartupCli.in.nextLine().equals("*")) {
                try {
                    Client.API.quitRoom(Preferences.getOptionalToken().get(), room.getUuid());
                } catch (UserRemoteException e) {
                    System.out.println("Errore nell'autenticazione, ritorni al login");
                    return Segue.of("login", StartupCli.class);
                } catch (RemoteException e) {
                    System.out.println(e.getMessage());
                }
                return Segue.of("joinGame");
            }
        }
        if (room.isGameCreated()) return Segue.of("postLobby");
        Thread.sleep(1000);
        return Segue.of("lobby");
    }

}
