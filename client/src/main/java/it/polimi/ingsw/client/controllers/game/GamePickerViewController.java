package it.polimi.ingsw.client.controllers.game;

import it.polimi.ingsw.client.controllers.base.BaseViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.views.gui.boards.GameBoard;
import it.polimi.ingsw.client.views.gui.boards.GameBoardListener;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.UUID;

public class GamePickerViewController extends BaseViewController implements GameBoardListener {
    private final Game game;
    private JPanel panel;
    private GameBoard gameBoard;
    private BoardPlayerPickerCallback playerPickerCallback;
    private BoardPointPickerCallback pointPickerCallback;

    public GamePickerViewController(@Nullable NavigationController navigationController, @NotNull Object... args) {
        super((String) args[0], 800, 600, navigationController);
        game = (Game) args[1];
        setContentPane(panel);
        gameBoard.setBoardListener(this);
        if (args[2] instanceof BoardPlayerPickerCallback) playerPickerCallback = (BoardPlayerPickerCallback) args[2];
        else if (args[2] instanceof BoardPointPickerCallback) pointPickerCallback = (BoardPointPickerCallback) args[2];
    }

    @Override
    public void spriteSelected(@Nullable Object data, @Nullable Point point) {
        if (playerPickerCallback != null) {
            if (data instanceof Player) {
                if (!game.getActualPlayer().equals(data)) {
                    playerPickerCallback.onPlayerSelected(((Player) data).getUuid());
                    dispose();
                } else JOptionPane.showMessageDialog(null, "Scegli un giocatore che non sia tu");
            } else JOptionPane.showMessageDialog(null, "Scegli un giocatore");
        } else JOptionPane.showMessageDialog(null, "Devi scegliere un punto sulla mappa");
    }

    @Override
    public boolean spriteMoved(@Nullable Object data, @Nullable Point point) {
        return false;
    }

    @Override
    public void boardClicked(@Nullable Point point) {
        if (pointPickerCallback != null) {
            if (point != null) {
                pointPickerCallback.onPointSelected(point);
                dispose();
            } else JOptionPane.showMessageDialog(null, "Seleziona meglio il punto");
        } else JOptionPane.showMessageDialog(null, "Devi scegliere un giocatore");
    }

    private void createUIComponents() {
        try {
            gameBoard = new GameBoard(game);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface BoardPointPickerCallback {
        void onPointSelected(@NotNull Point point);
    }

    public interface BoardPlayerPickerCallback {
        void onPlayerSelected(@NotNull UUID uuid);
    }
}