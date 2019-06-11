package it.polimi.ingsw.client.views.cli;

import it.polimi.ingsw.client.views.cli.base.Segue;
import it.polimi.ingsw.client.views.cli.base.TypeCell;
import it.polimi.ingsw.common.models.Bounds;
import it.polimi.ingsw.common.models.Cell;
import it.polimi.ingsw.common.models.Game;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GameCli {

    private static TypeCell[][] buildBoard(@NotNull Game game) {
        var cells = game.getCells();
        var board = new TypeCell[Game.MAX_X * 9][Game.MAX_Y * 9];
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                var cellCli = buildCell(cells[i][j]);
                for (int k = 0; k < cellCli.length; k++) {
                    for (int l = 0; l < cellCli[k].length; l++) {
                        if (cellCli[k][l] == null) {
                            board[(9 * i) + k][(9 * j) + l].setCharacter(' ');
                        } else {
                            board[(9 * i) + k][(9 * j) + l] = cellCli[k][l];
                        }
                    }
                }
            }
        }
        return board;
    }

    private static TypeCell[][] buildCell(@Nullable Cell cell) {
        var cellCli = new TypeCell[9][9];

        if (cell == null) { // falso
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    cellCli[i][j].setCharacter(' ');
                }
            }
        } else {
            var northBound = cell.getBounds().getType(Bounds.Direction.N);
            var southBound = cell.getBounds().getType(Bounds.Direction.S);
            var westBound = cell.getBounds().getType(Bounds.Direction.W);
            var eastBound = cell.getBounds().getType(Bounds.Direction.E);
            switch (northBound) {
                case SAME_ROOM: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[0][i].setAll(' ', cell.getColor());
                    }
                    break;
                }
                case DOOR: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[i][0].setAll('=', cell.getColor());

                    }
                    cellCli[0][3].setAll('╡', cell.getColor());
                    cellCli[0][4].setAll(' ', cell.getColor());
                    cellCli[0][5].setAll('╞', cell.getColor());
                    break;
                }
                case WALL: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[i][0].setAll('=', cell.getColor());
                    }
                    cellCli[0][0].setAll('╔', cell.getColor());
                    cellCli[8][0].setAll('╗', cell.getColor());
                    break;
                }
            }

            switch (southBound) {
                case SAME_ROOM: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[i][8].setAll(' ', cell.getColor());
                    }
                    cellCli[0][8].setAll('╚', cell.getColor());
                    cellCli[8][8].setAll('╝', cell.getColor());
                    break;
                }
                case DOOR: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[i][8].setAll('=', cell.getColor());
                    }
                    cellCli[0][8].setAll('╚', cell.getColor());
                    cellCli[3][8].setAll('╡', cell.getColor());
                    cellCli[4][8].setAll(' ', cell.getColor());
                    cellCli[5][8].setAll('╞', cell.getColor());
                    cellCli[8][8].setAll('╝', cell.getColor());
                    break;
                }
                case WALL: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[i][8].setAll('=', cell.getColor());
                    }
                    cellCli[0][8].setAll('╚', cell.getColor());
                    cellCli[8][8].setAll('╝', cell.getColor());
                    break;
                }
            }
            switch (eastBound) {
                case SAME_ROOM: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[0][i].setAll(' ', cell.getColor());
                    }
                    break;
                }
                case DOOR: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[0][i].setAll('║', cell.getColor());
                    }
                    cellCli[0][3].setAll('╨', cell.getColor());
                    cellCli[0][4].setAll(' ', cell.getColor());
                    cellCli[0][5].setAll('╥', cell.getColor());
                    break;
                }
                case WALL: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[0][i].setAll('║', cell.getColor());
                    }
                    break;
                }
            }
            switch (westBound) {
                case SAME_ROOM: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[8][i].setAll(' ', cell.getColor());
                    }
                    break;
                }
                case DOOR: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[8][i].setAll('=', cell.getColor());
                    }
                    cellCli[8][3].setAll('╨', cell.getColor());
                    cellCli[8][4].setAll(' ', cell.getColor());
                    cellCli[8][5].setAll('╥', cell.getColor());
                    break;
                }
                case WALL: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[8][i].setAll('║', cell.getColor());
                    }
                    break;
                }
            }
            if (cell.isSpawnPoint()) {
                cellCli[2][2].setCharacter('S');
                cellCli[2][4].setCharacter('P');
            } else {
                if (cell.getAmmoCard() != null) {
                    switch (cell.getAmmoCard().getLeft()) {
                        case RED: {
                            cellCli[3][3].setAll('R', Cell.Color.RED);
                            break;
                        }
                        case BLUE: {
                            cellCli[3][3].setAll('B', Cell.Color.BLUE);
                            break;
                        }
                        case YELLOW: {
                            cellCli[3][3].setAll('Y', Cell.Color.YELLOW);
                            break;
                        }
                    }
                }
                if (cell.getAmmoCard() != null) {
                    switch (cell.getAmmoCard().getType()) {
                        case RED: {
                            cellCli[2][4].setAll('R', Cell.Color.RED);
                            break;
                        }
                        case BLUE: {
                            cellCli[2][4].setAll('B', Cell.Color.BLUE);
                            break;
                        }
                        case YELLOW: {
                            cellCli[2][4].setAll('Y', Cell.Color.YELLOW);
                            break;
                        }
                        case POWER_UP: {
                            cellCli[2][4].setAll('P', Cell.Color.WHITE);
                            break;
                        }
                    }
                }
                if (cell.getAmmoCard() != null) {
                    switch (cell.getAmmoCard().getRight()) {
                        case RED: {
                            cellCli[3][5].setAll('R', Cell.Color.RED);
                            break;
                        }
                        case BLUE: {
                            cellCli[3][5].setAll('B', Cell.Color.BLUE);
                            break;
                        }
                        case YELLOW: {
                            cellCli[3][5].setAll('Y', Cell.Color.YELLOW);
                            break;
                        }
                    }
                }
            }
        }
        return cellCli;
    }

    public static @NotNull Segue game(Game game) {
        var board = buildBoard(game); // come prendo game?
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j].getColor().escape());
                System.out.print(board[i][j].getCharacter());
            }
            System.out.println("\n");
        }
        return null;//TODO da sistemare
    }
}
