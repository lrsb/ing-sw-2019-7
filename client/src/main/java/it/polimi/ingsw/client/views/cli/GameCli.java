package it.polimi.ingsw.client.views.cli;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.others.Preferences;
import it.polimi.ingsw.client.views.cli.base.Segue;
import it.polimi.ingsw.client.views.cli.base.TypeCell;
import it.polimi.ingsw.common.models.AmmoCard;
import it.polimi.ingsw.common.models.Bounds;
import it.polimi.ingsw.common.models.Cell;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.network.exceptions.UserRemoteException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static it.polimi.ingsw.common.models.Cell.Color.*;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class GameCli {
    public static Game game;

    private static TypeCell[][] buildBoard(@NotNull Game game) {
        var cells = game.getCells();
        var board = new TypeCell[Game.MAX_Y * 15][(Game.MAX_X * 30)];
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                var cellCli = buildCell(cells[i][j]);
                if (cells[i][j] != null) {
                    cellCli[1][24].setCharacter('(');
                    cellCli[1][25].setCharacter((char) (i + 48));
                    cellCli[1][26].setCharacter(',');
                    cellCli[1][27].setCharacter((char) (j + 48));
                    cellCli[1][28].setCharacter(')');
                }

                for (int u = 0; u < game.getPlayers().size(); u++) {
                    var position = game.getPlayers().get(u).getPosition();
                    if (position != null && position.getX() == i && position.getY() == j) {
                        if (game.getPlayers().get(u).getNickname().length() > 0)
                            cellCli[4 + u][15].setAll(game.getPlayers().get(u).getNickname().charAt(0), game.getPlayers().get(u).getBoardType().escape());
                        if (game.getPlayers().get(u).getNickname().length() > 1)
                            cellCli[4 + u][16].setAll(game.getPlayers().get(u).getNickname().charAt(1), game.getPlayers().get(u).getBoardType().escape());
                        if (game.getPlayers().get(u).getNickname().length() > 2)
                            cellCli[4 + u][17].setAll(game.getPlayers().get(u).getNickname().charAt(2), game.getPlayers().get(u).getBoardType().escape());
                        if (game.getPlayers().get(u).getNickname().length() > 3)
                            cellCli[4 + u][18].setAll(game.getPlayers().get(u).getNickname().charAt(3), game.getPlayers().get(u).getBoardType().escape());
                        if (game.getPlayers().get(u).getNickname().length() > 4)
                            cellCli[4 + u][19].setAll(game.getPlayers().get(u).getNickname().charAt(4), game.getPlayers().get(u).getBoardType().escape());
                        if (game.getPlayers().get(u).getNickname().length() > 5)
                            cellCli[4 + u][20].setAll(game.getPlayers().get(u).getNickname().charAt(5), game.getPlayers().get(u).getBoardType().escape());
                        if (game.getPlayers().get(u).getNickname().length() > 6)
                            cellCli[4 + u][21].setAll(game.getPlayers().get(u).getNickname().charAt(6), game.getPlayers().get(u).getBoardType().escape());
                    }
                }
                for (int k = 0; k < cellCli.length; k++)
                    System.arraycopy(cellCli[k], 0, board[(15 * i) + k], (30 * j), cellCli[k].length);
            }
        }
        return board;
    }

    private static TypeCell[][] buildCell(@Nullable Cell cell) {
        var cellCli = new TypeCell[15][30];
        for (int i = 0; i < 15; i++)
            for (int j = 0; j < 30; j++) {
                cellCli[i][j] = new TypeCell();
                cellCli[i][j].setCharacter(' ');
            }
        if (cell != null) {
            var northBound = cell.getBounds().getType(Bounds.Direction.N);
            var southBound = cell.getBounds().getType(Bounds.Direction.S);
            var westBound = cell.getBounds().getType(Bounds.Direction.W);
            var eastBound = cell.getBounds().getType(Bounds.Direction.E);
            switch (northBound) {
                case SAME_ROOM: {
                    for (int i = 1; i < 29; i++) cellCli[0][i].setAll(' ', cell.getColor().escape());
                    cellCli[0][0].setAll('╔', cell.getColor().escape());
                    cellCli[0][29].setAll('╗', cell.getColor().escape());
                    break;
                }
                case DOOR: {
                    for (int i = 1; i < 29; i++) cellCli[0][i].setAll('=', cell.getColor().escape());
                    cellCli[0][0].setAll('╔', cell.getColor().escape());
                    cellCli[0][12].setAll('╡', cell.getColor().escape());
                    cellCli[0][13].setAll(' ', cell.getColor().escape());
                    cellCli[0][14].setAll(' ', cell.getColor().escape());
                    cellCli[0][15].setAll(' ', cell.getColor().escape());
                    cellCli[0][16].setAll(' ', cell.getColor().escape());
                    cellCli[0][17].setAll('╞', cell.getColor().escape());
                    cellCli[0][29].setAll('╗', cell.getColor().escape());
                    break;
                }
                case WALL: {
                    for (int i = 1; i < 29; i++) cellCli[0][i].setAll('=', cell.getColor().escape());
                    cellCli[0][0].setAll('╔', cell.getColor().escape());
                    cellCli[0][29].setAll('╗', cell.getColor().escape());
                    break;
                }
            }

            switch (southBound) {
                case SAME_ROOM: {
                    for (int i = 1; i < 29; i++) cellCli[14][i].setAll(' ', cell.getColor().escape());
                    cellCli[14][0].setAll('╚', cell.getColor().escape());
                    cellCli[14][29].setAll('╝', cell.getColor().escape());
                    break;
                }
                case DOOR: {
                    for (int i = 1; i < 29; i++) cellCli[14][i].setAll('=', cell.getColor().escape());
                    cellCli[14][0].setAll('╚', cell.getColor().escape());
                    cellCli[14][12].setAll('╡', cell.getColor().escape());
                    cellCli[14][13].setAll(' ', cell.getColor().escape());
                    cellCli[14][14].setAll(' ', cell.getColor().escape());
                    cellCli[14][15].setAll(' ', cell.getColor().escape());
                    cellCli[14][16].setAll(' ', cell.getColor().escape());
                    cellCli[14][17].setAll('╞', cell.getColor().escape());
                    cellCli[14][29].setAll('╝', cell.getColor().escape());
                    break;
                }
                case WALL: {
                    for (int i = 1; i < 29; i++) cellCli[14][i].setAll('=', cell.getColor().escape());
                    cellCli[14][0].setAll('╚', cell.getColor().escape());
                    cellCli[14][29].setAll('╝', cell.getColor().escape());
                    break;
                }
            }
            switch (eastBound) {
                case SAME_ROOM: {
                    for (int i = 1; i < 14; i++) cellCli[i][29].setAll(' ', cell.getColor().escape());
                    break;
                }
                case DOOR: {
                    for (int i = 1; i < 14; i++) cellCli[i][29].setAll('║', cell.getColor().escape());
                    cellCli[6][29].setAll('╨', cell.getColor().escape());
                    cellCli[7][29].setAll(' ', cell.getColor().escape());
                    cellCli[8][29].setAll(' ', cell.getColor().escape());
                    cellCli[9][29].setAll('╥', cell.getColor().escape());
                    break;
                }
                case WALL: {
                    for (int i = 1; i < 14; i++) cellCli[i][29].setAll('║', cell.getColor().escape());
                    break;
                }
            }
            switch (westBound) {
                case SAME_ROOM: {
                    for (int i = 1; i < 14; i++) cellCli[i][0].setAll(' ', cell.getColor().escape());
                    break;
                }
                case DOOR: {
                    for (int i = 1; i < 14; i++) cellCli[i][0].setAll('║', cell.getColor().escape());
                    cellCli[6][0].setAll('╨', cell.getColor().escape());
                    cellCli[7][0].setAll(' ', cell.getColor().escape());
                    cellCli[8][0].setAll(' ', cell.getColor().escape());
                    cellCli[9][0].setAll('╥', cell.getColor().escape());
                    break;
                }
                case WALL: {
                    for (int i = 1; i < 14; i++) cellCli[i][0].setAll('║', cell.getColor().escape());
                    break;
                }
            }
            if (cell.isSpawnPoint()) {
                cellCli[2][4].setCharacter('╥');
                cellCli[3][2].setCharacter('╞');
                cellCli[3][3].setCharacter('=');
                cellCli[3][4].setCharacter('╬');
                cellCli[3][5].setCharacter('=');
                cellCli[3][6].setCharacter('╡');
                cellCli[4][4].setCharacter('╨');
                cellCli[2][5].setCharacter('◝');
                cellCli[4][5].setCharacter('◞');
                cellCli[2][3].setCharacter('◜');
                cellCli[4][3].setCharacter('◟');
            } else {
                cellCli[1][2].setCharacter('┌');
                cellCli[1][3].setCharacter('─');
                cellCli[1][4].setCharacter('─');
                cellCli[1][5].setCharacter('─');
                cellCli[1][6].setCharacter('─');
                cellCli[1][7].setCharacter('─');
                cellCli[1][8].setCharacter('┐');
                cellCli[2][8].setCharacter('│');
                cellCli[3][8].setCharacter('│');
                cellCli[4][8].setCharacter('┘');
                cellCli[4][7].setCharacter('─');
                cellCli[4][6].setCharacter('─');
                cellCli[4][5].setCharacter('─');
                cellCli[4][4].setCharacter('─');
                cellCli[4][3].setCharacter('─');
                cellCli[4][2].setCharacter('└');
                cellCli[3][2].setCharacter('│');
                cellCli[2][2].setCharacter('│');
                if (cell.getAmmoCard() != null) {
                    switch (cell.getAmmoCard().getLeft()) {
                        case RED: {
                            cellCli[3][4].setAll('R', Cell.Color.RED.escape());
                            break;
                        }
                        case BLUE: {
                            cellCli[3][4].setAll('B', Cell.Color.BLUE.escape());
                            break;
                        }
                        case YELLOW: {
                            cellCli[3][4].setAll('Y', Cell.Color.YELLOW.escape());
                            break;
                        }
                    }
                }
                if (cell.getAmmoCard() != null) {
                    switch (cell.getAmmoCard().getType()) {
                        case RED: {
                            cellCli[2][5].setAll('R', Cell.Color.RED.escape());
                            break;
                        }
                        case BLUE: {
                            cellCli[2][5].setAll('B', Cell.Color.BLUE.escape());
                            break;
                        }
                        case YELLOW: {
                            cellCli[2][5].setAll('Y', Cell.Color.YELLOW.escape());
                            break;
                        }
                        case POWER_UP: {
                            cellCli[2][5].setAll('P', WHITE.escape());
                            break;
                        }
                    }
                }
                if (cell.getAmmoCard() != null) {
                    switch (cell.getAmmoCard().getRight()) {
                        case RED: {
                            cellCli[3][6].setAll('R', Cell.Color.RED.escape());
                            break;
                        }
                        case BLUE: {
                            cellCli[3][6].setAll('B', Cell.Color.BLUE.escape());
                            break;
                        }
                        case YELLOW: {
                            cellCli[3][6].setAll('Y', Cell.Color.YELLOW.escape());
                            break;
                        }
                    }
                }
            }
        }
        return cellCli;
    }

    public static @NotNull Segue preGame(@NotNull Game game) {
        GameCli.game = game;
        if (Preferences.getOptionalToken().isEmpty()) return Segue.of("login", StartupCli.class);
        try {
            Client.API.addListener(Preferences.getOptionalToken().get(), f -> {
                if (f instanceof String) System.out.println((String) f);
                else if (f instanceof Game && ((Game) f).getUuid().equals(game.getUuid())) {
                    GameCli.game = (Game) f;
                }
            });
        } catch (UserRemoteException ex) {
            ex.printStackTrace();
            return Segue.of("login", StartupCli.class);
        } catch (RemoteException ex) {
            System.out.println(ex.getMessage());
        }
        while (!game.isCompleted()) {
            var segue = printGame();
            if (segue != null) return segue;
            GameCli.game = null;

            while (GameCli.game == null) try {
                Thread.onSpinWait();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        return Segue.of("postGame");
    }

    public static @NotNull Segue postGame() {
        if (Preferences.getOptionalToken().isEmpty()) return Segue.of("login", StartupCli.class);
        try {
            Client.API.removeListener(Preferences.getOptionalToken().get());
            return Segue.of("mainMenu", PregameCli.class);
        } catch (UserRemoteException ex) {
            ex.printStackTrace();
            return Segue.of("login", StartupCli.class);
        } catch (RemoteException ex) {
            System.out.println(ex.getMessage());
            return Segue.of("mainMenu", PregameCli.class);
        }
    }

    private static void boardInfo(@NotNull Game game) {
        System.out.println("_____________________________________________________________________________");
        System.out.println();
        System.out.println(game.getTurn());
        System.out.println("Teschi: " + game.getSkulls());
        System.out.print("Colpi Mortali: ");
        game.getKillshotsTrack().forEach(e -> game.getPlayers().parallelStream().filter(f -> f.getUuid().equals(e))
                .forEach(f -> System.out.print(f.getBoardType().escape() + "■ " + "\u001b[0m")));
        System.out.println();
        System.out.print(RED.escape() + "■ " + "\u001b[0m" + "Armi SP rosso: " + game.getWeapons(RED).stream()
                .map(e -> e.getColor().escape() + e.name() + "\u001b[0m").collect(Collectors.joining(", ")));
        System.out.println();
        System.out.print(YELLOW.escape() + "■ " + "\u001b[0m" + "Armi SP giallo: " + game.getWeapons(YELLOW).stream()
                .map(e -> e.getColor().escape() + e.name() + "\u001b[0m").collect(Collectors.joining(", ")));
        System.out.println();
        System.out.print(BLUE.escape() + "■ " + "\u001b[0m" + "Armi SP blu: " + game.getWeapons(BLUE).stream()
                .map(e -> e.getColor().escape() + e.name() + "\u001b[0m").collect(Collectors.joining(", ")));
        System.out.println();
        System.out.println();
        System.out.println("_____________________________________________________________________________");
    }

    private static void playerInfo(@NotNull Game game) {
        System.out.println("informazioni sui giocatori");
        System.out.println("_____________________________________________________________________________");
        System.out.printf("%1s %15s %1s %5s %10s %8s %18s", "", "NOME", "", "VAL BOARD", "MUN R - Y - B", "MORTI", "PUNTI ACCUMULATI");
        System.out.println();
        game.getPlayers().forEach(e -> {
            System.out.printf("%1s %15s %1s %10s %14s %8s %12s", e.getBoardType().escape(), e.getNickname(), "\u001b[0m", e.getMaximumPoints(),
                    e.getColoredCubes(AmmoCard.Color.RED) + "   " + e.getColoredCubes(AmmoCard.Color.YELLOW) + "   " + e.getColoredCubes(AmmoCard.Color.BLUE), e.getDeaths(),
                    e.getPoints());
            System.out.println();
            System.out.print("ARMI: " + e.getWeapons().stream().map(c -> c.getColor().escape() + c.name() + "\u001b[0m").collect(Collectors.joining(", ")));
            System.out.println();
            System.out.print(e.getUuid().equals(Preferences.getUuid()) ? ("POWERUP: " + e.getPowerUps().stream().map(c -> c.getAmmoColor().escape() + c.getType().name() + "\u001b[0m").collect(Collectors.joining(", ")))
                    : ("N° POWERUP: " + e.getPowerUps().size()));
            System.out.println();
            System.out.print("COLPI SUBITI: ");
            e.getDamagesTaken().forEach(f -> game.getPlayers().parallelStream().filter(g -> g.getUuid().equals(f))
                    .forEach(g -> System.out.print(g.getBoardType().escape() + "■ " + "\u001b[0m")));
            System.out.println();
            System.out.print("MARCHI: ");
            e.getMarksTaken().forEach(f -> game.getPlayers().parallelStream().filter(g -> g.getUuid().equals(f))
                    .forEach(g -> System.out.print(g.getBoardType().escape() + "■ " + "\u001b[0m")));
            System.out.println("\n________________________________________________________________________");
        });
    }

    public static @Nullable Segue printGame() {
        var board = buildBoard(game);
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j].getColor());
                System.out.print(board[i][j].getCharacter());
            }
            System.out.println();
        }
        System.out.print("\u001b[0m");
        boardInfo(game);
        playerInfo(game);
        if (game.getActualPlayer().getUuid().equals(Preferences.getUuid())) {
            try {
                new ActionManager().actionMenu(game);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            System.out.print(game.getActualPlayer().getBoardType().escape() + game.getActualPlayer().getNickname());
            if (game.isAReborn()) System.out.println("\u001b[0m" + " sta rinascendo");
            else if (game.isATagbackResponse()) System.out.println("\u001b[0m" + " sta valutando se rispondere al fuoco");
            else System.out.println("\u001b[0m" + " sta facendo la sua mossa...");
        }
        //TODO if (exited) return Segue.of(PregameCli.class, "mainMenu");
        return null;
    }

    private void printRanking(@NotNull Game game) {
        final List<ArrayList<UUID>> ranking = game.getFinalRanking();
        if (ranking == null) return;
        System.out.println("CLASSIFICA\n");
        for (int i = 0; i < ranking.size(); i++) {
            System.out.print((i + 1) + ". ");
            for (int j = 0; j < ranking.get(i).size(); j++) {
                var uuid = ranking.get(i).get(j);
                game.getPlayers().stream().filter(e -> e.getUuid().equals(uuid))
                        .forEach(e -> System.out.print(e.getBoardType().escape() + e.getNickname() +
                                "\u001b[0m" + " (" + e.getBoardType().escape() + e.getPoints() + "\u001b[0m" + ")"));
                if (j + 1 < ranking.get(i).size()) System.out.print(", ");
                else System.out.println();
            }
        }
    }
}
