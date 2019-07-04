package it.polimi.ingsw.client.controllers.game;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.controllers.base.BaseViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.others.Preferences;
import it.polimi.ingsw.client.others.Utils;
import it.polimi.ingsw.client.views.gui.boards.GameBoard;
import it.polimi.ingsw.client.views.gui.boards.GameBoardListener;
import it.polimi.ingsw.common.models.Action;
import it.polimi.ingsw.common.models.*;
import it.polimi.ingsw.common.network.exceptions.UserRemoteException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.swing.JOptionPane.*;

@SuppressWarnings("RedundantSuppression")
public class GameViewController extends BaseViewController implements GameBoardListener {
    private static final @NotNull Color BACKGROUND_COLOR = Objects.requireNonNull(Utils.hexToColor("1F1E1A"));
    private static final @NotNull Color WHITE_ACCENT = Objects.requireNonNull(Utils.hexToColor("F9F7FB"));

    private JPanel panel;
    private GameBoard gameBoard;
    private JButton playersBoardButton;
    private JButton exitButton;
    private JPanel buttonPanel;
    private JButton moveButton;
    private JButton shootButton;
    private JButton grabButton;
    private JLabel actualPlayerLabel;
    private JLabel moveLabel;
    private JPanel actionPanel;
    private JButton cancelButton;
    private JPanel cancelPanel;
    private JButton spawnButton;
    private JButton skipButton;
    private JButton powerupButton;
    private JButton reloadButton;
    private JButton rulesButton;
    private JButton chatButton;

    private @Nullable PlayersBoardsViewController playersBoardsViewController;
    private @Nullable ChatViewController chatViewController;

    private Game game;
    private boolean yourTurn;
    private @Nullable Action.Type type;
    private @Nullable PowerUp powerUp;
    private Action weaponAction;

