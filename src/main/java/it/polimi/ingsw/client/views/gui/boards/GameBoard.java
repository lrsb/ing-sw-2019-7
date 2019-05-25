package it.polimi.ingsw.client.views.gui.boards;

import it.polimi.ingsw.client.others.Utils;
import it.polimi.ingsw.client.views.gui.sprite.Sprite;
import it.polimi.ingsw.client.views.gui.sprite.SpriteBoard;
import it.polimi.ingsw.client.views.gui.sprite.SpriteBoardListener;
import it.polimi.ingsw.client.views.gui.sprite.interpolators.LinearInterpolator;
import it.polimi.ingsw.common.models.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.util.Optional;

public class GameBoard extends SpriteBoard implements SpriteBoardListener {
    private @NotNull Game game;
    private @Nullable GameBoardListener gameBoardListener;

    public GameBoard(@NotNull Game game) throws IOException {
        super(Utils.joinBufferedImage(game.getBackImage(), game.getFrontImage()));
        setBoardListener(this);
        this.game = game;
        updateBoard(game);
    }

    public void updateBoard(@NotNull Game game) throws IOException {
        this.game = game;
        removeAllSprites();

        insertStaticSprites();
        populateAmmoCard(game);
        populateWeapons(game);
        populateSkulls(game);
        //TODO
    }

    @Override
    public void onSpriteClicked(@NotNull Sprite sprite) {
        //TODO
        Optional.ofNullable(gameBoardListener).ifPresent(e -> e.doAction(Action.Builder.create(game.getUuid()).buildMoveAction(new Point(0, 0))));
    }

    @Override
    public void onSpriteDragged(@NotNull Sprite sprite) {
        if (sprite.getTag() != null) if (sprite.getTag().startsWith("p:")) {
            var ints = sprite.getTag().substring(2).split(",");
            sprite.moveTo(new LinearInterpolator(sprite.getPosition(), new Point(Integer.parseInt(ints[0]), Integer.parseInt(ints[1])), 250) {
            });
        } else if (sprite.getX() > 205 && sprite.getX() + sprite.getDimension().getWidth() / 2 < 994 &&
                sprite.getY() > 175 && sprite.getY() + sprite.getDimension().getHeight() / 2 < 744)
            spriteMovedTo(sprite, new Point((int) ((sprite.getX() + sprite.getDimension().getWidth() / 2 - 205) / 220),
                    (int) ((sprite.getY() + sprite.getDimension().getWidth() / 2 - 175) / 190)));
    }

    private void spriteMovedTo(@NotNull Sprite sprite, @NotNull Point point) {
        sprite.moveTo(new LinearInterpolator(sprite.getPosition(), new Point((int) (250 + point.getX() * 220), (int) (210 + point.getY() * 190)), 250) {
        });
    }

    public @NotNull Game getGame() {
        return game;
    }

    public void setBoardListener(@Nullable GameBoardListener boardListener) {
        this.gameBoardListener = boardListener;
    }

    private void insertStaticSprites() throws IOException {
        var weapon = new Sprite(1053, 227, 108, 177, Utils.readPngImage(Weapon.class, "back"));
        weapon.setDraggable(true);
        weapon.setTag("p:1053,227");
        addSprite(weapon);

        var powerup = new Sprite(1081, 46, 81, 115, Utils.readPngImage(PowerUp.class, "back"));
        powerup.setDraggable(true);
        powerup.setTag("p:1081,46");
        addSprite(powerup);

        var ammoCard = new Sprite(57, 725, 84, 84, Utils.readPngImage(AmmoCard.class, "back"));
        ammoCard.setDraggable(true);
        ammoCard.setTag("p:57,725");
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
            addSprite(yellowWeapon);
        }

        for (var i = 0; i < 3; i++) {
            var redWeapon = new Sprite(4, 306 + i * 122, 108, 177, game.getWeapons(Cell.Color.RED).get(i).getFrontImage());
            redWeapon.setRotation(Sprite.Rotation.HALF_PI);
            redWeapon.setTag("p:4" + "," + (306 + i * 122));
            redWeapon.setDraggable(true);
            addSprite(redWeapon);
        }

        for (var i = 0; i < 3; i++) {
            var blueWeapon = new Sprite(666 + i * 122, -17, 108, 177, game.getWeapons(Cell.Color.BLUE).get(i).getFrontImage());
            blueWeapon.setRotation(Sprite.Rotation.PI);
            blueWeapon.setTag("p:" + (666 + i * 122) + ",-17");
            blueWeapon.setDraggable(true);
            addSprite(blueWeapon);
        }
    }

    private void populateSkulls(@NotNull Game game) throws IOException {
        for (int i = 0; i < game.getSkulls(); i++) {
            var skull = new Sprite(91 + 54 * i, 48, 50, 50, Utils.readPngImage(Game.class, "skull"));
            skull.setTag("p:" + (91 + 54 * i) + ",48");
            skull.setDraggable(true);
            addSprite(skull);
        }
    }
}