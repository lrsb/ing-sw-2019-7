package it.polimi.ingsw.client.views.gui.boards;

import it.polimi.ingsw.client.views.gui.sprite.Sprite;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Player;
import it.polimi.ingsw.common.others.Utils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.others.Utils.applyColorToMask;

public class PlayerBoard extends AbstractBoard {
    private @NotNull Player player;

    public PlayerBoard(@NotNull Game game, @NotNull Player player) throws IOException {
        super(game, new SecureRandom().nextBoolean() ? player.getBackImage() : player.getFrontImage());
        this.player = player;
        setGame(game);
    }

    @Override
    public void setGame(@NotNull Game game) throws IOException {
        super.setGame(game);
        var mask = Utils.readPngImage(Player.class, "mark");
        //noinspection OptionalGetWithoutIsPresent
        var damages = player.getDamagesTaken().stream().map(e -> game.getPlayers().parallelStream()
                .filter(f -> e.equals(f.getUuid())).findFirst().get()).map(Player::getBoardType).collect(Collectors.toList());
        for (int i = 0; i < damages.size(); i++) {
            var x = 87 + i * 47 + (i > 1 ? 8 : 0) + (i > 4 ? 8 : 0) + (i > 9 ? 8 : 0);
            var damage = new Sprite(x, 83, 28, 40, applyColorToMask(mask, damages.get(i).getColor()));
            damage.setTag("p:" + x + ",83");
            damage.setDraggable(true);
            addSprite(damage);
        }
    }

    @Override
    public void onSpriteClicked(@NotNull Sprite sprite) {

    }
}