    public GameViewController(@NotNull NavigationController navigationController, @NotNull Object... params) throws IOException {
        super("Gioca", 1100, 700, navigationController);
        game = (Game) params[0];
        $$$setupUI$$$();
        setContentPane(panel);
        setResizable(true);
        setEnableFullscreen(true);

        updateBoards(game);

        panel.setBackground(BACKGROUND_COLOR);
        actionPanel.setBackground(BACKGROUND_COLOR);
        gameBoard.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBackground(BACKGROUND_COLOR);
        playersBoardButton.setBackground(BACKGROUND_COLOR);
        exitButton.setBackground(BACKGROUND_COLOR);
        cancelPanel.setBackground(BACKGROUND_COLOR);
        moveLabel.setForeground(WHITE_ACCENT);
        actualPlayerLabel.setForeground(WHITE_ACCENT);

        Preferences.getTokenOrJumpBack(getNavigationController()).ifPresent(e -> {
            try {
                Client.API.addListener(e, f -> {
                    if (f instanceof String) JOptionPane.showMessageDialog(null, f);
                    else if (f instanceof Game && ((Game) f).getUuid().equals(game.getUuid())) try {
                        updateBoards((Game) f);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } finally {
                        if (((Game) f).isCompleted()) navigationController.popViewController();
                    }
                    else if (f instanceof Message) {
                        if (!((Message) f).getFrom().getUuid().equals(Preferences.getUuid()))
                            chatButton.setText("Chat (*)");
                        ChatViewController.messages.add((Message) f);
                    }
                });
            } catch (UserRemoteException ex) {
                ex.printStackTrace();
                Utils.jumpBackToLogin(getNavigationController());
            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });

        setupButtons();
    }

    private void setupButtons() {
        playersBoardButton.addActionListener(e -> {
            if (playersBoardsViewController != null) playersBoardsViewController.dispose();
            playersBoardsViewController = new PlayersBoardsViewController(null, gameBoard.getGame());
            playersBoardsViewController.setVisible(true);
        });

        chatButton.addActionListener(e -> {
            chatButton.setText("Chat");
            if (chatViewController != null) chatViewController.dispose();
            chatViewController = new ChatViewController(null, game.getUuid());
            chatViewController.setVisible(true);
        });

        rulesButton.addActionListener(e -> {
            try {
                Utils.swingOpenRules();
            } catch (URISyntaxException | IOException ex) {
                ex.printStackTrace();
            }
        });

        exitButton.addActionListener(e -> Preferences.getTokenOrJumpBack(getNavigationController()).ifPresent(f -> {
            if (JOptionPane.showConfirmDialog(null, "Vuoi uscire dal gioco?", "Esci", YES_NO_OPTION) == YES_OPTION) {
                try {
                    Client.API.quitGame(f, game.getUuid());
                    getNavigationController().popViewController();
                } catch (UserRemoteException ex) {
                    ex.printStackTrace();
                    Utils.jumpBackToLogin(getNavigationController());
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        }));

        moveButton.addActionListener(e -> {
            if (yourTurn) {
                type = Action.Type.MOVE;
                reloadActionPanel();
            }
        });

        shootButton.addActionListener(e -> {
            if (yourTurn) {
                if (game.getActualPlayer().getWeapons().parallelStream().anyMatch(f -> game.getActualPlayer().isALoadedGun(f) || game.getSkulls() == 0))
                    try {
                        new WeaponSelectorViewController(null,
                                game.getActualPlayer().getWeapons().parallelStream().filter(f -> game.getActualPlayer().isALoadedGun(f) || game.getSkulls() == 0).collect(Collectors.toList()),
                                (WeaponSelectorViewController.WeaponCallback) f -> {
                                    weaponAction = Action.Builder.create(game.getUuid())
                                            .buildFireAction(f, null, null, false,
                                                    0, new ArrayList<>(), null, new ArrayList<>(),
                                                    null, new ArrayList<>(), null);
                                    if (!game.getActualPlayer().isALoadedGun(f))
                                        getPowerup(game.getActualPlayer().getPowerUps(), p -> {
                                            p.forEach(pp -> weaponAction.addPowerUpPayment(pp));
                                            if (game.getActualPlayer().getDamagesTaken().size() > 5 || game.getSkulls() == 0) {
                                                type = Action.Type.MOVE;
                                            } else continueBuildWeapon();
                                        });
                                    else {
                                        if (game.getActualPlayer().getDamagesTaken().size() > 5 || game.getSkulls() == 0) {
                                            type = Action.Type.MOVE;
                                        } else continueBuildWeapon();
                                    }
                                }).setVisible(true);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                else
                    JOptionPane.showMessageDialog(null, (game.getActualPlayer().getWeapons().size() == 0) ? "Non hai armi!" : "Non hai armi cariche");

                type = Action.Type.FIRE;
                reloadActionPanel();
            }
        });

        grabButton.addActionListener(e -> {
            if (yourTurn) {
                type = Action.Type.GRAB_WEAPON;
                reloadActionPanel();
            }
        });

        spawnButton.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(null,
                    Utils.getStrings("cli", "actions").get("borning_action").getAsString(),
                    game.getActualPlayer().getPosition() == null ? "Nasci" : "Rinasci", OK_CANCEL_OPTION) == OK_OPTION) {
                new PowerUpSelectorViewController(null, game.getActualPlayer().getPowerUps(), (PowerUpSelectorViewController.PowerCallback) powerUps -> {
                    if (powerUps.size() == 1)
                        doAction(Action.Builder.create(game.getUuid()).buildReborn(powerUps.get(0).getType(), powerUps.get(0).getAmmoColor()));
                    else if (powerUps.size() > 1) JOptionPane.showMessageDialog(null, "Scegli solo un powerup!");
                    else JOptionPane.showMessageDialog(null, "scegli un powerup!");
                }).setVisible(true);
            }
        });

        reloadButton.addActionListener(e -> {
            if (yourTurn) {
                try {
                    new WeaponSelectorViewController(null,
                            game.getActualPlayer().getWeapons().parallelStream().filter(f -> !game.getActualPlayer().isALoadedGun(f)).collect(Collectors.toList()),
                            (WeaponSelectorViewController.WeaponCallback) f -> {
                                if (JOptionPane.showConfirmDialog(null, "Vuoi pagare con powerup?", "Ricarica", YES_NO_OPTION) == YES_OPTION) {
                                    new PowerUpSelectorViewController(null, game.getActualPlayer().getPowerUps(),
                                            (PowerUpSelectorViewController.PowerCallback) c -> doAction(Action.Builder.create(game.getUuid()).buildReload(f, c))).setVisible(true);
                                } else doAction(Action.Builder.create(game.getUuid()).buildReload(f, null));
                            }).setVisible(true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        skipButton.addActionListener(e -> doAction(Action.Builder.create(game.getUuid()).buildNextTurn()));

        cancelButton.addActionListener(e -> reset());

        powerupButton.addActionListener(e -> {
            if (yourTurn) {
                if (game.getActualPlayer().getPowerUps().size() != 0 && !game.isATagbackResponse()) {
                    new PowerUpSelectorViewController(null, game.getActualPlayer().getPowerUps().parallelStream().filter(f -> f.getType() != PowerUp.Type.TAGBACK_GRENADE).collect(Collectors.toList()), (PowerUpSelectorViewController.PowerCallback) f -> {
                        if (f.size() == 1) {
                            switch (f.get(0).getType()) {
                                case TARGETING_SCOPE:
                                    JOptionPane.showMessageDialog(null, "Scegli il bersaglio che vuoi colpire");
                                    break;
                                case NEWTON:
                                    JOptionPane.showMessageDialog(null, "sposta il bersaglio");
                                    break;
                                case TELEPORTER:
                                    JOptionPane.showMessageDialog(null, "Scegli dove vuoi teletrasportarti");
                                    break;
                            }
                            type = Action.Type.USE_POWER_UP;
                            powerUp = f.get(0);
                            reloadActionPanel();
                        } else if (f.size() > 1) JOptionPane.showMessageDialog(null, "Scegli un solo powerup!");
                        else JOptionPane.showMessageDialog(null, "Scegli un powerup!");
                    }).setVisible(true);
                } else if (game.getActualPlayer().getPowerUps().parallelStream().anyMatch(l -> l.getType() == PowerUp.Type.TAGBACK_GRENADE) && game.isATagbackResponse()) {
                    new PowerUpSelectorViewController(null, game.getActualPlayer().getPowerUps().parallelStream().filter(f -> f.getType() == PowerUp.Type.TAGBACK_GRENADE).collect(Collectors.toList()), (PowerUpSelectorViewController.PowerCallback) f -> {
                        if (f.size() == 1) {
                            doAction(Action.Builder.create(game.getUuid()).buildUsePowerUp(f.get(0).getType(), f.get(0).getAmmoColor(), null, null));
                            reloadActionPanel();
                        } else if (f.size() < 1) JOptionPane.showMessageDialog(null, "Scegli un powerup!");
                        else JOptionPane.showMessageDialog(null, "scegli un solo powerup!");
                    }).setVisible(true);

                } else JOptionPane.showMessageDialog(null, "non hai powerup a disposizione!");
            }
        });
    }

    private void updateBoards(@NotNull Game game) throws IOException {
        this.game = game;
        if (gameBoard != null) gameBoard.setGame(game);
        if (playersBoardsViewController != null) {
            playersBoardsViewController.dispose();
            playersBoardsViewController = null;
        }
        yourTurn = Preferences.getUuid().equals(game.getActualPlayer().getUuid());
        reloadActionPanel();

        actualPlayerLabel.setText(yourTurn ? "TE" : game.getActualPlayer().getNickname());
    }

    private void continueBuildWeapon() {
        var fireModes = Utils.getStrings("cli", "actions", "fire_action", "select_fire_mode").get(weaponAction.getWeapon().name().toLowerCase()).getAsString().split("\n");
        //noinspection unchecked
        if (fireModes.length == 1) ; //TODO: gia scelta
        var list = new JList(fireModes);
        JOptionPane.showMessageDialog(null, list, "Scegli la modalità di fuoco", JOptionPane.PLAIN_MESSAGE);
        var index = list.getSelectedIndices();
        if (index.length > 0) {
            if (index[0] != 0) switch (weaponAction.getWeapon()) {
                case ELECTROSCYTHE:
                case TRACTOR_BEAM:
                case FURNACE:
                case HELLION:
                case FLAMETHROWER:
                case RAILGUN:
                case ZX2:
                case SHOTGUN:
                case POWER_GLOVE:
                case SHOCKWAVE:
                case SLEDGEHAMMER:
                    weaponAction.setAlternativeFire(true);
            }
            var option = index[0];
            switch (weaponAction.getWeapon()) {
                case THOR:
                    if (option == 2) option++;
                    break;
                case PLASMA_GUN:
                case CYBERBLADE:
                    if (option == 1) option++;
                    break;
            }
            var players = 0;
            JOptionPane.showMessageDialog(null, "Utils");

            int finalOption = option;
            var names = playersNicknames();
            //noinspection unchecked
            var namesList = new JList(names.toArray());
            switch (weaponAction.getWeapon()) {
                case LOCK_RIFLE:
                    printMessage("select_target_basic");
                    getTarget(e -> {
                        weaponAction.addBasicTarget(e);
                        if (finalOption == 1) {
                            printMessage("select_target_first");
                            getTarget(f -> {
                                weaponAction.addFirstAdditionalTarget(f);
                                getPowerup(game.getActualPlayer().getPowerUps(),
                                        p -> p.forEach(pp -> weaponAction.addPowerUpPayment(pp)));
                            });
                        }
                    });
                    break;
                case MACHINE_GUN:
                    printMessage("select_target_basic");
                    getTarget(e -> {
                        weaponAction.addBasicTarget(e);
                        if (otherTarget()) {
                            getTarget(f -> {
                                weaponAction.addBasicTarget(f);
                                //todo
                            });
                        } else {

                        }
                    });
                    break;
                case THOR:
                    printMessage("select_target_basic");
                    getTarget(e -> {
                        weaponAction.addBasicTarget(e);
                        if (finalOption > 0) {
                            printMessage("select_target_first");
                            getTarget(f -> {
                                weaponAction.addFirstAdditionalTarget(f);
                                if (finalOption > 1) {
                                    printMessage("select_target_second");
                                    getTarget(g -> {
                                        weaponAction.addSecondAdditionalTarget(g);
                                        getPowerup(game.getActualPlayer().getPowerUps(), p -> {
                                            p.forEach(pp -> weaponAction.addPowerUpPayment(pp));
                                            doAction(weaponAction);
                                        });
                                    });
                                } else {
                                    getPowerup(game.getActualPlayer().getPowerUps(), p -> {
                                        p.forEach(pp -> weaponAction.addPowerUpPayment(pp));
                                        doAction(weaponAction);
                                    });
                                }
                            });
                        } else doAction(weaponAction);
                    });
                    break;
                case PLASMA_GUN:
                    if (JOptionPane.showConfirmDialog(null, "Vuoi selezionare un punto in cui muoverti?", "", YES_NO_OPTION) == YES_OPTION) {
                        printMessage("select_point_basic");
                        getPoint(e -> {
                            weaponAction.setBasicTargetPoint(e);
                            printMessage("select_target_basic");
                            getTarget(f -> {
                                weaponAction.addBasicTarget(f);
                                if (finalOption > 0) {
                                    getPowerup(game.getActualPlayer().getPowerUps(), p -> {
                                        p.forEach(pp -> weaponAction.addPowerUpPayment(pp));
                                        doAction(weaponAction);
                                    });
                                } else doAction(weaponAction);
                            });
                        });
                    } else {
                        printMessage("select_target_basic");
                        getTarget(f -> {
                            weaponAction.addBasicTarget(f);
                            if (finalOption > 0) {
                                getPowerup(game.getActualPlayer().getPowerUps(), p -> {
                                    p.forEach(pp -> weaponAction.addPowerUpPayment(pp));
                                    doAction(weaponAction);
                                });
                            } else doAction(weaponAction);
                        });
                    }
                    break;
                case WHISPER:
                    printMessage("select_target_basic");
                    getTarget(e -> {
                        weaponAction.addBasicTarget(e);
                        doAction(weaponAction);
                    });
                    break;
                case ELECTROSCYTHE:
                    if (weaponAction.getAlternativeFire()) {
                        getPowerup(game.getActualPlayer().getPowerUps(), p -> {
                            p.forEach(pp -> weaponAction.addPowerUpPayment(pp));
                            doAction(weaponAction);
                        });
                    } else doAction(weaponAction);
                    break;
                case TRACTOR_BEAM:
                    printMessage("select_target_basic");
                    getTarget(e -> {
                        weaponAction.addBasicTarget(e);
                        if (weaponAction.getAlternativeFire()) {
                            getPowerup(game.getActualPlayer().getPowerUps(), p -> {
                                p.forEach(pp -> weaponAction.addPowerUpPayment(pp));
                                doAction(weaponAction);
                            });
                        } else {

                        }
                    });
                    break;
                case VORTEX_CANNON:
                    break;
                case FURNACE:
                    break;
                case HEATSEEKER:
                    break;
                case HELLION:
                    break;
                case FLAMETHROWER:
                    break;
                case GRENADE_LAUNCHER:
                    break;
                case ROCKET_LAUNCHER:
                    break;
                case RAILGUN:
                    break;
                case CYBERBLADE:
                    break;
                case ZX2:
                    break;
                case SHOTGUN:
                    break;
                case POWER_GLOVE:
                    break;
                case SHOCKWAVE:
                    if (option == 0) {
                        getTarget(e -> {
                            weaponAction.addBasicTarget(e);
                            if (otherTarget()) getTarget(f -> {
                                weaponAction.addBasicTarget(f);
                                if (otherTarget()) getTarget(g -> {
                                    weaponAction.addBasicTarget(g);
                                    doAction(weaponAction);
                                });
                                else doAction(weaponAction);
                            });
                            else doAction(weaponAction);
                        });
                    } else getPowerup(game.getActualPlayer().getPowerUps(), p -> {
                        p.forEach(pp -> weaponAction.addPowerUpPayment(pp));
                        doAction(weaponAction);
                    });
                    break;
                case SLEDGEHAMMER:
                    printMessage("select_target_basic");
                    getTarget(e -> {
                        weaponAction.addBasicTarget(e);
                        if (finalOption == 1) {
                            printMessage("select_point_basic");
                            getPoint(f -> {
                                weaponAction.setBasicTargetPoint(f);
                                getPowerup(game.getActualPlayer().getPowerUps(), p -> {
                                    p.forEach(pp -> weaponAction.addPowerUpPayment(pp));
                                    doAction(weaponAction);
                                });
                            });
                        } else doAction(weaponAction);
                    });
                    break;
            }
        } else reset();
    }

    private void reset() {
        type = null;
        weaponAction = null;
        reloadActionPanel();
    }

    private @NotNull UUID nicknameToUuid(String nickname) {
        return game.getPlayers().parallelStream().filter(e -> e.getNickname().equals(nickname)).findAny().get().getUuid();
    }

    private @NotNull String uuidToNickname(UUID uuid) {
        return game.getPlayers().parallelStream().filter(e -> e.getUuid().equals(uuid)).findAny().get().getNickname();
    }

    private ArrayList<String> playersNicknames() {
        return game.getPlayers().stream().filter(e -> !e.getUuid().equals(Preferences.getUuid())).map(Player::getNickname).collect(Collectors.toCollection(ArrayList::new));
    }

    private void reloadActionPanel() {
        if (yourTurn) {
            if (type != null) {
                switch (type) {
                    case MOVE:
                        JOptionPane.showMessageDialog(null, "Muovi il tuo giocatore");
                        break;
                    case GRAB_WEAPON:
                    case GRAB_AMMOCARD:
                        JOptionPane.showMessageDialog(null, "Scegli la tessera delle munizioni o l'arma da raccogliere");
                        break;
                    case FIRE:
                        JOptionPane.showMessageDialog(null, "Stai per sparare");
                        break;
                    case USE_POWER_UP:
                        break;
                    case NEXT_TURN:
                        break;
                    case REBORN:
                        break;
                }
                actionPanel.setVisible(false);
                cancelPanel.setVisible(true);
            } else {
                actionPanel.setVisible(true);
                moveButton.setVisible(false);
                shootButton.setVisible(false);
                grabButton.setVisible(false);
                spawnButton.setVisible(false);
                powerupButton.setVisible(false);
                reloadButton.setVisible(false);
                rulesButton.setVisible(true);

                if (game.getActualPlayer().getPosition() == null) { // non è mai spawnato
                    spawnButton.setText("Nasci");
                    spawnButton.setVisible(true);

                } else if (game.isAReborn()) {
                    spawnButton.setText("Rinasci");
                    spawnButton.setVisible(true);

                } else if (game.isATagbackResponse()) {
                    powerupButton.setVisible(true);

                } else if (game.getRemainedActions() == 0) {
                    if (game.getActualPlayer().getWeapons().parallelStream().anyMatch(e -> !game.getActualPlayer().isALoadedGun(e)) && game.getSkulls() != 0)
                        reloadButton.setVisible(true);
                    powerupButton.setVisible(true);

                } else if (!game.isCompleted()) {//azione standard
                    moveButton.setVisible(true);
                    shootButton.setVisible(true);
                    grabButton.setVisible(true);
                    powerupButton.setVisible(true);
                }
                cancelPanel.setVisible(false);
            }
        } else {
            actionPanel.setVisible(false);
            cancelPanel.setVisible(false);
        }
    }

    private void getPoint(GamePickerViewController.BoardPointPickerCallback callback) {
        new GamePickerViewController(null, "Scegli un punto", game, callback).setVisible(true);
    }

    private void getTarget(GamePickerViewController.BoardPlayerPickerCallback callback) {
        new GamePickerViewController(null, "Scegli un obiettivo", game, callback).setVisible(true);
    }

    private void getPowerup(java.util.List<PowerUp> powerUps, PowerUpSelectorViewController.PowerCallback callback) {
        if (JOptionPane.showConfirmDialog(null, "Vuoi usare powerup?", "Pagamento alternativo", YES_NO_OPTION) == YES_OPTION)
            new PowerUpSelectorViewController(null, powerUps, callback).setVisible(true);
        else callback.userDidSelect(new ArrayList<>());
    }

    private boolean otherTarget() {
        return JOptionPane.showConfirmDialog(null, "Vuoi selezionare un altro bersaglio?", "", YES_NO_OPTION) == YES_OPTION;
    }

    @Override
    public void spriteSelected(@Nullable Object data, @Nullable Point point) {
        @Nullable Action todo = null;

        if (data instanceof Player) {
            if (powerUp != null) {
                if (type == Action.Type.USE_POWER_UP && powerUp.getType() == PowerUp.Type.TARGETING_SCOPE) {
                    if ((game.getLastsDamaged().parallelStream().anyMatch(e -> e.equals(((Player) data).getUuid()))))
                        doAction(Action.Builder.create(game.getUuid()).buildUsePowerUp(powerUp.getType(), powerUp.getAmmoColor(), null, ((Player) data).getUuid()));
                    else JOptionPane.showMessageDialog(null, "non lo puoi fare su questo gicatore");
                }
            }
        }
        var availableMovement = 1;
        if (game.getSkulls() == 0 || game.getActualPlayer().getDamagesTaken().size() > 2) {
            if (game.isLastTurn()) availableMovement = 3;
            else availableMovement = 2;
        }
        if (data instanceof Weapon) {
            if (type == Action.Type.GRAB_WEAPON) {
                var cellColor = Stream.of(Cell.Color.values()).filter(f -> game.getWeapons(f).contains(data)).findAny().get();
                for (var ref = new Object() {
                    int i = 0;
                    int j = 0;
                }; ref.i < Game.MAX_Y; ref.i++)
                    for (ref.j = 0; ref.j < Game.MAX_X; ref.j++)
                        if (game.getCell(new Point(ref.i, ref.j)) != null && game.getCell(new Point(ref.i, ref.j)).isSpawnPoint() && game.getCell(new Point(ref.i, ref.j)).getColor() == cellColor) {
                            if (game.canMove(game.getActualPlayer().getPosition(), new Point(ref.i, ref.j), availableMovement)) {
                                int i = ref.i;
                                int j = ref.j;
                                var temp = NO_OPTION;
                                if (game.getActualPlayer().getPowerUps().size() == 0) temp = NO_OPTION;
                                else
                                    temp = JOptionPane.showConfirmDialog(null, "Vuoi pagare con delle PowerUp?", "Raccogli", YES_NO_OPTION);
                                if (game.getActualPlayer().getWeapons().size() == 3)//TODO scegliere arma da scartare ( forse fatto )
                                    try {
                                        int finalTemp = temp;
                                        new WeaponSelectorViewController(null,
                                                game.getActualPlayer().getWeapons().parallelStream().collect(Collectors.toList()),
                                                (WeaponSelectorViewController.WeaponCallback) f -> {
                                                    if (JOptionPane.showConfirmDialog(null, "Vuoi scartare questa arma?", "Conferma", YES_NO_OPTION) == YES_OPTION) {
                                                        if (finalTemp == YES_OPTION) {
                                                            new PowerUpSelectorViewController(null, game.getActualPlayer().getPowerUps(),
                                                                    (PowerUpSelectorViewController.PowerCallback) powerUps ->
                                                                            doAction(Action.Builder.create(game.getUuid()).buildWeaponGrabAction(new Point(i, j), (Weapon) data, f, powerUps)))
                                                                    .setVisible(true);
                                                        } else
                                                            doAction(Action.Builder.create(game.getUuid()).buildWeaponGrabAction(new Point(i, j), (Weapon) data, f, null));
                                                    }
                                                }).setVisible(true);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                if (temp == YES_OPTION) {
                                    new PowerUpSelectorViewController(null, game.getActualPlayer().getPowerUps(),
                                            (PowerUpSelectorViewController.PowerCallback) powerUps ->
                                                    doAction(Action.Builder.create(game.getUuid()).buildWeaponGrabAction(new Point(i, j), (Weapon) data, null, powerUps)))
                                            .setVisible(true);
                                } else {
                                    todo = Action.Builder.create(game.getUuid()).buildWeaponGrabAction(new Point(i, j), (Weapon) data, null, null);
                                }
                            } else JOptionPane.showMessageDialog(null, "arma troppo lontana!");
                        }
            } else new ExpoViewController(null, data).setVisible(true);
        }
        if (data instanceof AmmoCard && type == Action.Type.GRAB_WEAPON) {
            if (game.canMove(game.getActualPlayer().getPosition(), point, availableMovement)) {
                todo = Action.Builder.create(game.getUuid()).buildAmmoCardGrabAction(point);
            } else JOptionPane.showMessageDialog(null, "Troppo lontana!");
        }

        doAction(todo);
    }

    @Override
    public boolean spriteMoved(@Nullable Object data, @Nullable Point point) {
        if (data == null) return false;
        @Nullable Action todo = null;

        if (data instanceof Player && type == Action.Type.MOVE)
            if (Preferences.getUuid().equals(((Player) data).getUuid())) {
                var nMosse = 0;
                if (weaponAction != null) nMosse = game.isLastTurn() ? 2 : 1;
                else if (game.getSkulls() == 0 && !game.isLastTurn()) nMosse = 4;
                else if (!game.isLastTurn()) nMosse = 3;
                if (weaponAction != null) {
                    weaponAction.setDestination(point);
                    continueBuildWeapon();
                } else if (point != null && game.canMove(game.getActualPlayer().getPosition(), point, nMosse))
                    todo = Action.Builder.create(game.getUuid()).buildMoveAction(point);
                else JOptionPane.showMessageDialog(null, "Non ti puoi muovere lì");
            } else JOptionPane.showMessageDialog(null, "Muovi il tuo giocatore");

        if (data instanceof Player && type == Action.Type.USE_POWER_UP && powerUp != null && powerUp.getType() == PowerUp.Type.NEWTON) {
            if (!game.getActualPlayer().getUuid().equals(((Player) data).getUuid())) {
                if (game.canMove(((Player) data).getPosition(), point, 2)) {
                    if ((((Player) data).getPosition().getX() == point.getX() || ((Player) data).getPosition().getY() == point.getY())) {
                        if ((((Player) data).getPosition().getX() == point.getX() && ((Player) data).getPosition().getY() == point.getY()))
                            JOptionPane.showMessageDialog(null, "non ha senso non muovere il giocatore!");
                        else
                            doAction(Action.Builder.create(game.getUuid()).buildUsePowerUp(powerUp.getType(), powerUp.getAmmoColor(), point, ((Player) data).getUuid()));
                    } else
                        JOptionPane.showMessageDialog(null, "non puoi spostare lì " + ((Player) data).getNickname() + "!");
                } else
                    JOptionPane.showMessageDialog(null, "non puoi spostare " + ((Player) data).getNickname() + " così lontano!");
            } else JOptionPane.showMessageDialog(null, "non puoi spostare te stesso!");
        }

        return doAction(todo);
    }

    @Override
    public void boardClicked(@Nullable Point point) { // se null ha cliccato fuori dalla mappa
        if (point != null) {
            if (type == Action.Type.USE_POWER_UP) {
                if ((powerUp != null ? powerUp.getType() : null) == PowerUp.Type.TELEPORTER) {
                    doAction(Action.Builder.create(game.getUuid()).buildUsePowerUp(powerUp.getType(), powerUp.getAmmoColor(), point, null));
                }
            }
        }
    }

    private boolean doAction(@Nullable Action action) {
        reset();
        var token = Preferences.getTokenOrJumpBack(getNavigationController());
        if (action != null && token.isPresent()) try {
            return Client.API.doAction(token.get(), action);
        } catch (UserRemoteException ex) {
            ex.printStackTrace();
            Utils.jumpBackToLogin(getNavigationController());
        } catch (RemoteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return false;
    }

    @Override
    protected void controllerPopped() {
        ChatViewController.messages.clear();
        Preferences.getTokenOrJumpBack(getNavigationController()).ifPresent(e -> {
            try {
                Client.API.removeListener(e);
            } catch (UserRemoteException ex) {
                ex.printStackTrace();
                Utils.jumpBackToLogin(getNavigationController());
            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });
    }

    private void createUIComponents() {
        try {
            gameBoard = new GameBoard(game);
            gameBoard.setBoardListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printMessage(@NotNull String mex) {
        JOptionPane.showMessageDialog(null,
                Utils.getStrings("cli", "weapons_details", weaponAction.getWeapon().name().toLowerCase(), "fire_details").get(mex));
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(gameBoard, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayoutManager(10, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(buttonPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        actualPlayerLabel = new JLabel();
        Font actualPlayerLabelFont = this.$$$getFont$$$(null, -1, 28, actualPlayerLabel.getFont());
        if (actualPlayerLabelFont != null) actualPlayerLabel.setFont(actualPlayerLabelFont);
        actualPlayerLabel.setText("Label");
        buttonPanel.add(actualPlayerLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        playersBoardButton = new JButton();
        playersBoardButton.setText("Visualizza plance");
        buttonPanel.add(playersBoardButton, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exitButton = new JButton();
        exitButton.setText("Esci");
        buttonPanel.add(exitButton, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        buttonPanel.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        moveLabel = new JLabel();
        Font moveLabelFont = this.$$$getFont$$$(null, -1, 24, moveLabel.getFont());
        if (moveLabelFont != null) moveLabel.setFont(moveLabelFont);
        moveLabel.setText("Tocca a:");
        buttonPanel.add(moveLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        actionPanel = new JPanel();
        actionPanel.setLayout(new GridLayoutManager(8, 1, new Insets(0, 0, 0, 0), -1, -1));
        buttonPanel.add(actionPanel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        moveButton = new JButton();
        moveButton.setText("Muovi");
        actionPanel.add(moveButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        shootButton = new JButton();
        shootButton.setText("Spara");
        actionPanel.add(shootButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        grabButton = new JButton();
        grabButton.setText("Raccogli");
        actionPanel.add(grabButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spawnButton = new JButton();
        spawnButton.setText("Nasci");
        actionPanel.add(spawnButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        skipButton = new JButton();
        skipButton.setText("Salta");
        actionPanel.add(skipButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        powerupButton = new JButton();
        powerupButton.setText("Usa powerup");
        actionPanel.add(powerupButton, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        reloadButton = new JButton();
        reloadButton.setText(" Ricarica arma");
        actionPanel.add(reloadButton, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cancelPanel = new JPanel();
        cancelPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        buttonPanel.add(cancelPanel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setText("Annulla mossa");
        cancelPanel.add(cancelButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rulesButton = new JButton();
        rulesButton.setText("Regole");
        buttonPanel.add(rulesButton, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chatButton = new JButton();
        chatButton.setText("Chat");
        buttonPanel.add(chatButton, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}