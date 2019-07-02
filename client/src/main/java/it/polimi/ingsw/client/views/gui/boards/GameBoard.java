package it.polimi.ingsw.client.views.gui.boards;

import it.polimi.ingsw.client.views.gui.sprite.Sprite;
import it.polimi.ingsw.client.views.gui.sprite.fadeinterpolators.LinearFadeInterpolator;
import it.polimi.ingsw.client.views.gui.sprite.pointinterpolators.LinearPointInterpolator;
import it.polimi.ingsw.common.models.*;
import it.polimi.ingsw.common.others.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.others.Utils.applyColorToMask;
import static it.polimi.ingsw.client.others.Utils.blurBorder;

public class GameBoard extends AbstractBoard {
    public GameBoard(@NotNull Game game) throws IOException {
        super(game, blurBorder(Utils.joinBufferedImage(game.getBackImage(), game.getFrontImage()), 30));
        insertStaticSprites();
        setGame(game);
    }

    private static @Nullable Point convertSpriteToPosition(@NotNull Sprite sprite) {
        if (sprite.getX() > 205 && sprite.getX() + sprite.getDimension().getWidth() / 2 < 994 &&
                sprite.getY() > 175 && sprite.getY() + sprite.getDimension().getHeight() / 2 < 744) {
            var x = (int) ((sprite.getX() + sprite.getDimension().getWidth() / 2 - 205) / 220);
            var y = (int) ((sprite.getY() + sprite.getDimension().getWidth() / 2 - 175) / 190);
            return new Point(x, y);
        } else return null;
    }

    @Override
    public void setGame(@NotNull Game game) throws IOException {
        super.setGame(game);
        getSprites().parallelStream().filter(e -> (e.getAssociatedObject() != null && !(e.getAssociatedObject() instanceof Player)) ||
                (e.getTag() == null || !e.getTag().contains("static"))).forEach(e -> e.fade(new LinearFadeInterpolator(1, 0, 1000) {
            @Override
            public void onInterpolationCompleted() {
                e.remove();
            }
        }));

        populateAmmoCard(game);
        populateWeapons(game);
        populateSkulls(game);
        populatePlayers(game);
    }

    @Override
    public void onSpriteClicked(@NotNull Sprite sprite) {
        if (getGameBoardListener() != null)
            getGameBoardListener().spriteSelected(sprite.getAssociatedObject(), convertSpriteToPosition(sprite));
    }

    @Override
    public void onSpriteDragged(@NotNull Sprite sprite) {
        var position = convertSpriteToPosition(sprite);
        if (position != null) {
            if (getGameBoardListener() != null) {
                if (getGameBoardListener().spriteMoved(sprite.getAssociatedObject(), position))
                    sprite.moveTo(new LinearPointInterpolator(sprite.getPosition(), new Point(250 + position.x * 220, 210 + position.y * 190), 250) {
                    });
                else super.onSpriteDragged(sprite);
            } else super.onSpriteDragged(sprite);
        } else super.onSpriteDragged(sprite);
    }

    private void insertStaticSprites() throws IOException {
        var weapon = new Sprite(1044, 227, 143, 237, Utils.readPngImage(Weapon.class, "back"));
        weapon.setDraggable(true);
        weapon.setTag("p:1044,227;static");
        weapon.fade(new LinearFadeInterpolator(0, 1, 1000) {
        });
        addSprite(weapon);

        var powerup = new Sprite(1076, 46, 105, 154, Utils.readPngImage(PowerUp.class, "back"));
        powerup.setDraggable(true);
        powerup.setTag("p:1076,46;static");
        powerup.fade(new LinearFadeInterpolator(0, 1, 1000) {
        });
        addSprite(powerup);

        var ammoCard = new Sprite(59, 738, 84, 84, Utils.readPngImage(AmmoCard.class, "back"));
        ammoCard.setDraggable(true);
        ammoCard.setTag("p:59,738;static");
        ammoCard.fade(new LinearFadeInterpolator(0, 1, 1000) {
        });
        addSprite(ammoCard);
    }

