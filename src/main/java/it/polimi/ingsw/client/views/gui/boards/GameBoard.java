package it.polimi.ingsw.client.views.gui.boards;

import it.polimi.ingsw.client.others.Utils;
import it.polimi.ingsw.client.views.gui.sprite.Sprite;
import it.polimi.ingsw.client.views.gui.sprite.fadeinterpolators.LinearFadeInterpolator;
import it.polimi.ingsw.client.views.gui.sprite.pointinterpolators.LinearPointInterpolator;
import it.polimi.ingsw.common.models.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.Optional;

public class GameBoard extends AbstractBoard {
    public GameBoard(@NotNull Game game) throws IOException {
        super(game, Utils.joinBufferedImage(game.getBackImage(), game.getFrontImage()));
        setGame(game);
    }

    @Override
    public void setGame(@NotNull Game game) throws IOException {
        super.setGame(game);
        getSprites().parallelStream().forEach(e -> e.fade(new LinearFadeInterpolator(1, 0, 1000) {
            @Override
            public void onInterpolationCompleted() {
                e.remove();
            }
        }));

        insertStaticSprites();
        populateAmmoCard(game);
        populateWeapons(game);
        populateSkulls(game);
        //TODO
    }

    @Override
    public void onSpriteClicked(@NotNull Sprite sprite) {
        //TODO
        if (sprite.getAssociatedObject() != null) {
            if (sprite.getAssociatedObject() instanceof Weapon.Name) {
                System.out.println("clicked: " + sprite.getAssociatedObject());
            }
        }
        //Optional.ofNullable(gameBoardListener).ifPresent(e -> e.doAction(Action.Builder.create(getGame().getUuid()).buildMoveAction(new Point(0, 0))));
    }

    @Override
    public void onSpriteDragged(@NotNull Sprite sprite) {
        super.onSpriteDragged(sprite);
        if ((sprite.getTag() == null || !sprite.getTag().startsWith("p")) &&
                sprite.getX() > 205 && sprite.getX() + sprite.getDimension().getWidth() / 2 < 994 &&
                sprite.getY() > 175 && sprite.getY() + sprite.getDimension().getHeight() / 2 < 744)
            spriteMovedTo(sprite, new Point((int) ((sprite.getX() + sprite.getDimension().getWidth() / 2 - 205) / 220),
                    (int) ((sprite.getY() + sprite.getDimension().getWidth() / 2 - 175) / 190)));
    }

    private void spriteMovedTo(@NotNull Sprite sprite, @NotNull Point point) {
        sprite.moveTo(new LinearPointInterpolator(sprite.getPosition(), new Point((int) (250 + point.getX() * 220), (int) (210 + point.getY() * 190)), 250) {
        });
    }

    private void insertStaticSprites() throws IOException {
        var weapon = new Sprite(1053, 227, 108, 177, Utils.readPngImage(Weapon.class, "back"));
        weapon.setDraggable(true);
        weapon.setTag("p:1053,227");
        weapon.fade(new LinearFadeInterpolator(0, 1, 1000) {
        });
        addSprite(weapon);

        var powerup = new Sprite(1081, 46, 81, 115, Utils.readPngImage(PowerUp.class, "back"));
        powerup.setDraggable(true);
        powerup.setTag("p:1081,46");
        powerup.fade(new LinearFadeInterpolator(0, 1, 1000) {
        });
        addSprite(powerup);

        var ammoCard = new Sprite(57, 725, 84, 84, Utils.readPngImage(AmmoCard.class, "back"));
        ammoCard.setDraggable(true);
        ammoCard.setTag("p:57,725");
        ammoCard.fade(new LinearFadeInterpolator(0, 1, 1000) {
        });
        addSprite(ammoCard);
    }

    private void populateAmmoCard(@NotNull Game game) {
        var cells = game.getCells();
        for (var i = 0; i < cells.length; i++)
            for (var j = 0; j < cells[i].length; j++) {
                int finalJ = j;
                int finalI = i;
                Optional.ofNullable(cells[i][j]).map(Cell::getAmmoCard).ifPresent(e -> {
                    try {
                        var ammoSprite = new Sprite(250 + finalI * 220, 210 + finalJ * 190, 45, 45, e.getFrontImage());
                        ammoSprite.setTag("p:" + (250 + finalI * 220) + "," + (210 + finalJ * 190));
                        ammoSprite.setDraggable(true);
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
            var yellowWeapon = new Sprite(1042, 477 + i * 122, 108, 177, game.getWeapons(Cell.Color.YELLOW).get(i).getFrontImage());
            yellowWeapon.setRotation(Sprite.Rotation.THREE_HALF_PI);
            yellowWeapon.setTag("p:1042" + "," + (477 + i * 122));
            yellowWeapon.setDraggable(true);
            yellowWeapon.setAssociatedObject(game.getWeapons(Cell.Color.YELLOW).get(i));
            yellowWeapon.fade(new LinearFadeInterpolator(0, 1, 1000) {
            });
            addSprite(yellowWeapon);
        }

        for (var i = 0; i < 3; i++) {
            var redWeapon = new Sprite(4, 306 + i * 122, 108, 177, game.getWeapons(Cell.Color.RED).get(i).getFrontImage());
            redWeapon.setRotation(Sprite.Rotation.HALF_PI);
            redWeapon.setTag("p:4" + "," + (306 + i * 122));
            redWeapon.setDraggable(true);
            redWeapon.setAssociatedObject(game.getWeapons(Cell.Color.RED).get(i));
            redWeapon.fade(new LinearFadeInterpolator(0, 1, 1000) {
            });
            addSprite(redWeapon);
        }

        for (var i = 0; i < 3; i++) {
            var blueWeapon = new Sprite(666 + i * 122, -17, 108, 177, game.getWeapons(Cell.Color.BLUE).get(i).getFrontImage());
            blueWeapon.setRotation(Sprite.Rotation.PI);
            blueWeapon.setTag("p:" + (666 + i * 122) + ",-17");
            blueWeapon.setDraggable(true);
            blueWeapon.setAssociatedObject(game.getWeapons(Cell.Color.BLUE).get(i));
            blueWeapon.fade(new LinearFadeInterpolator(0, 1, 1000) {
            });
            addSprite(blueWeapon);
        }
    }

    private void populateSkulls(@NotNull Game game) throws IOException {
        for (int i = 0; i < game.getSkulls(); i++) {
            var skull = new Sprite(92 + 54 * i, 48, 50, 50, Utils.readPngImage(Game.class, "skull"));
            skull.setTag("p:" + (92 + 54 * i) + ",48");
            skull.setDraggable(true);
            skull.fade(new LinearFadeInterpolator(0, 1, 1000) {
            });
            addSprite(skull);
        }
    }
}