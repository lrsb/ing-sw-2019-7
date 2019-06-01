package it.polimi.ingsw.client.views.gui.boards;

import it.polimi.ingsw.client.controllers.game.WeaponExpoViewController;
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
        setBackground(new Color(55, 55, 55));
        setGame(game);
    }

    @Override
    public void setGame(@NotNull Game game) throws IOException {
        super.setGame(game);
        setBackground(Utils.joinBufferedImage(game.getBackImage(), game.getFrontImage()));
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
            if (sprite.getAssociatedObject() instanceof Weapon.Name)
                new WeaponExpoViewController(null, sprite.getAssociatedObject()).setVisible(true);
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
        var weapon = new Sprite(1044, 227, 143, 237, Utils.readPngImage(Weapon.class, "back"));
        weapon.setDraggable(true);
        weapon.setTag("p:1044,227");
        weapon.fade(new LinearFadeInterpolator(0, 1, 1000) {
        });
        addSprite(weapon);

        var powerup = new Sprite(1076, 47, 105, 154, Utils.readPngImage(PowerUp.class, "back"));
        powerup.setDraggable(true);
        powerup.setTag("p:1076,47");
        powerup.fade(new LinearFadeInterpolator(0, 1, 1000) {
        });
        addSprite(powerup);

        var ammoCard = new Sprite(59, 738, 84, 84, Utils.readPngImage(AmmoCard.class, "back"));
        ammoCard.setDraggable(true);
        ammoCard.setTag("p:59,738");
        ammoCard.fade(new LinearFadeInterpolator(0, 1, 1000) {
        });
        addSprite(ammoCard);
    }

    private void populateAmmoCard(@NotNull Game game) {
        var cells = game.getCells();
        for (var point = new Point(); point.y < cells.length; point.y++)
            for (point.x = 0; point.x < cells[point.y].length; point.x++) {
                Optional.ofNullable(cells[point.y][point.x]).map(Cell::getAmmoCard).ifPresent(e -> {
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
                            position = new Point(621, 661);
                    }
                    try {
                        var ammoSprite = new Sprite(position.x, position.y, 55, 55, e.getFrontImage());
                        ammoSprite.setTag("p:" + position.x + "," + position.y);
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
        for (int i = 0; i < game.getSkulls(); i++) {
            var skull = new Sprite((int) (86 + 50.5 * i), 51, 60, 60, Utils.readPngImage(Game.class, "skull"));
            skull.setTag("p:" + (int) (86 + 50.5 * i) + ",51");
            skull.setDraggable(true);
            skull.fade(new LinearFadeInterpolator(0, 1, 1000) {
            });
            addSprite(skull);
        }
    }
}