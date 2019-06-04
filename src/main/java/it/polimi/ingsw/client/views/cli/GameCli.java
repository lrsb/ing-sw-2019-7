package it.polimi.ingsw.client.views.cli;

import it.polimi.ingsw.client.views.cli.base.Segue;
import it.polimi.ingsw.common.models.Bounds;
import it.polimi.ingsw.common.models.Cell;
import it.polimi.ingsw.common.models.Game;
import org.jetbrains.annotations.NotNull;

public class GameCli {

    private static @NotNull Segue buildBoard(Game game) {
        var cells = game.getCells();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; i++) {
                switch (cells[i][j].getBounds().getType(Bounds.Direction.N)) {
                    case SAME_ROOM: {

                    }
                }
            }
        }
        //TODO return
    }

    private static Character[][] BuildCell(Cell cell) {
        var northBound = cell.getBounds().getType(Bounds.Direction.N);
        var southBound = cell.getBounds().getType(Bounds.Direction.S);
        var westBound = cell.getBounds().getType(Bounds.Direction.W);
        var eastBound = cell.getBounds().getType(Bounds.Direction.E);
        var cellCli = new Character[9][9]; // why 9? non dovrebbe essere 8?

        switch (northBound) {
            case SAME_ROOM: {
                for (int i = 1; i < 8; i++) {
                    cellCli[i][0] = ' ';
                }
                break;
            }
            case DOOR: {
                for (int i = 1; i < 8; i++) {
                    cellCli[i][0] = '=';
                }
                cellCli[3][0] = '╡';
                cellCli[4][0] = ' ';
                cellCli[5][0] = '╞';
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
        //TODO proseguire
        cell.getAmmoCard().getType()
        return cellCli;
    }
