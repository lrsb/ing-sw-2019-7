package it.polimi.ingsw.client.views.boards;

import it.polimi.ingsw.client.others.Utils;
import it.polimi.ingsw.client.views.sprite.Sprite;
import it.polimi.ingsw.client.views.sprite.SpriteBoard;
import it.polimi.ingsw.client.views.sprite.SpriteBoardListener;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.stream.Collectors;

public class PlayerBoard extends SpriteBoard implements SpriteBoardListener {
    public PlayerBoard(@NotNull Game game, @NotNull Player player) throws IOException {
        super(new SecureRandom().nextBoolean() ? player.getBackImage() : player.getFrontImage());
        var mask = Utils.readPngImage(Player.class, "mark");
        setBoardListener(this);
        //noinspection OptionalGetWithoutIsPresent
        var damages = player.getDamagesTaken().stream().map(e -> game.getPlayers().parallelStream()
                .filter(f -> e.equals(f.getUuid())).findFirst().get()).map(Player::getBoardType).collect(Collectors.toList());
        for (int i = 0; i < damages.size(); i++) {
            var damage = new Sprite(87 + i * 47 + (i > 1 ? 8 : 0) + (i > 4 ? 8 : 0) + (i > 9 ? 8 : 0), 83,
                    28, 40, Utils.applyColorToMask(mask, damages.get(i).getColor()));
            damage.setDraggable(true);
            addSprite(damage);
        }
    }

    @Override
    public void onSpriteClicked(@NotNull Sprite sprite) {

    }

    @Override
    public void onSpriteDragged(@NotNull Sprite sprite) {

    }
}