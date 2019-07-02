package it.polimi.ingsw.client.views.gui.boards;

import it.polimi.ingsw.client.views.gui.sprite.Sprite;
import it.polimi.ingsw.common.models.AmmoCard;
import it.polimi.ingsw.common.models.Game;
import it.polimi.ingsw.common.models.Player;
import it.polimi.ingsw.common.others.Utils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.others.Utils.applyColorToMask;

public class PlayerBoard extends AbstractBoard {
    private @NotNull Player player;

    public PlayerBoard(@NotNull Game game, @NotNull Player player) throws IOException {
        super(game, player.getBackImage());
        this.player = player;
        setGame(game);
    }

    @Override
    public void setGame(@NotNull Game game) throws IOException {
        super.setGame(game);
        var mask = Utils.readPngImage(Player.class, "mark");
        //noinspection OptionalGetWithoutIsPresent
        var damages = player.getDamagesTaken().stream().map(e -> game.getPlayers().parallelStream()
                .filter(f -> e.equals(f.getUuid())).findAny().get()).map(Player::getBoardType).collect(Collectors.toList());
        if (player.isEasyBoard()) {
            for (var i = 0; i < 12; i++) {
                var x = 139 + i * 66;
                var damage = new Sprite(x, 368, 30, 45, applyColorToMask(mask, damages.get(i).getColor()));
                damage.setTag("p:" + x + ",368");
                damage.setDraggable(true);
                addSprite(damage);
            }
        } else {
            for (int i = 0, x = 114; i < Math.min(damages.size(), 10); i++) {
                if (i != 0) {
                    if (i < 3 || i == 5) x += 73;
                    else x += 66;
                }
                var damage = new Sprite(x, 368, 30, 45, applyColorToMask(mask, damages.get(i).getColor()));
                damage.setTag("p:" + x + ",368");
                damage.setDraggable(true);
                addSprite(damage);
            }
            if (damages.size() == 11) {
                var skull = new Sprite(781, 334, 65, 70, Utils.readPngImage(Game.class, "skull"));
                skull.setTag("p:" + "781" + ",334");
                skull.setDraggable(true);
                addSprite(skull);
            }
            if (damages.size() == 12) {
                var target = new Sprite(856, 351, 50, 50, Utils.readPngImage(Game.class, "target"));
                target.setTag("p:" + "856" + ",351");
                target.setDraggable(true);
                addSprite(target);
            }
        }
        var marks = player.getMarksTaken().stream().map(e -> game.getPlayers().parallelStream() //funzionerà quando andrà la applyColorToMask
                .filter(f -> e.equals(f.getUuid())).findAny().get()).map(Player::getBoardType).collect(Collectors.toList());
        for (var i = 0; i < marks.size(); i++) {
            var x = 575 + i * 28;
            var damage = new Sprite(x, 30, 24, 36, applyColorToMask(mask, marks.get(i).getColor()));
            damage.setTag("p:" + x + ",368");
            damage.setDraggable(false);
            addSprite(damage);
        }

        for (var i = 0; i < AmmoCard.Color.values().length; i++) { //AmmoCard.Color.values().length
            for (int j = 0; j < player.getColoredCubes(AmmoCard.Color.values()[i]); j++) { // player.getColoredCubes(AmmoCard.Color.values()[i])
                var cubes = Utils.readPngImage(AmmoCard.class, AmmoCard.Color.values()[i].name().substring(0, 3));
                var x = 963 + j * 66;
                var y = 139 + i * 199;
                var cube = new Sprite(x, y, 50, 50, cubes);
                cube.setTag("p:" + x + "," + y);
                cube.setDraggable(true);
                addSprite(cube);
            }
        }
    }

    @Override
    public void onSpriteClicked(@NotNull Sprite sprite) {
    }
}