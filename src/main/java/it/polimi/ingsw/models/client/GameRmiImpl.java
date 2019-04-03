package it.polimi.ingsw.models.client;

import it.polimi.ingsw.models.interfaces.GameListener;
import it.polimi.ingsw.models.interfaces.IGame;
import it.polimi.ingsw.models.interfaces.IRmiGame;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameRmiImpl implements IGame {
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private IRmiGame rmiGame;
    private @Nullable GameListener gameListener;

    @Contract(pure = true)
    public GameRmiImpl(Remote remote) {
        this.rmiGame = (IRmiGame) remote;
    }

    //TODO: impl
    @Override
    public String makeMove() throws RemoteException {
        var response = rmiGame.makeMove();
        executorService.submit(() -> {
            try {
                var update = rmiGame.waitBoardUpdate();
                Optional.ofNullable(gameListener).ifPresent(e -> e.onGameUpdated(update));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        return response;
    }

    @Override
    public void addGameListener(GameListener gameListener) {
        this.gameListener = gameListener;
    }
}
