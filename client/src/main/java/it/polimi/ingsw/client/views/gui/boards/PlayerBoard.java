package it.polimi.ingsw.client.views.gui.boards;

import it.polimi.ingsw.client.controllers.game.ExpoViewController;
import it.polimi.ingsw.client.others.Preferences;
import it.polimi.ingsw.client.views.gui.sprite.Sprite;
import it.polimi.ingsw.client.views.gui.sprite.fadeinterpolators.LinearFadeInterpolator;
import it.polimi.ingsw.client.views.gui.sprite.pointinterpolators.LinearPointInterpolator;
import it.polimi.ingsw.common.models.*;
import it.polimi.ingsw.common.others.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.others.Utils.applyColorToMask;

public class PlayerBoard extends AbstractBoard {
    private @NotNull Player player;
    private @Nullable Sprite weapon;
    private @Nullable Sprite powerup;

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
            for (var i = 0; i < damages.size(); i++) {
                var x = 139 + i * 66;
                var damage = new Sprite(x, 368, 30, 45, applyColorToMask(mask, damages.get(i).getColor()));
                damage.setTag("p:" + x + ",368");
                damage.setClickable(false);
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

        //noinspection OptionalGetWithoutIsPresent
        var marks = player.getMarksTaken().stream().map(e -> game.getPlayers().parallelStream()
                .filter(f -> e.equals(f.getUuid())).findAny().get()).map(Player::getBoardType).collect(Collectors.toList());
        for (var i = 0; i < marks.size(); i++) {
            var x = 575 + i * 28;
            var marksSprite = new Sprite(x, 30, 24, 36, applyColorToMask(mask, marks.get(i).getColor()));
            marksSprite.setTag("p:" + x + ",368");
            marksSprite.setClickable(false);
            marksSprite.setDraggable(true);
            addSprite(marksSprite);
        }

        for (var i = 0; i < AmmoCard.Color.values().length; i++) {
            for (int j = 0; j < player.getColoredCubes(AmmoCard.Color.values()[i]); j++) {
                var x = 963 + j * 66;
                var y = 139 + i * 199;
                var cube = new Sprite(x, y, 50, 50, Utils.readPngImage(AmmoCard.class, AmmoCard.Color.values()[i].name().substring(0, 3)));
                cube.setTag("p:" + x + "," + y);
                cube.setClickable(false);
                cube.setDraggable(true);
                addSprite(cube);
            }
        }


        for (var i = 0; i < player.getWeapons().size(); i++) {
            var weaponImage = player.getWeapons().get(i).getFrontImage();
            var x = 1035 - i * 170;
            var weapon = new Sprite(x, 770, 150, 250, weaponImage);
            weapon.setDraggable(false);
            if (!player.isALoadedGun(player.getWeapons().get(i))) weapon.setFade(0.7);
            weapon.setAssociatedObject(player.getWeapons().get(i));
            addSprite(weapon);
        }


        if (player.getUuid().equals(Preferences.getUuid())) {
            for (var i = 0; i < player.getPowerUps().size(); i++) {
                var powerupImg = player.getPowerUps().get(i);
                var x = 77 + i * 120;
                var powerup = new Sprite(x, -420, 105, 165, powerupImg.getFrontImage());
                powerup.setDraggable(false);
                powerup.setRotation(Sprite.Rotation.PI);
                powerup.setAssociatedObject(powerupImg);
                addSprite(powerup);
            }
        }

        for (var i = 0; i < player.getDeaths(); i++) {
            var skull = new Sprite((int) (249 + 63.7 * i), 620, 60, 60, Utils.readPngImage(Game.class, "skull"));
            skull.setTag("p:" + (int) (249 + 63.7 * i) + ",620");
            skull.setDraggable(true);
            skull.fade(new LinearFadeInterpolator(0, 1, 1000) {
            });
            addSprite(skull);
        }


    }

    @Override
    public void onSpriteClicked(@NotNull Sprite sprite) {
        if (sprite.getAssociatedObject() instanceof Weapon)
            new ExpoViewController(null, sprite.getAssociatedObject()).setVisible(true);
        if (sprite.getAssociatedObject() instanceof PowerUp)//  && player.getUuid().equals(Preferences.getUuid()))
            new ExpoViewController(null, sprite.getAssociatedObject()).setVisible(true);
    }

    public void onSpriteHovered(@NotNull List<Sprite> sprites) {
        var hw = sprites.parallelStream().filter(e -> e.getAssociatedObject() instanceof Weapon).findAny();
        if (hw.isPresent()) {
            if (weapon == null) {
                weapon = hw.get();
                hw.get().moveTo(new LinearPointInterpolator(hw.get().getPosition(), new Point(hw.get().getPosition().x, 110), 800) {
                });
            } else if (weapon != hw.get()) {
                weapon.moveTo(new LinearPointInterpolator(weapon.getPosition(), new Point(weapon.getPosition().x, 770), 800) {
                });
                weapon = null;
                weapon = hw.get();
                hw.get().moveTo(new LinearPointInterpolator(hw.get().getPosition(), new Point(hw.get().getPosition().x, 110), 800) {
                });
            }
        } else if (weapon != null) {
            weapon.moveTo(new LinearPointInterpolator(weapon.getPosition(), new Point(weapon.getPosition().x, 770), 800) {
            });
            weapon = null;
        }

        var pu = sprites.parallelStream().filter(e -> e.getAssociatedObject() instanceof PowerUp).findAny();
        if (pu.isPresent()) {
            if (powerup == null) {
                powerup = pu.get();
                pu.get().moveTo(new LinearPointInterpolator(pu.get().getPosition(), new Point(pu.get().getPosition().x, -25), 700) {
                });
            } else if (powerup != pu.get()) {
                powerup.moveTo(new LinearPointInterpolator(powerup.getPosition(), new Point(powerup.getPosition().x, -420), 700) {
                });
                powerup = null;
                powerup = pu.get();
                pu.get().moveTo(new LinearPointInterpolator(pu.get().getPosition(), new Point(pu.get().getPosition().x, -25), 700) {
                });
            }
        } else if (powerup != null) {
            powerup.moveTo(new LinearPointInterpolator(powerup.getPosition(), new Point(powerup.getPosition().x, -420), 700) {
            });
            powerup = null;
        }
    }

}