package it.polimi.ingsw.models.client;

import it.polimi.ingsw.models.interfaces.GameListener;
import it.polimi.ingsw.models.interfaces.IGame;
import it.polimi.ingsw.models.interfaces.RmiGame;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Optional;

public class GameRmiImpl implements IGame {
    private RmiGame rmiGame;
    private @Nullable GameListener gameListener;

    @Contract(pure = true)
    public GameRmiImpl(Remote remote) {
        this.rmiGame = (RmiGame) remote;
    }

    //TODO: impl
    @Override
    public String makeMove() throws RemoteException {
        var response = rmiGame.makeMove();
        new Thread(() -> {
            try {
                var update = rmiGame.waitBoardUpdate();
                Optional.ofNullable(gameListener).ifPresent(e -> e.onGameUpdated(update));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }).start();
        return response;
    }

    @Override
    public void addGameListener(GameListener gameListener) {
        this.gameListener = gameListener;
    }
}
