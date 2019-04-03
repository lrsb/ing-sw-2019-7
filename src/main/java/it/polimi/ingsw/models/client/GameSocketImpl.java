package it.polimi.ingsw.models.client;

import it.polimi.ingsw.models.interfaces.GameListener;
import it.polimi.ingsw.models.interfaces.IGame;
import it.polimi.ingsw.socket.AdrenalinePacket;
import it.polimi.ingsw.socket.AdrenalineSocket;
import it.polimi.ingsw.socket.AdrenalineSocketListener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class GameSocketImpl implements IGame, AdrenalineSocketListener {
    private @NotNull AdrenalineSocket adrenalineSocket;
    private @Nullable GameListener gameListener;

    @Contract(pure = true)
    public GameSocketImpl(@NotNull AdrenalineSocket adrenalineSocket) {
        this.adrenalineSocket = adrenalineSocket;
        adrenalineSocket.setAdrenalineSocketListener(this);
    }

    //TODO: impl
    @Override
    public String makeMove() {
        adrenalineSocket.send(new AdrenalinePacket(AdrenalinePacket.Type.JOIN_ROOM, ""));
        return "";
    }

    @Override
    public void addGameListener(GameListener gameListener) {
        this.gameListener = gameListener;
    }

    //TODO: impl
    @Override
    public void onNewPacket(@NotNull AdrenalinePacket packet) {
        Optional.ofNullable(gameListener).ifPresent(e -> e.onGameUpdated("socket"));
    }

    //TODO: impl
    @Override
    public void onClose(@NotNull AdrenalineSocket socket) {

    }
}