    private void populateAmmoCard(@NotNull Game game) {
        var cells = game.getCells();
        for (var point = new Point(); point.x < cells.length; point.x++)
            for (point.y = 0; point.y < cells[point.x].length; point.y++) {
                Optional.ofNullable(cells[point.x][point.y]).map(Cell::getAmmoCard).ifPresent(e -> {
                    var position = new Point(-100, -100);
                    if (point.x == 0 && point.y == 0) {
                        if (game.getType().getLeft().equals(Game.Type.SIX_SIX.getLeft()))
                            position = new Point(248, 305);
                        if (game.getType().getLeft().equals(Game.Type.FIVE_FIVE.getLeft()))
                            position = new Point(243, 213);
                    }
                    if (point.x == 0 && point.y == 1) {
                        if (game.getType().getLeft().equals(Game.Type.SIX_SIX.getLeft()))
                            position = new Point(425, 208);
                        if (game.getType().getLeft().equals(Game.Type.FIVE_FIVE.getLeft()))
                            position = new Point(426, 298);
                    }
                    if (point.x == 0 && point.y == 3 && game.getType().getRight().equals(Game.Type.SIX_SIX.getRight()))
                        position = new Point(919, 295);
                    if (point.x == 1 && point.y == 1) {
                        if (game.getType().getLeft().equals(Game.Type.SIX_SIX.getLeft()))
                            position = new Point(409, 445);
                        if (game.getType().getLeft().equals(Game.Type.FIVE_FIVE.getLeft()))
                            position = new Point(409, 439);
                    }
                    if (point.x == 1 && point.y == 2) {
                        if (game.getType().getRight().equals(Game.Type.SIX_SIX.getRight()))
                            position = new Point(641, 498);
                        if (game.getType().getRight().equals(Game.Type.FIVE_FIVE.getRight()))
                            position = new Point(644, 463);
                    }
                    if (point.x == 1 && point.y == 3) {
                        if (game.getType().getRight().equals(Game.Type.SIX_SIX.getRight()))
                            position = new Point(823, 498);
                        if (game.getType().getRight().equals(Game.Type.FIVE_FIVE.getRight()))
                            position = new Point(919, 462);
                    }
                    if (point.x == 2 && point.y == 0 && game.getType().getLeft().equals(Game.Type.SIX_SIX.getLeft()))
                        position = new Point(249, 659);
                    if (point.x == 2 && point.y == 1) {
                        if (game.getType().getLeft().equals(Game.Type.SIX_SIX.getLeft()))
                            position = new Point(415, 660);
                        if (game.getType().getLeft().equals(Game.Type.FIVE_FIVE.getLeft()))
                            position = new Point(415, 659);
                    }
                    if (point.x == 2 && point.y == 2) {
                        if (game.getType().getRight().equals(Game.Type.SIX_SIX.getRight()))
                            position = new Point(654, 655);
                        if (game.getType().getRight().equals(Game.Type.FIVE_FIVE.getRight()))
                            position = new Point(621, 660);
                    }
                    try {
                        var ammoSprite = new Sprite(position.x, position.y, 55, 55, e.getFrontImage());
                        ammoSprite.setTag("p:" + position.x + "," + position.y);
                        ammoSprite.setDraggable(true);
                        ammoSprite.setAssociatedObject(e);
                        ammoSprite.fade(new LinearFadeInterpolator(0, 1, 1000) {
                        });
                        addSprite(ammoSprite);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            }
    }

    private void populateWeapons(@NotNull Game game) throws IOException {
        for (var i = 0; i < 3; i++) {
            var yellowWeapon = new Sprite(1035, 480 + i * 121, 140, 210, game.getWeapons(Cell.Color.YELLOW).get(i).getFrontImage());
            yellowWeapon.setRotation(Sprite.Rotation.THREE_HALF_PI);
            yellowWeapon.setTag("p:1035" + "," + (480 + i * 121));
            yellowWeapon.setDraggable(true);
            yellowWeapon.setAssociatedObject(game.getWeapons(Cell.Color.YELLOW).get(i));
            yellowWeapon.fade(new LinearFadeInterpolator(0, 1, 1000) {
            });
            addSprite(yellowWeapon);
        }

        for (var i = 0; i < 3; i++) {
            var redWeapon = new Sprite(1, 308 + i * 121, 140, 210, game.getWeapons(Cell.Color.RED).get(i).getFrontImage());
            redWeapon.setRotation(Sprite.Rotation.HALF_PI);
            redWeapon.setTag("p:1" + "," + (308 + i * 121));
            redWeapon.setDraggable(true);
            redWeapon.setAssociatedObject(game.getWeapons(Cell.Color.RED).get(i));
            redWeapon.fade(new LinearFadeInterpolator(0, 1, 1000) {
            });
            addSprite(redWeapon);
        }

        for (var i = 0; i < 3; i++) {
            var blueWeapon = new Sprite(634 + i * 131, 0, 140, 210, game.getWeapons(Cell.Color.BLUE).get(i).getFrontImage());
            blueWeapon.setRotation(Sprite.Rotation.PI);
            blueWeapon.setTag("p:" + (634 + i * 131) + ",0");
            blueWeapon.setDraggable(true);
            blueWeapon.setAssociatedObject(game.getWeapons(Cell.Color.BLUE).get(i));
            blueWeapon.fade(new LinearFadeInterpolator(0, 1, 1000) {
            });
            addSprite(blueWeapon);
        }
    }

    private void populateSkulls(@NotNull Game game) throws IOException {
        var mask = Utils.readPngImage(Player.class, "mark");
        for (var i = 0; i < game.getSkulls(); i++) {
            var skull = new Sprite((int) (436.5 - 50 * i), 51, 60, 60, Utils.readPngImage(Game.class, "skull"));
            skull.setTag("p:" + (int) (436.5 - 50 * i) + ",51");
            skull.setDraggable(true);
            skull.fade(new LinearFadeInterpolator(0, 1, 1000) {
            });
            addSprite(skull);
        }
        //noinspection OptionalGetWithoutIsPresent
        var killshots = game.getKillshotsTrack().stream().map(e -> game.getPlayers().parallelStream()
                .filter(f -> e.equals(f.getUuid())).findAny().get()).map(Player::getBoardType).collect(Collectors.toList());
        for (int i = 0, x = 96 + 50 * (8 - game.getStartingSkulls()); i < killshots.size(); i++, x += 50) {
            var killshot = new Sprite(x, 56, 35, 50, applyColorToMask(mask, killshots.get(i).getColor()));
            killshot.setTag("p:" + x + ",56");
            killshot.setDraggable(true);
            killshot.fade(new LinearFadeInterpolator(0, 1, 1000) {
            });
            addSprite(killshot);
        }
    }

    private void populatePlayers(@NotNull Game game) {
        var random = new SecureRandom();
        addAllSprite(game.getPlayers().parallelStream().filter(e -> e.getPosition() != null).map(e -> {
            var optSprite = getSprites().parallelStream().filter(f -> f.getAssociatedObject() instanceof Player && f.getAssociatedObject().equals(e)).findAny();
            var x = 250 + e.getPosition().x * 220 - 30 + random.nextInt(60);
            var y = 210 + e.getPosition().y * 190 + random.nextInt(60);
            if (optSprite.isPresent()) {
                optSprite.get().moveTo(new LinearPointInterpolator(optSprite.get().getPosition(), new Point(x, y), 250) {
                });
                return optSprite.get();
            }
            try {
                var sprite = new Sprite(x, y, 80, 80, e.getFrontImage());
                sprite.setTag("p:" + x + "," + y + ";static");
                sprite.setDraggable(true);
                sprite.fade(new LinearFadeInterpolator(0, 1, 1000) {
                });
                sprite.setAssociatedObject(e);
                return sprite;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList()));
        game.getPlayers().parallelStream().filter(e -> e.getPosition() == null).map(e -> getSprites().parallelStream()
                .filter(f -> f.getTag() != null && f.getTag().contains(e.getUuid().toString())).findAny())
                .filter(Optional::isPresent).map(Optional::get).forEach(e -> e.fade(new LinearFadeInterpolator(1, 0, 1000) {
            @Override
            public void onInterpolationCompleted() {
                e.remove();
            }
        }));
    }
}