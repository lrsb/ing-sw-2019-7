package it.polimi.ingsw.client.views.cli;

import it.polimi.ingsw.client.views.cli.base.Segue;
import it.polimi.ingsw.client.views.cli.base.TypeCell;
import it.polimi.ingsw.common.models.Bounds;
import it.polimi.ingsw.common.models.Cell;
import it.polimi.ingsw.common.models.Game;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static it.polimi.ingsw.common.models.Cell.Color.WHITE;

public class GameCli {
    private static TypeCell[][] buildBoard(@NotNull Game game) {
        var cells = game.getCells();
        var board = new TypeCell[Game.MAX_Y * 15][Game.MAX_X * 30];
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                var cellCli = buildCell(cells[i][j]);
                for (int u = 0; u < game.getPlayers().size(); u++) {
                    try {
                        if (game.getPlayers().get(u).getPosition().getX() == i && game.getPlayers().get(u).getPosition().getY() == j) {
                            board[15 * i + 4][30 * j + 15].setAll('P', game.getPlayers().get(u).getBoardType().escape());
                            board[15 * i + 4][30 * j + 16].setAll('l', game.getPlayers().get(u).getBoardType().escape());
                            board[15 * i + 4][30 * j + 17].setAll('a', game.getPlayers().get(u).getBoardType().escape());
                            board[15 * i + 4][30 * j + 18].setAll('y', game.getPlayers().get(u).getBoardType().escape());
                            board[15 * i + 4][30 * j + 19].setAll('e', game.getPlayers().get(u).getBoardType().escape());
                            board[15 * i + 4][30 * j + 20].setAll('r', game.getPlayers().get(u).getBoardType().escape());
                            board[15 * i + 4][30 * j + 21].setAll((char) u, game.getPlayers().get(u).getBoardType().escape());
                        }
                    } catch (NullPointerException ignored) {
                    }
                }
                for (int k = 0; k < cellCli.length; k++) {
                    System.arraycopy(cellCli[k], 0, board[(15 * i) + k], (30 * j), cellCli[k].length);
                }
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

    public static @NotNull Segue game(Game game) {
        var board = buildBoard(game);
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j].getColor());
                System.out.print(board[i][j].getCharacter());
            }
            System.out.println();
        }

        System.out.print("\u001b[0m");
        return null;
    }
}
