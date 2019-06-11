package it.polimi.ingsw.client.views.cli;

import it.polimi.ingsw.client.views.cli.base.Color;
import it.polimi.ingsw.client.views.cli.base.Segue;
import it.polimi.ingsw.common.models.Bounds;
import it.polimi.ingsw.common.models.Cell;
import it.polimi.ingsw.common.models.Game;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GameCli {

    public static @NotNull Segue buildBoard(@NotNull Game game) {//TODO ritornare game dopo i tesr
        var cells = game.getCells();
        var board = new Character[Game.MAX_X * 9][Game.MAX_Y * 9];
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                var cellCli = buildCell(cells[i][j]);
                for (int k = 0; k < cellCli.length; k++) {
                    for (int l = 0; l < cellCli[k].length; l++) {
                        if (cellCli[k][l] == null) {
                            board[(9 * i) + k][(9 * j) + l] = ' ';
                        } else {
                            board[(9 * i) + k][(9 * j) + l] = cellCli[k][l];
                        }
                    }
                }
            }
        }
        return null;
    }

    private static Character[][] buildCell(@Nullable Cell cell) {
        var northBound = cell.getBounds().getType(Bounds.Direction.N);
        var southBound = cell.getBounds().getType(Bounds.Direction.S);
        var westBound = cell.getBounds().getType(Bounds.Direction.W);
        var eastBound = cell.getBounds().getType(Bounds.Direction.E);
        var cellCli = new Character[9][9];
        if (cell == null) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    cellCli[i][j] = ' ';
                }
            }
        } else {
            cell.getColor().escape();
            switch (northBound) {
                case SAME_ROOM: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[0][i] = ' ';
                    }
                    break;
                }
                case DOOR: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[i][0] = '=';
                    }
                    cellCli[0][3] = '╡';
                    cellCli[0][4] = ' ';
                    cellCli[0][5] = '╞';
                    break;
                }
                case WALL: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[i][0] = '=';
                    }
                    cellCli[0][0] = '╔';
                    cellCli[8][0] = '╗';
                    break;
                }
            }

            switch (southBound) {
                case SAME_ROOM: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[i][8] = ' ';
                    }
                    cellCli[0][8] = '╚';
                    cellCli[8][8] = '╝';
                    break;
                }
                case DOOR: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[i][8] = '=';
                    }
                    cellCli[0][8] = '╚';
                    cellCli[3][8] = '╡';
                    cellCli[4][8] = ' ';
                    cellCli[5][8] = '╞';
                    cellCli[8][8] = '╝';
                    break;
                }
                case WALL: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[i][8] = '=';
                    }
                    cellCli[0][8] = '╚';
                    cellCli[8][8] = '╝';
                    break;
                }
            }
            switch (eastBound) {
                case SAME_ROOM: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[0][i] = ' ';
                    }
                    break;
                }
                case DOOR: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[0][i] = '║';
                    }
                    cellCli[0][3] = '╨';
                    cellCli[0][4] = ' ';
                    cellCli[0][5] = '╥';
                    break;
                }
                case WALL: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[0][i] = '║';
                    }
                    break;
                }
            }
            switch (westBound) {
                case SAME_ROOM: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[8][i] = ' ';
                    }
                    break;
                }
                case DOOR: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[8][i] = '=';
                    }
                    cellCli[8][3] = '╨';
                    cellCli[8][4] = ' ';
                    cellCli[8][5] = '╥';
                    break;
                }
                case WALL: {
                    for (int i = 1; i < 8; i++) {
                        cellCli[8][i] = '║';
                    }
                    break;
                }
            }
            Color.ANSI_RESET.escape();
            if (cell.isSpawnPoint()) {
                cellCli[2][2] = 'S';
                cellCli[2][4] = 'P';
            } else {
                if (cell.getAmmoCard() != null) {
                    switch (cell.getAmmoCard().getLeft()) {
                        case RED: {
                            Color.ANSI_RED.escape();
                            cellCli[3][3] = 'R';
                            break;
                        }
                        case BLUE: {
                            Color.ANSI_BLUE.escape();
                            cellCli[3][3] = 'B';
                            break;
                        }
                        case YELLOW: {
                            Color.ANSI_YELLOW.escape();
                            cellCli[3][3] = 'Y';
                            break;
                        }
                    }
                }
                if (cell.getAmmoCard() != null) {
                    switch (cell.getAmmoCard().getType()) {
                        case RED: {
                            Color.ANSI_RED.escape();
                            cellCli[2][4] = 'R';
                            break;
                        }
                        case BLUE: {
                            Color.ANSI_BLUE.escape();
                            cellCli[2][4] = 'B';
                            break;
                        }
                        case YELLOW: {
                            Color.ANSI_YELLOW.escape();
                            cellCli[2][4] = 'Y';
                            break;
                        }
                        case POWER_UP: {
                            Color.ANSI_WHITE.escape();
                            cellCli[2][4] = 'P';
                            break;
                        }
                    }
                }
                if (cell.getAmmoCard() != null) {
                    switch (cell.getAmmoCard().getRight()) {
                        case RED: {
                            Color.ANSI_RED.escape();
                            cellCli[3][5] = 'R';
                            break;
                        }
                        case BLUE: {
                            Color.ANSI_BLUE.escape();
                            cellCli[3][5] = 'B';
                            break;
                        }
                        case YELLOW: {
                            Color.ANSI_YELLOW.escape();
                            cellCli[3][5] = 'Y';
                            break;
                        }
                    }
                }
            }
            Color.ANSI_RESET.escape();
        }
        return cellCli;
    }

}
