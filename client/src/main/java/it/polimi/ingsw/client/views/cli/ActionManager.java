package it.polimi.ingsw.client.views.cli;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.others.Preferences;
import it.polimi.ingsw.client.others.Utils;
import it.polimi.ingsw.common.models.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActionManager {
    private final String stdColor = "\u001b[0m";
    private final String invalidChoice = "Scelta non valida";
    private boolean done = false;
    private @Nullable Weapon weapon;
    private @Nullable Weapon discardedWeapon;
    private @Nullable Point destination;
    private @Nullable PowerUp.Type powerUpType;
    private @Nullable AmmoCard.Color color;
    private @NotNull ArrayList<PowerUp> powerUpPayment = new ArrayList<>();

    private boolean alternativeFire = false;
    private int options = 0;
    private @NotNull ArrayList<UUID> basicTarget = new ArrayList<>();
    private @Nullable Point basicTargetPoint;

    private @NotNull ArrayList<UUID> firstAdditionalTarget = new ArrayList<>();
    private @Nullable Point firstAdditionalTargetPoint;

    private @NotNull ArrayList<UUID> secondAdditionalTarget = new ArrayList<>();
    private @Nullable Point secondAdditionalTargetPoint;

    private @Nullable UUID target;

    private void clearAll() {
        weapon = null;
        discardedWeapon = null;
        destination = null;
        powerUpType = null;
        color = null;
        powerUpPayment.clear();
        alternativeFire = false;
        options = 0;
        basicTarget.clear();
        basicTargetPoint = null;
        firstAdditionalTarget.clear();
        firstAdditionalTargetPoint = null;
        secondAdditionalTarget.clear();
        secondAdditionalTargetPoint = null;
        target = null;
    }

    private void actionMenu(@NotNull Game game) throws FileNotFoundException, RemoteException {
        clearAll();
        try {
            if (game.getActualPlayer().getPosition() == null) {
                System.out.println(game.getActualPlayer().getNickname() + " è la tua prima mossa: scegli " +
                        "una powerup da scartare per poter entrare in gioco!\nRicorda: il colore della carta" +
                        " scartata determinerà lo spawnpoint in cui apparirai.");
                selectReborningPowerUp(game);
            } else {
                if (game.isAReborn()) {
                    System.out.println(game.getActualPlayer().getNickname() + " sei morto...\nScegli una " +
                            "powerup da scartare per rientrare in gioco!");
                    selectReborningPowerUp(game);
                } else if (game.isATagbackResponse()) {
                    System.out.println("Hai l'occasione di prenderti una rivincita!");
                    selectTagbackResponse(game);
                } else {
                    System.out.println(Utils.getStrings("cli").get("select_action").getAsString());
                    if (!game.isLastTurn() && game.getRemainedActions() > 0) selectStandardAction(game);
                    else if (!game.isLastTurn()) selectLastAction(game);
                    else if (game.getRemainedActions() > 0) selectUltimateAction(game);
                    else selectLastAction(game);
                }
            }
        } catch (InterruptedException e) {
            actionMenu(game);
        }
    }

    private void selectReborningPowerUp(@NotNull Game game) throws InterruptedException {
        if (game.getActualPlayer().getPowerUps().size() == 1) {
            try {
                System.out.println("Ops...non hai molta scelta");
                Client.API.doAction(Preferences.getToken(), Action.Builder.create(game.getUuid())
                        .buildReborn(game.getActualPlayer().getPowerUps().get(0).getType(), game.getActualPlayer().getPowerUps().get(0).getAmmoColor()));
                return;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < game.getActualPlayer().getPowerUps().size(); i++)
            System.out.println((1 + i) + ". " + game.getActualPlayer().getPowerUps().get(i).getAmmoColor().escape() +
                    game.getActualPlayer().getPowerUps().get(i).getType().name() + stdColor);
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= game.getActualPlayer().getPowerUps().size()) {
            try {
                Client.API.doAction(Preferences.getToken(), Action.Builder.create(game.getUuid())
                        .buildReborn(game.getActualPlayer().getPowerUps().get(Integer.parseInt(choice) - 1).getType(),
                                game.getActualPlayer().getPowerUps().get(Integer.parseInt(choice) - 1).getAmmoColor()));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(invalidChoice);
            selectReborningPowerUp(game);
        }
    }

    private void selectAlternativePayment(@NotNull Game game) throws InterruptedException {
        System.out.println("Inserisci pagamento alternativo (se vuoi)");
        List<PowerUp> selectablePowerUps = new ArrayList<>(game.getActualPlayer().getPowerUps());
        powerUpPayment.forEach(selectablePowerUps::remove);
        if (selectablePowerUps.isEmpty()) return;
        for (int i = 0; i < selectablePowerUps.size(); i++)
            System.out.println((1 + i) + ". " + selectablePowerUps.get(i).getAmmoColor().escape() +
                    selectablePowerUps.get(i).getType().name() + stdColor);
        System.out.println((selectablePowerUps.size() + 1) + ". Fine selezione pagamento alternativo");
        System.out.println(Utils.getStrings("cli").get("back_to_menu").getAsString());
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= selectablePowerUps.size()) {
            powerUpPayment.add(selectablePowerUps.get(Integer.parseInt(choice) - 1));
            selectAlternativePayment(game);
        } else if (Integer.parseInt(choice) != selectablePowerUps.size() + 1) {
            System.out.println(invalidChoice);
            selectAlternativePayment(game);
        }
    }

    private void selectTagbackResponse(@NotNull Game game) throws InterruptedException, RemoteException {
        List<PowerUp> selectableTagbackGrenade = new ArrayList<>();
        game.getActualPlayer().getPowerUps().stream().filter(e -> e.getType().equals(PowerUp.Type.TAGBACK_GRENADE)).forEach(selectableTagbackGrenade::add);
        for (int i = 0; i < selectableTagbackGrenade.size(); i++)
            System.out.println((i + 1) + ". " + selectableTagbackGrenade.get(i).getAmmoColor().escape() +
                    selectableTagbackGrenade.get(i).getType().name() + stdColor);
        System.out.println((selectableTagbackGrenade.size() + 1) + ". Non rispondere");
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) < selectableTagbackGrenade.size() + 1)
            Client.API.doAction(Preferences.getToken(), Action.Builder.create(game.getUuid()).buildUsePowerUp(selectableTagbackGrenade.get(Integer.parseInt(choice) - 1).getType(),
                    selectableTagbackGrenade.get(Integer.parseInt(choice) - 1).getAmmoColor(), destination, target));
        else if (Integer.parseInt(choice) == selectableTagbackGrenade.size() + 1)
            Client.API.doAction(Preferences.getToken(), Action.Builder.create(game.getUuid()).buildNextTurn());
        else {
            System.out.println(invalidChoice);
            selectTagbackResponse(game);
        }
    }

    private void selectStandardAction(@NotNull Game game) throws InterruptedException, FileNotFoundException, RemoteException {
        if (game.getSkulls() > 0)
            System.out.println(Utils.getStrings("cli", "possible_actions").get(game.getActualPlayer().getDamagesTaken().size() < 3 ?
                    "standard_actions" : game.getActualPlayer().getDamagesTaken().size() < 6 ? "standard_actions_plus1" :
                    "standard_actions_plus2").getAsString());
        else System.out.println(Utils.getStrings("cli", "possible_actions").get("frenzy_actions"));
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) < 7) {
            switch (Integer.parseInt(choice)) {
                case 1:
                    System.out.println(Utils.getStrings("cli", "actions", "move_action").get("select_square").getAsString());
                    selectMyDestination(game, game.getSkulls() > 0 ? 3 : 4);
                    Client.API.doAction(Preferences.getToken(), Action.Builder.create(game.getUuid()).buildMoveAction(destination));
                    break;
                case 2:
                    System.out.println(Utils.getStrings("cli", "actions", "grab_action").get("grab_ammo_square").getAsString());
                    selectGrabAmmoDestination(game, game.getActualPlayer().getDamagesTaken().size() > 2 || game.getSkulls() == 0 ? 2 : 1);
                    break;
                case 3:
                    System.out.println(Utils.getStrings("cli", "actions", "grab_action", "grab_weapon").get("select_square").getAsString());
                    selectGrabWeaponDestination(game, game.getActualPlayer().getDamagesTaken().size() > 2 || game.getSkulls() == 0 ? 2 : 1);
                    System.out.println(Utils.getStrings("cli", "actions", "grab_action", "grab_weapon").get("select_weapon").getAsString());
                    selectSPWeapon(game);
                    if (game.getActualPlayer().getWeapons().size() > 2) {
                        System.out.println(Utils.getStrings("cli", "actions", "grab_action", "grab_weapon").get("discard_weapon").getAsString());
                        selectMyDiscardWeapon(game);
                    }
                    System.out.println(Utils.getStrings("cli", "actions").get("alternative_payment").getAsString());
                    selectAlternativePayment(game);
                    Client.API.doAction(Preferences.getToken(), Action.Builder.create(game.getUuid())
                            .buildWeaponGrabAction(destination, weapon, discardedWeapon, powerUpPayment));
                    break;
                case 4:
                    System.out.println(Utils.getStrings("cli", "actions", "fire_action").get("select_weapon").getAsString());
                    selectMyFireWeapon(game);
                    if (game.getActualPlayer().getDamagesTaken().size() > 5 || game.getSkulls() == 0) {
                        System.out.println(Utils.getStrings("cli", "actions", "fire_action").get("select_move_square").getAsString());
                        selectMyDestination(game, 1);
                    } else destination = game.getActualPlayer().getPosition();
                    buildFireAction(game);
                    Client.API.doAction(Preferences.getToken(), Action.Builder.create(game.getUuid())
                            .buildFireAction(weapon, destination, powerUpPayment, alternativeFire, options,
                                    basicTarget, basicTargetPoint, firstAdditionalTarget, firstAdditionalTargetPoint,
                                    secondAdditionalTarget, secondAdditionalTargetPoint));
                    break;
                case 5:
                    buildUsePowerUp(game);
                    break;
                case 6:
                    Client.API.doAction(Preferences.getToken(), Action.Builder.create(game.getUuid()).buildNextTurn());
                    break;
            }
        } else {
            System.out.println(invalidChoice);
            selectStandardAction(game);
        }
    }

    private void selectLastAction(@NotNull Game game) throws FileNotFoundException, InterruptedException, RemoteException {
        System.out.println(Utils.getStrings("cli", "possible_actions").get("last_standard_actions").getAsString());
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) < 4) {
            switch (Integer.parseInt(choice)) {
                case 1:
                    buildUsePowerUp(game);
                    break;
                case 2:
                    System.out.println(Utils.getStrings("cli", "actions", "reload_action").get("select_weapon").getAsString());
                    selectMyReloadWeapon(game);
                    System.out.println(Utils.getStrings("cli", "actions").get("alternative_payment").getAsString());
                    selectAlternativePayment(game);
                    Client.API.doAction(Preferences.getToken(), Action.Builder.create(game.getUuid()).buildReload(weapon, powerUpPayment));
                    break;
                case 3:
                    Client.API.doAction(Preferences.getToken(), Action.Builder.create(game.getUuid()).buildNextTurn());
                    break;
            }
        } else {
            System.out.println(invalidChoice);
            selectStandardAction(game);
        }
    }

    private void selectUltimateAction(@NotNull Game game) throws FileNotFoundException, InterruptedException, RemoteException {
        System.out.println(Utils.getStrings("cli", "possible_actions").get("last_turn_actions").getAsString());
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) < 6) {
            switch (Integer.parseInt(choice)) {
                case 1:
                    System.out.println(Utils.getStrings("cli", "actions", "grab_action").get("grab_ammo_square").getAsString());
                    selectGrabAmmoDestination(game, 3);
                    break;
                case 2:
                    System.out.println(Utils.getStrings("cli", "actions", "grab_action", "grab_weapon").get("select_square").getAsString());
                    selectGrabWeaponDestination(game, 3);
                    System.out.println(Utils.getStrings("cli", "actions", "grab_action", "grab_weapon").get("select_weapon").getAsString());
                    selectSPWeapon(game);
                    if (game.getActualPlayer().getWeapons().size() > 2) {
                        System.out.println(Utils.getStrings("cli", "actions", "grab_action", "grab_weapon").get("discard_weapon").getAsString());
                        selectMyDiscardWeapon(game);
                    }
                    System.out.println(Utils.getStrings("cli", "actions").get("alternative_payment").getAsString());
                    selectAlternativePayment(game);
                    Client.API.doAction(Preferences.getToken(), Action.Builder.create(game.getUuid())
                            .buildWeaponGrabAction(destination, weapon, discardedWeapon, powerUpPayment));
                    break;
                case 3:
                    System.out.println(Utils.getStrings("cli", "actions", "fire_action").get("select_weapon").getAsString());
                    selectMyFireWeapon(game);
                    System.out.println(Utils.getStrings("cli", "actions", "fire_action").get("select_move_square").getAsString());
                    selectMyDestination(game, 2);
                    buildFireAction(game);
                    Client.API.doAction(Preferences.getToken(), Action.Builder.create(game.getUuid())
                            .buildFireAction(weapon, destination, powerUpPayment, alternativeFire, options,
                                    basicTarget, basicTargetPoint, firstAdditionalTarget, firstAdditionalTargetPoint,
                                    secondAdditionalTarget, secondAdditionalTargetPoint));
                    break;
                case 4:
                    buildUsePowerUp(game);
                    break;
                case 5:
                    Client.API.doAction(Preferences.getToken(), Action.Builder.create(game.getUuid()).buildNextTurn());
                    break;
            }
        } else {
            System.out.println(invalidChoice);
            selectStandardAction(game);
        }
    }

    private void selectMyDestination(@NotNull Game game, int step) throws InterruptedException {
        List<Point> possibleDestination = new ArrayList<>();
        addMovementPoint(game, possibleDestination, step);
        printDestinations(game, possibleDestination);
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= possibleDestination.size())
            destination = possibleDestination.get(Integer.parseInt(choice) - 1);
        else if (choice.charAt(0) != '*') {
            System.out.println(invalidChoice);
            selectMyDestination(game, step);
        }
    }

    private void selectGrabAmmoDestination(@NotNull Game game, int step) throws RemoteException, InterruptedException {
        List<Point> possibleDestination = new ArrayList<>();
        for (int i = 0; i < Game.MAX_Y; i++)
            for (int j = 0; j < Game.MAX_X; j++)
                if (game.getCell(new Point(i, j)) != null && game.getCell(new Point(i, j)).getAmmoCard() != null &&
                        game.canMove(game.getActualPlayer().getPosition(), new Point(i, j), step))
                    possibleDestination.add(new Point(i, j));
        printDestinations(game, possibleDestination);
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= possibleDestination.size())
            Client.API.doAction(Preferences.getToken(), Action.Builder.create(game.getUuid())
                    .buildAmmoCardGrabAction(possibleDestination.get(Integer.parseInt(choice) - 1)));
        else if (choice.charAt(0) != '*') {
            System.out.println(invalidChoice);
            selectGrabAmmoDestination(game, step);
        }
    }

    private void selectGrabWeaponDestination(@NotNull Game game, int step) throws InterruptedException {
        List<Point> possibleDestination = new ArrayList<>();
        for (int i = 0; i < Game.MAX_Y; i++)
            for (int j = 0; j < Game.MAX_X; j++)
                if (game.getCell(new Point(i, j)) != null && game.getCell(new Point(i, j)).isSpawnPoint() &&
                        game.canMove(game.getActualPlayer().getPosition(), new Point(i, j), step))
                    possibleDestination.add(new Point(i, j));
        printDestinations(game, possibleDestination);
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= possibleDestination.size())
            destination = possibleDestination.get(Integer.parseInt(choice) - 1);
        else {
            System.out.println(invalidChoice);
            selectGrabWeaponDestination(game, step);
        }
    }

    private void selectSPWeapon(@NotNull Game game) throws InterruptedException {
        for (int i = 0; i < game.getWeapons(game.getCell(destination).getColor()).size(); i++)
            System.out.println((i + 1) + ". " + game.getWeapons(game.getCell(destination).getColor()).get(i).getColor().escape() +
                    game.getWeapons(game.getCell(destination).getColor()).get(i).name() + "\u001b[0m" + " " +
                    Utils.getStrings("cli", "weapons_details", game.getWeapons(game.getCell(destination).getColor()).get(i).name().toLowerCase()).get("grab_cost").getAsString());
        System.out.println(Utils.getStrings("cli").get("back_to_menu").getAsString());
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= game.getWeapons(game.getCell(destination).getColor()).size()) {
            weapon = game.getWeapons(game.getCell(destination).getColor()).get(Integer.parseInt(choice) - 1);
        } else {
            System.out.println(invalidChoice);
            selectSPWeapon(game);
        }
    }

    private void selectMyDiscardWeapon(@NotNull Game game) throws InterruptedException {
        List<Weapon> selectableWeapon = new ArrayList<>(game.getActualPlayer().getWeapons());
        for (int i = 0; i < selectableWeapon.size(); i++)
            System.out.println((i + 1) + ". " + selectableWeapon.get(i).getColor().escape() +
                    selectableWeapon.get(i).name() + "\u001b[0m");
        System.out.println(Utils.getStrings("cli").get("back_to_menu").getAsString());
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= game.getWeapons(game.getCell(destination).getColor()).size()) {
            discardedWeapon = selectableWeapon.get(Integer.parseInt(choice) - 1);
        } else {
            System.out.println(invalidChoice);
            selectMyDiscardWeapon(game);
        }
    }

    private void selectMyFireWeapon(@NotNull Game game) throws InterruptedException {
        List<Weapon> selectableWeapon = new ArrayList<>();
        if (game.getSkulls() != 0)
            game.getActualPlayer().getWeapons().parallelStream()
                    .filter(e -> game.getActualPlayer().isALoadedGun(e)).forEach(selectableWeapon::add);
        else selectableWeapon.addAll(game.getActualPlayer().getWeapons());
        if (selectableWeapon.isEmpty())
            System.out.println("Non hai armi con cui fare fuoco");
        for (int i = 0; i < selectableWeapon.size(); i++)
            System.out.println((i + 1) + ". " + selectableWeapon.get(i).getColor().escape() + selectableWeapon.get(i).name() +
                    "\u001b[0m" + (game.getActualPlayer().isALoadedGun(selectableWeapon.get(i)) ? "" : " (scarica) " +
                    Utils.getStrings("cli", "weapons_details", selectableWeapon.get(i).name().toLowerCase()).get("reload_cost").getAsString()));
        System.out.println(Utils.getStrings("cli").get("back_to_menu").getAsString());
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= selectableWeapon.size())
            weapon = selectableWeapon.get(Integer.parseInt(choice) - 1);
        else {
            System.out.println(invalidChoice);
            selectMyFireWeapon(game);
        }
    }

    private void buildUsePowerUp(@NotNull Game game) throws InterruptedException, RemoteException {
        System.out.println(Utils.getStrings("cli", "actions", "use_power_up_action").get("select_power_up").getAsString());
        selectPowerUpToUse(game);
        switch (powerUpType) {
            case TARGETING_SCOPE:
                System.out.println(Utils.getStrings("cli", "actions", "use_power_up_action", "targeting_scope").get("select_target").getAsString());
                selectTargetingScopeTarget(game);
                Client.API.doAction(Preferences.getToken(), Action.Builder.create(game.getUuid()).buildUsePowerUp(powerUpType, color, destination, target));
                break;
            case NEWTON:
                System.out.println(Utils.getStrings("cli", "actions", "use_power_up_action", "newton").get("select_target").getAsString());
                selectNewtonTarget(game);
                System.out.println(Utils.getStrings("cli", "actions", "use_power_up_action", "newton").get("select_square").getAsString());
                selectNewtonDestination(game);
                Client.API.doAction(Preferences.getToken(), Action.Builder.create(game.getUuid()).buildUsePowerUp(powerUpType, color, destination, target));
                break;
            case TELEPORTER:
                System.out.println(Utils.getStrings("cli", "actions", "use_power_up_action", "teleporter").get("select_square").getAsString());
                selectTeleporterDestination(game);
                Client.API.doAction(Preferences.getToken(), Action.Builder.create(game.getUuid()).buildUsePowerUp(powerUpType, color, destination, target));
                break;
            default:
                break;
        }
    }

    private void selectPowerUpToUse(@NotNull Game game) throws InterruptedException {
        List<PowerUp> usablePowerUps = new ArrayList<>();
        game.getActualPlayer().getPowerUps().stream().filter(e -> !e.getType().equals(PowerUp.Type.TAGBACK_GRENADE))
                .forEach(usablePowerUps::add);
        if (usablePowerUps.isEmpty()) System.out.println("Non hai powerups da utilizzare");
        for (int i = 0; i < usablePowerUps.size(); i++)
            System.out.println((i + 1) + ". " + usablePowerUps.get(i).getAmmoColor().escape() + usablePowerUps.get(i).getType().name() +
                    "\u001b[0m");
        System.out.println(Utils.getStrings("cli").get("back_to_menu").getAsString());
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= usablePowerUps.size()) {
            powerUpType = usablePowerUps.get(Integer.parseInt(choice) - 1).getType();
            color = usablePowerUps.get(Integer.parseInt(choice) - 1).getAmmoColor();
        } else {
            System.out.println(invalidChoice);
            selectPowerUpToUse(game);
        }
    }

    private void selectTargetingScopeTarget(@NotNull Game game) throws InterruptedException {
        List<UUID> possibleTargets = new ArrayList<>(game.getLastsDamaged());
        if (possibleTargets.isEmpty()) System.out.println("Non hai bersagli validi");
        printTargets(game, possibleTargets);
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= possibleTargets.size())
            target = possibleTargets.get(Integer.parseInt(choice) - 1);
        else {
            System.out.println(invalidChoice);
            selectTargetingScopeTarget(game);
        }
    }

    private void selectNewtonTarget(@NotNull Game game) throws InterruptedException {
        List<UUID> possibleTargets = new ArrayList<>();
        game.getPlayers().stream().filter(e -> !e.equals(game.getActualPlayer())).map(Player::getUuid)
                .forEach(possibleTargets::add);
        printTargets(game, possibleTargets);
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= possibleTargets.size())
            target = possibleTargets.get(Integer.parseInt(choice) - 1);
        else {
            System.out.println(invalidChoice);
            selectNewtonTarget(game);
        }
    }

    private void selectNewtonDestination(@NotNull Game game) throws InterruptedException {
        List<Point> possibleDestinations = new ArrayList<>();
        for (int i = 0; i < Game.MAX_Y; i++)
            for (int j = 0; j < Game.MAX_X; j++) {
                var point = new Point(i, j);
                for (Bounds.Direction dir : Bounds.Direction.values())
                    if (game.getPlayers().stream().filter(e -> e.getUuid().equals(target))
                            .allMatch(e -> e.isPointAtMaxDistanceInDirection(point, game.getCells(), 2, dir)))
                        possibleDestinations.add(point);
            }
        for (int i = 0; i < possibleDestinations.size(); i++)
            System.out.println((i + 1) + ". " + possibleDestinations.get(i));
        System.out.println(Utils.getStrings("cli").get("back_to_menu").getAsString());
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= possibleDestinations.size())
            destination = possibleDestinations.get(Integer.parseInt(choice) - 1);
        else {
            System.out.println(invalidChoice);
            selectNewtonDestination(game);
        }
    }

    private void selectTeleporterDestination(@NotNull Game game) throws InterruptedException {
        List<Point> possibleDestinations = new ArrayList<>();
        for (int i = 0; i < Game.MAX_Y; i++)
            for (int j = 0; j < Game.MAX_X; j++)
                if (game.getCell(new Point(i, j)) != null)
                    possibleDestinations.add(new Point(i, j));
        printDestinations(game, possibleDestinations);
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= possibleDestinations.size())
            destination = possibleDestinations.get(Integer.parseInt(choice) - 1);
        else {
            System.out.println(invalidChoice);
            selectTeleporterDestination(game);
        }
    }

    private void selectMyReloadWeapon(@NotNull Game game) throws InterruptedException {
        List<Weapon> selectableWeapons = new ArrayList<>();
        game.getActualPlayer().getWeapons().parallelStream()
                .filter(e -> !game.getActualPlayer().isALoadedGun(e)).forEach(selectableWeapons::add);
        if (selectableWeapons.isEmpty())
            System.out.println("Non hai armi da ricaricare");
        for (int i = 0; i < selectableWeapons.size(); i++)
            System.out.println((i + 1) + ". " + selectableWeapons.get(i).getColor().escape() +
                    selectableWeapons.get(i).name() + "\u001b[0m" +
                    Utils.getStrings("cli", "weapons_details", selectableWeapons.get(i).name().toLowerCase()).get("reload_cost").getAsString());
        System.out.println(Utils.getStrings("cli").get("back_to_menu").getAsString());
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= selectableWeapons.size())
            weapon = selectableWeapons.get(Integer.parseInt(choice) - 1);
        else {
            System.out.println(invalidChoice);
            selectMyReloadWeapon(game);
        }
    }

    private @NotNull String getLine() throws InterruptedException {
        var line = new Scanner(System.in).nextLine();
        if (line.equals("*")) throw new InterruptedException();
        return line;
    }

    private void printDestinations(@NotNull Game game, @NotNull List<Point> possibleDestination) {
        for (int i = 0; i < possibleDestination.size(); i++)
            System.out.println((i + 1) + ". " + (possibleDestination.get(i).equals(game.getActualPlayer().getPosition()) ?
                    "Rimani fermo" : possibleDestination.get(i)));
        System.out.println(Utils.getStrings("cli").get("back_to_menu").getAsString());
    }

    private void printTargets(@NotNull Game game, @NotNull List<UUID> possibleTargets) {
        for (int i = 0; i < possibleTargets.size(); i++) {
            var uuid = possibleTargets.get(i);
            game.getPlayers().stream().filter(e -> e.getUuid().equals(uuid))
                    .forEach(e -> System.out.println((possibleTargets.indexOf(uuid) + 1) + ". " +
                            e.getBoardType().escape() + e.getNickname() + stdColor));
        }
        System.out.println(Utils.getStrings("cli").get("back_to_menu").getAsString());
    }

    private void buildFireAction(@NotNull Game game) throws FileNotFoundException, InterruptedException {
        switch (weapon) {
            case LOCK_RIFLE:
                System.out.println(Utils.getStrings("cli", "weapons_details", "lock_rifle").get("fire_description").getAsString());
                alternativeFire = false;
                doubleChoice(game);
                selectBasicVisibleTarget(game, new ArrayList<>());
                if (options == 1) {
                    selectFirstVisibleTarget(game, basicTarget);
                    selectAlternativePayment(game);
                }
                break;
            case MACHINE_GUN:
                System.out.println(Utils.getStrings("cli", "weapons_details", "machine_gun").get("fire_description").getAsString());
                alternativeFire = false;
                quadrupleChoice(game);
                done = false;
                do {
                    selectBasicVisibleTarget(game, basicTarget);
                    if (basicTarget.size() < 2)
                        doneQuestion();
                } while (basicTarget.size() < 2 && !done);
                if (options == 1 || options == 3)
                    selectFirstVisibleTarget(game, game.getPlayers().stream().map(Player::getUuid)
                            .filter(e -> !basicTarget.contains(e)).collect(Collectors.toList()));
                if (options == 2 || options == 3) {
                    done = false;
                    do {
                        List<UUID> unselectable = new ArrayList<>(secondAdditionalTarget);
                        unselectable.addAll(firstAdditionalTarget);
                        if (basicTarget.stream().anyMatch(secondAdditionalTarget::contains))
                            unselectable.addAll(basicTarget);
                        else if (!unselectable.isEmpty())
                            game.getPlayers().stream().map(Player::getUuid).filter(e -> !basicTarget.contains(e)).forEach(unselectable::add);
                        selectSecondVisibleTarget(game, unselectable);
                        if (secondAdditionalTarget.size() < 2)
                            doneQuestion();
                    } while (secondAdditionalTarget.size() < 2 && !done);
                }
                if (options > 0) selectAlternativePayment(game);
                break;
            case THOR:
                System.out.println(Utils.getStrings("cli", "weapons_details", "thor").get("fire_description").getAsString());
                alternativeFire = false;
                tripleChoice(game);
                if (options == 2) options = 3;
                selectBasicVisibleTarget(game, new ArrayList<>());
                if (options == 1 || options == 3) selectFirstThorTarget(game);
                if (options == 3) selectSecondThorTarget(game);
                if (options > 0) selectAlternativePayment(game);
                break;
            case PLASMA_GUN:
                System.out.println(Utils.getStrings("cli", "weapons_details", "plasma_gun").get("fire_description").getAsString());
                alternativeFire = false;
                doubleChoice(game);
                selectBasicMovementPoint(game, 2);
                //todo aggiusta per vedere anche quelli dopo il movimento
                selectBasicVisibleTarget(game, new ArrayList<>());
                if (options == 1) {
                    options = 2;
                    selectAlternativePayment(game);
                }
                break;
            case WHISPER:
                System.out.println(Utils.getStrings("cli", "weapons_details", "whisper").get("fire_description").getAsString());
                alternativeFire = false;
                options = 0;
                selectBasicVisibleTarget(game, new ArrayList<>(game.getPlayers().parallelStream()
                        .filter(e -> game.canMove(game.getActualPlayer().getPosition(), e.getPosition(), 1))
                        .map(Player::getUuid).collect(Collectors.toList())));
                break;
            case ELECTROSCYTHE:
                System.out.println(Utils.getStrings("cli", "weapons_details", "electroscythe").get("fire_description").getAsString());
                doubleChoice(game);
                alternativeFire = options != 0;
                options = 0;
                if (alternativeFire) selectAlternativePayment(game);
                break;
            case TRACTOR_BEAM:
                System.out.println(Utils.getStrings("cli", "weapons_details", "tractor_beam").get("fire_description").getAsString());
                doubleChoice(game);
                alternativeFire = options != 0;
                options = 0;
                if (alternativeFire) {
                    System.out.println(Utils.getStrings("cli", "weapons_details", "tractor_beam", "fire_details").get("select_target_basic").getAsString());
                    List<UUID> selectableTargets = game.getPlayers().parallelStream()
                            .filter(e -> game.canMove(e.getPosition(), game.getActualPlayer().getPosition(), 3))
                            .map(Player::getUuid).collect(Collectors.toList());
                    printSelectableTargets(game, selectableTargets);
                    simpleBasicTargetSelection(selectableTargets);
                    selectAlternativePayment(game);
                } else {
                    selectVisibleBasicPoint(game);
                    System.out.println(Utils.getStrings("cli", "weapons_details", "tractor_beam", "fire_details").get("select_target_basic").getAsString());
                    List<UUID> selectableTargets = game.getPlayers().parallelStream()
                            .filter(e -> game.canMove(e.getPosition(), basicTargetPoint, 3))
                            .map(Player::getUuid).collect(Collectors.toList());
                    printSelectableTargets(game, selectableTargets);
                    simpleBasicTargetSelection(selectableTargets);
                }
                break;
            case VORTEX_CANNON:
                System.out.println(Utils.getStrings("cli", "weapons_details", "vortex_cannon").get("fire_description").getAsString());
                doubleChoice(game);
                selectVisibleBasicPoint(game);
                List<UUID> selectableTargets = game.getPlayers().parallelStream()
                        .filter(e -> game.canMove(e.getPosition(), basicTargetPoint, 1))
                        .map(Player::getUuid).collect(Collectors.toList());
                System.out.println(Utils.getStrings("cli", "weapons_details", "vortex_cannon", "fire_details").get("select_target_basic").getAsString());
                printSelectableTargets(game, selectableTargets);
                simpleBasicTargetSelection(selectableTargets);
                if (options == 1) {
                    done = false;
                    do {
                        selectableTargets.clear();
                        System.out.println(Utils.getStrings("cli", "weapons_details", "vortex_cannon", "fire_details").get("select_target_first").getAsString());
                        selectableTargets = game.getPlayers().parallelStream()
                                .filter(e -> !basicTarget.contains(e.getUuid()) && !firstAdditionalTarget.contains(e.getUuid()) &&
                                        game.canMove(e.getPosition(), basicTargetPoint, 1))
                                .map(Player::getUuid).collect(Collectors.toList());
                        System.out.println(Utils.getStrings("cli", "weapons_details", "vortex_cannon", "fire_details").get("select_target_first").getAsString());
                        printSelectableTargets(game, selectableTargets);
                        simpleFirstTargetSelection(selectableTargets);
                        if (firstAdditionalTarget.size() < 2) doneQuestion();
                    } while (firstAdditionalTarget.size() < 2 && !done);
                    selectAlternativePayment(game);
                }
                break;
            case FURNACE:
                System.out.println(Utils.getStrings("cli", "weapons_details", "furnace").get("fire_description").getAsString());
                doubleChoice(game);
                alternativeFire = options != 0;
                options = 0;
                if (alternativeFire) {
                    List<Point> selectablePoints = new ArrayList<>();
                    for (int i = 0; i < Game.MAX_Y; i++)
                        for (int j = 0; j < Game.MAX_X; j++)
                            if (game.getCell(new Point(i, j)) != null && !new Point(i, j).equals(game.getActualPlayer().getPosition()) &&
                                    game.canMove(game.getActualPlayer().getPosition(), new Point(i, j), 1))
                                selectablePoints.add(new Point(i, j));
                    System.out.println(Utils.getStrings("cli", "weapons_details", "furnace", "fire_details").get("select_square").getAsString());
                    printSelectablePoints(selectablePoints);
                    simpleBasicPointSelection(selectablePoints);
                } else {
                    List<Cell.Color> selectableColor = new ArrayList<>();
                    for (int i = 0; i < Game.MAX_Y; i++)
                        for (int j = 0; j < Game.MAX_X; j++)
                            if (game.getCell(new Point(i, j)) != null && !game.getCell(new Point(i, j)).getColor().equals(game.getCell(game.getActualPlayer().getPosition()).getColor()) &&
                                    game.canMove(game.getActualPlayer().getPosition(), new Point(i, j), 1) && !selectableColor.contains(game.getCell(new Point(i, j)).getColor()))
                                selectableColor.add(game.getCell(new Point(i, j)).getColor());
                    System.out.println(Utils.getStrings("cli", "weapons_details", "furnace", "fire_details").get("select_chamber").getAsString());
                    for (int i = 0; i < selectableColor.size(); i++)
                        System.out.println((i + 1) + ". " + selectableColor.get(i).escape() + "Stanza " +
                                selectableColor.get(i).name() + stdColor);
                    System.out.println(Utils.getStrings("cli").get("back_to_menu").getAsString());
                    selectColor(game, selectableColor);
                }
                break;
            case HEATSEEKER:
                System.out.println(Utils.getStrings("cli", "weapons_details", "heatseeker").get("fire_description").getAsString());
                alternativeFire = false;
                options = 0;
                System.out.println(Utils.getStrings("cli", "weapons_details", "heatseeker", "fire_details").get("select_target_basic").getAsString());
                List<UUID> selectableHeatTargets = game.getPlayers().parallelStream().filter(e -> !e.equals(game.getActualPlayer()) &&
                        !game.getActualPlayer().canSeeNotSame(e, game.getCells())).map(Player::getUuid).collect(Collectors.toList());
                printSelectableTargets(game, selectableHeatTargets);
                simpleBasicTargetSelection(selectableHeatTargets);
                break;
            case HELLION:
                System.out.println(Utils.getStrings("cli", "weapons_details", "hellion").get("fire_description").getAsString());
                doubleChoice(game);
                alternativeFire = options != 0;
                options = 0;
                selectBasicVisibleTarget(game, game.getPlayers().stream().filter(e -> e.getPosition() == null ||
                        e.getPosition().equals(game.getActualPlayer().getPosition())).map(Player::getUuid).collect(Collectors.toList()));
                if (alternativeFire) selectAlternativePayment(game);
                break;
            case FLAMETHROWER:
                System.out.println(Utils.getStrings("cli", "weapons_details", "flamethrower").get("fire_description").getAsString());
                doubleChoice(game);
                alternativeFire = options != 0;
                options = 0;
                if (alternativeFire) {
                    List<Point> selectablePoints = new ArrayList<>();
                    for (int i = 0; i < Game.MAX_Y; i++)
                        for (int j = 0; j < Game.MAX_X; j++)
                            for (Bounds.Direction dir : Bounds.Direction.values()) {
                                if (game.getActualPlayer().isPointAtMaxDistanceInDirection(new Point(i, j), game.getCells(), 2, dir))
                                    selectablePoints.add(new Point(i, j));
                            }
                    System.out.println(Utils.getStrings("cli", "weapons_details", "flamethrower", "fire_details").get("select_point"));
                    printSelectablePoints(selectablePoints);
                    simpleBasicPointSelection(selectablePoints);
                    selectAlternativePayment(game);
                } else {
                    do {
                        List<UUID> selectableFlameTargets = new ArrayList<>();
                        if (basicTarget.size() == 1)
                            game.getPlayers().stream().filter(e -> e.getPosition() != null &&
                                    !e.getPosition().equals(game.getActualPlayer().getPosition()) &&
                                    game.getPlayers().stream().noneMatch(f -> e.getPosition().equals(f.getPosition()) && f.getUuid().equals(basicTarget.get(0))) &&
                                    Stream.of(Bounds.Direction.values()).anyMatch(f -> game.getActualPlayer().isPointAtMaxDistanceInDirection(e.getPosition(), game.getCells(), 2, f) &&
                                            game.getPlayers().stream().anyMatch(g -> g.getUuid().equals(basicTarget.get(0)) && g.getPosition() != null &&
                                                    game.getActualPlayer().isPointAtMaxDistanceInDirection(g.getPosition(), game.getCells(), 2, f))))
                                    .map(Player::getUuid).forEach(selectableFlameTargets::add);
                        else game.getPlayers().stream().filter(e -> e.getPosition() != null &&
                                !e.getPosition().equals(game.getActualPlayer().getPosition()) &&
                                Stream.of(Bounds.Direction.values()).anyMatch(f -> game.getActualPlayer().isPointAtMaxDistanceInDirection(e.getPosition(), game.getCells(), 2, f)))
                                .map(Player::getUuid).forEach(selectableFlameTargets::add);
                        System.out.println(Utils.getStrings("cli", "weapons_details", "flamethrower", "fire_details").get("select_target"));
                        printSelectableTargets(game, selectableFlameTargets);
                        simpleBasicTargetSelection(selectableFlameTargets);
                        if (basicTarget.size() < 2) doneQuestion();
                    } while (basicTarget.size() < 2 && !done);
                }
                break;
            case GRENADE_LAUNCHER:
                System.out.println(Utils.getStrings("cli", "weapons_details", "grenade_launcher").get("fire_description").getAsString());
                doubleChoice(game);
                selectBasicVisibleTarget(game, new ArrayList<>());
                selectBasicTargetMovementInDirection(game, 1);
                if (options == 1) {
                    selectVisibleFirstPoint(game);
                    selectAlternativePayment(game);
                }
                break;
            case ROCKET_LAUNCHER:
                System.out.println(Utils.getStrings("cli", "weapons_details", "rocket_launcher").get("fire_description").getAsString());
                quadrupleChoice(game);
                if (options == 1 || options == 3) {
                    List<Point> selectablePoints = new ArrayList<>();
                    addMovementPoint(game, selectablePoints, 2);
                    printDestinations(game, selectablePoints);
                }
                //todo: aggiusta per fargli vedere anche quelli dopo il movimento se c'è
                selectBasicVisibleTarget(game, game.getPlayers().stream().filter(e -> e.getPosition() != null &&
                        !e.getPosition().equals(game.getActualPlayer().getPosition())).map(Player::getUuid).collect(Collectors.toList()));
                selectBasicTargetMovementInDirection(game, 1);
                if (options > 0) selectAlternativePayment(game);
                break;
            case RAILGUN:
                System.out.println(Utils.getStrings("cli", "weapons_details", "railgun").get("fire_description").getAsString());
                doubleChoice(game);
                alternativeFire = options != 0;
                options = 0;
                List<UUID> selectableRailTargets = game.getPlayers().parallelStream().filter(e -> e.getPosition() != null &&
                        !e.equals(game.getActualPlayer()) && (e.getPosition().x == game.getActualPlayer().getPosition().x ||
                        e.getPosition().y == game.getActualPlayer().getPosition().y)).map(Player::getUuid).collect(Collectors.toList());
                if (alternativeFire) {
                    done = false;
                    do {
                        System.out.println(Utils.getStrings("cli", "weapons_details", "railgun", "fire_details").get("select_target_basic_alt").getAsString());
                        if (!basicTarget.isEmpty()) {
                            selectableRailTargets.clear();
                            game.getPlayers().stream().filter(e -> e.getPosition() != null && !e.getUuid().equals(basicTarget.get(0)) &&
                                    (e.getPosition().equals(game.getActualPlayer().getPosition()) ||
                                            game.getPlayers().parallelStream().anyMatch(f -> f.getUuid().equals(basicTarget.get(0)) &&
                                                    (e.getPosition().x == f.getPosition().x && (game.getActualPlayer().getPosition().y - e.getPosition().y) * (game.getActualPlayer().getPosition().y - f.getPosition().y) >= 0 ||
                                                            e.getPosition().y == f.getPosition().y && (game.getActualPlayer().getPosition().x - e.getPosition().x) * (game.getActualPlayer().getPosition().x - f.getPosition().x) >= 0))))
                                    .map(Player::getUuid).forEach(selectableRailTargets::add);
                        }
                        printSelectableTargets(game, selectableRailTargets);
                        simpleBasicTargetSelection(selectableRailTargets);
                        if (basicTarget.size() < 2) doneQuestion();
                    } while (basicTarget.size() < 2 && !done);
                } else {
                    System.out.println(Utils.getStrings("cli", "weapons_details", "railgun", "fire_details").get("select_target_basic").getAsString());
                    printSelectableTargets(game, selectableRailTargets);
                    simpleBasicTargetSelection(selectableRailTargets);
                }
                break;
            case CYBERBLADE:
                System.out.println(Utils.getStrings("cli", "weapons_details", "cyberblade").get("fire_description").getAsString());
                doubleChoice(game);
                selectBasicMovementPoint(game, 1);
                List<UUID> selectableBladeTargets = game.getPlayers().stream().filter(e -> e.getPosition() != null &&
                        (e.getPosition().equals(game.getActualPlayer().getPosition()) || e.getPosition().equals(basicTargetPoint)))
                        .map(Player::getUuid).collect(Collectors.toList());
                System.out.println(Utils.getStrings("cli", "weapons_details", "cyberblade", "fire_details").get("select_target_basic").getAsString());
                printSelectableTargets(game, selectableBladeTargets);
                simpleBasicTargetSelection(selectableBladeTargets);
                if (options == 1) {
                    options = 2;
                    selectableBladeTargets.clear();
                    if (game.getPlayers().parallelStream().anyMatch(e -> e.getUuid().equals(basicTarget.get(0)) &&
                            e.getPosition() != null && e.getPosition().equals(basicTargetPoint)))
                        selectableBladeTargets = game.getPlayers().stream().filter(e -> !e.getUuid().equals(basicTarget.get(0)) &&
                                e.getPosition() != null && e.getPosition().equals(basicTargetPoint)).map(Player::getUuid).collect(Collectors.toList());
                    else selectableBladeTargets.remove(basicTarget.get(0));
                    printSelectableTargets(game, selectableBladeTargets);
                    simpleSecondTargetSelection(selectableBladeTargets);
                    selectAlternativePayment(game);
                }
                break;
            case ZX2:
                System.out.println(Utils.getStrings("cli", "weapons_details", "zx2").get("fire_description").getAsString());
                doubleChoice(game);
                alternativeFire = options != 0;
                options = 0;
                if (alternativeFire) {
                    do {
                        System.out.println(Utils.getStrings("cli", "weapons_details", "zx2", "fire_details").get("select_target_basic").getAsString());
                        selectBasicVisibleTarget(game, basicTarget);
                        if (basicTarget.size() < 3) doneQuestion();
                    } while (basicTarget.size() < 3 && !done);
                } else {
                    System.out.println(Utils.getStrings("cli", "weapons_details", "zx2", "fire_details").get("select_target_basic").getAsString());
                    selectBasicVisibleTarget(game, new ArrayList<>());
                }
                break;
            case SHOTGUN:
                System.out.println(Utils.getStrings("cli", "weapons_details", "shotgun").get("fire_description").getAsString());
                doubleChoice(game);
                alternativeFire = options != 0;
                options = 0;
                List<UUID> selectableShotgunTargets = new ArrayList<>();
                if (alternativeFire) {
                    game.getPlayers().stream().filter(e -> e.getPosition() != null && !e.getPosition().equals(game.getActualPlayer().getPosition()) &&
                            Stream.of(Bounds.Direction.values()).anyMatch(f -> game.getActualPlayer().isPointAtMaxDistanceInDirection(e.getPosition(), game.getCells(), 1, f)))
                            .map(Player::getUuid).forEach(selectableShotgunTargets::add);
                    System.out.println(Utils.getStrings("cli", "weapons_details", "shotgun", "fire_details").get("select_target_basic").getAsString());
                    printSelectableTargets(game, selectableShotgunTargets);
                    simpleBasicTargetSelection(selectableShotgunTargets);
                } else {
                    game.getPlayersAtPosition(game.getActualPlayer().getPosition()).stream().map(Player::getUuid).forEach(selectableShotgunTargets::add);
                    System.out.println(Utils.getStrings("cli", "weapons_details", "shotgun", "fire_details").get("select_target_basic").getAsString());
                    printSelectableTargets(game, selectableShotgunTargets);
                    simpleBasicTargetSelection(selectableShotgunTargets);
                    selectBasicTargetMovementInDirection(game, 1);
                }
                break;
            case POWER_GLOVE:
                System.out.println(Utils.getStrings("cli", "weapons_details", "power_glove").get("fire_description").getAsString());
                doubleChoice(game);
                alternativeFire = options != 0;
                options = 0;
                List<UUID> selectableGloveTargets = game.getPlayers().stream().filter(e -> e.getPosition() != null &&
                        !e.getPosition().equals(game.getActualPlayer().getPosition()) &&
                        Stream.of(Bounds.Direction.values()).anyMatch(d -> game.getActualPlayer().isPointAtMaxDistanceInDirection(e.getPosition(), game.getCells(), 2, d)))
                        .map(Player::getUuid).collect(Collectors.toList());
                if (alternativeFire) {
                    do {
                        if (!basicTarget.isEmpty()) {
                            for (Player tar : game.getPlayers()) {
                                if (tar.getUuid().equals(basicTarget.get(0))) {
                                    selectableGloveTargets.remove(tar.getUuid());
                                    for (Player player : game.getPlayers()) {
                                        if (selectableGloveTargets.contains(player.getUuid()) && (player.getPosition().equals(tar.getPosition()) ||
                                                Stream.of(Bounds.Direction.values()).noneMatch(d -> game.getActualPlayer()
                                                        .isPointAtMaxDistanceInDirection(player.getPosition(), game.getCells(), 2, d) &&
                                                        game.getActualPlayer().isPointAtMaxDistanceInDirection(tar.getPosition(), game.getCells(), 2, d))))
                                            selectableGloveTargets.remove(player.getUuid());
                                    }
                                }
                            }
                        }
                        System.out.println(Utils.getStrings("cli", "weapons_details", "power_glove", "fire_details").get("select_targets_rocket_fist_mode").getAsString());
                        printSelectableTargets(game, selectableGloveTargets);
                        simpleBasicTargetSelection(selectableGloveTargets);
                        if (basicTarget.size() < 2) doneQuestion();
                    } while (basicTarget.size() < 2 && !done);
                    //todo: ho barato
                    for (UUID tar : basicTarget) {
                        for (Player player : game.getPlayers()) {
                            if (player.getUuid().equals(tar) && Stream.of(Bounds.Direction.values())
                                    .noneMatch(d -> game.getActualPlayer().isPointAtMaxDistanceInDirection(player.getPosition(), game.getCells(), 2, d)))
                                basicTargetPoint = player.getPosition();
                        }
                    }
                    if (basicTargetPoint == null)
                        for (Player player : game.getPlayers())
                            if (player.getUuid().equals(basicTarget.get(0)))
                                basicTargetPoint = player.getPosition();
                    selectAlternativePayment(game);
                } else {
                    game.getPlayers().stream().filter(e -> selectableGloveTargets.contains(e.getUuid()) &&
                            Stream.of(Bounds.Direction.values()).noneMatch(d -> game.getActualPlayer().isPointAtMaxDistanceInDirection(e.getPosition(), game.getCells(), 1, d)))
                            .map(Player::getUuid).forEach(selectableGloveTargets::remove);
                    System.out.println(Utils.getStrings("cli", "weapons_details", "power_glove", "fire_details").get("select_target_basic").getAsString());
                    printSelectableTargets(game, selectableGloveTargets);
                    simpleBasicTargetSelection(selectableGloveTargets);
                }
                break;
            case SHOCKWAVE:
                System.out.println(Utils.getStrings("cli", "weapons_details", "shockwave").get("fire_description").getAsString());
                doubleChoice(game);
                alternativeFire = options != 0;
                options = 0;
                if (alternativeFire) selectAlternativePayment(game);
                else {
                    List<UUID> selectableShockTargets = game.getPlayers().stream().filter(e -> e.getPosition() != null &&
                            !e.getPosition().equals(game.getActualPlayer().getPosition()) &&
                            Stream.of(Bounds.Direction.values()).anyMatch(d -> game.getActualPlayer().isPointAtMaxDistanceInDirection(e.getPosition(), game.getCells(), 1, d)))
                            .map(Player::getUuid).collect(Collectors.toList());
                    do {
                        if (!basicTarget.isEmpty())
                            for (Player tar : game.getPlayers()) {
                                if (tar.getUuid().equals(basicTarget.get(basicTarget.size() - 1))) {
                                    for (Player player : game.getPlayers()) {
                                        if (selectableShockTargets.contains(player.getUuid()) &&
                                                tar.getPosition().equals(player.getPosition()))
                                            selectableShockTargets.remove(player.getUuid());
                                    }
                                }
                            }
                        System.out.println(Utils.getStrings("cli", "weapons_details", "shockwave", "fire_details").get("select_target_basic").getAsString());
                        printSelectableTargets(game, selectableShockTargets);
                        simpleBasicTargetSelection(selectableShockTargets);
                        if (basicTarget.size() < 3) doneQuestion();
                    } while (basicTarget.size() < 3 && !done);
                }
                break;
            case SLEDGEHAMMER:
                System.out.println(Utils.getStrings("cli", "weapons_details", "sledgehammer").get("fire_description").getAsString());
                doubleChoice(game);
                alternativeFire = options != 0;
                options = 0;
                List<UUID> selectableSledgeTargets = game.getPlayersAtPosition(game.getActualPlayer().getPosition()).stream()
                        .filter(e -> !e.equals(game.getActualPlayer())).map(Player::getUuid).collect(Collectors.toList());
                System.out.println(Utils.getStrings("cli", "weapons_details", "sledgehammer", "fire_details").get("select_target_basic").getAsString());
                printSelectableTargets(game, selectableSledgeTargets);
                simpleBasicTargetSelection(selectableSledgeTargets);
                if (alternativeFire) {
                    System.out.println(Utils.getStrings("cli", "weapons_details", "sledgehammer", "fire_details").get("select_point_basic").getAsString());
                    selectBasicTargetMovementInDirection(game, 2);
                    selectAlternativePayment(game);
                }
                break;
        }
    }

    private void printChoiceMessage() {
        System.out.println(Utils.getStrings("cli", "actions", "fire_action", "select_fire_mode").get("standard_message").getAsString());
        System.out.println(Utils.getStrings("cli", "actions", "fire_action", "select_fire_mode").get(weapon.name().toLowerCase()).getAsString());
        System.out.println(Utils.getStrings("cli").get("back_to_menu").getAsString());
    }

    private void doubleChoice(@NotNull Game game) throws InterruptedException {
        printChoiceMessage();
        String choice = getLine();
        if (Integer.parseInt(choice) == 1 || Integer.parseInt(choice) == 2)
            options = Integer.parseInt(choice) - 1;
        else {
            System.out.println(invalidChoice);
            doubleChoice(game);
        }
    }

    private void tripleChoice(@NotNull Game game) throws InterruptedException {
        printChoiceMessage();
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) < 4)
            options = Integer.parseInt(choice) - 1;
        else {
            System.out.println(invalidChoice);
            tripleChoice(game);
        }
    }

    private void quadrupleChoice(@NotNull Game game) throws InterruptedException {
        printChoiceMessage();
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) < 5)
            options = Integer.parseInt(choice) - 1;
        else {
            System.out.println(invalidChoice);
            quadrupleChoice(game);
        }
    }

    private void selectBasicVisibleTarget(@NotNull Game game, @NotNull List<UUID> unselectable) throws InterruptedException {
        System.out.println(Utils.getStrings("cli", "weapons_details", weapon.name().toLowerCase(), "fire_details").get("select_target_basic"));
        List<UUID> selectableTargets = new ArrayList<>();
        selection(game, selectableTargets, unselectable);
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= selectableTargets.size())
            basicTarget.add(selectableTargets.get(Integer.parseInt(choice) - 1));
        else {
            System.out.println(invalidChoice);
            selectBasicVisibleTarget(game, unselectable);
        }
    }

    private void selectFirstVisibleTarget(@NotNull Game game, @NotNull List<UUID> unselectable) throws InterruptedException {
        System.out.println(Utils.getStrings("cli", "weapons_details", weapon.name().toLowerCase(), "fire_details").get("select_target_first"));
        List<UUID> selectableTargets = new ArrayList<>();
        selection(game, selectableTargets, unselectable);
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= selectableTargets.size())
            firstAdditionalTarget.add(selectableTargets.get(Integer.parseInt(choice) - 1));
        else {
            System.out.println(invalidChoice);
            selectFirstVisibleTarget(game, unselectable);
        }
    }

    private void selectFirstThorTarget(@NotNull Game game) throws InterruptedException {
        System.out.println(Utils.getStrings("cli", "weapons_details", weapon.name().toLowerCase(), "fire_details").get("select_target_first"));
        List<UUID> selectableTargets = new ArrayList<>();
        //todo: controllare
        Player bTarget = game.getPlayers().stream().filter(e -> e.getUuid().equals(basicTarget.get(0))).findFirst().get();
        game.getPlayers().stream().filter(e -> !e.equals(game.getActualPlayer()) && !e.getUuid().equals(basicTarget.get(0)) &&
                bTarget.canSeeNotSame(e, game.getCells())).map(Player::getUuid).forEach(selectableTargets::add);
        printSelectableTargets(game, selectableTargets);
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= selectableTargets.size())
            firstAdditionalTarget.add(selectableTargets.get(Integer.parseInt(choice) - 1));
        else {
            System.out.println(invalidChoice);
            selectFirstThorTarget(game);
        }
    }

    private void selectSecondVisibleTarget(@NotNull Game game, @NotNull List<UUID> unselectable) throws InterruptedException {
        System.out.println(Utils.getStrings("cli", "weapons_details", weapon.name().toLowerCase(), "fire_details").get("select_target_second"));
        List<UUID> selectableTargets = new ArrayList<>();
        selection(game, selectableTargets, unselectable);
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= selectableTargets.size())
            secondAdditionalTarget.add(selectableTargets.get(Integer.parseInt(choice) - 1));
        else {
            System.out.println(invalidChoice);
            selectSecondVisibleTarget(game, unselectable);
        }
    }

    private void selectSecondThorTarget(@NotNull Game game) throws InterruptedException {
        System.out.println(Utils.getStrings("cli", "weapons_details", weapon.name().toLowerCase(), "fire_details").get("select_target_second"));
        List<UUID> selectableTargets = new ArrayList<>();
        //todo: controllare
        Player fTarget = game.getPlayers().stream().filter(e -> e.getUuid().equals(firstAdditionalTarget.get(0))).findFirst().get();
        game.getPlayers().stream().filter(e -> !e.equals(game.getActualPlayer()) && !e.getUuid().equals(basicTarget.get(0)) && !e.equals(fTarget) &&
                fTarget.canSeeNotSame(e, game.getCells())).map(Player::getUuid).forEach(selectableTargets::add);
        printSelectableTargets(game, selectableTargets);
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= selectableTargets.size())
            secondAdditionalTarget.add(selectableTargets.get(Integer.parseInt(choice) - 1));
        else {
            System.out.println(invalidChoice);
            selectSecondThorTarget(game);
        }
    }

    private void selection(@NotNull Game game, @NotNull List<UUID> selectableTargets, @NotNull List<UUID> unselectable) {
        addVisibleTarget(game, selectableTargets);
        removeUnselectable(selectableTargets, unselectable);
        printSelectableTargets(game, selectableTargets);
    }

    private void printSelectableTargets(@NotNull Game game, @NotNull List<UUID> selectableTargets) {
        if (selectableTargets.isEmpty())
            System.out.println("Non ci sono giocatori selezionabili");
        for (int i = 0; i < selectableTargets.size(); i++) {
            for (Player player : game.getPlayers()) {
                if (player.getUuid().equals(selectableTargets.get(i)))
                    System.out.println((i + 1) + ". " + player.getBoardType().escape() + player.getNickname() + stdColor);
            }
        }
        System.out.println(Utils.getStrings("cli").get("back_to_menu").getAsString());
    }

    private void printSelectablePoints(@NotNull List<Point> selectablePoints) {
        for (int i = 0; i < selectablePoints.size(); i++)
            System.out.println((i + 1) + ". " + selectablePoints.get(i));
        System.out.println(Utils.getStrings("cli").get("back_to_menu").getAsString());
    }

    private void addVisibleTarget(@NotNull Game game, @NotNull List<UUID> selectableTargets) {
        game.getPlayers().stream().filter(e -> !e.equals(game.getActualPlayer()) && game.getActualPlayer().canSeeNotSame(e, game.getCells()))
                .map(Player::getUuid).forEach(selectableTargets::add);
    }

    private void addVisiblePoint(@NotNull Game game, @NotNull List<Point> selectablePoints) {
        for (int i = 0; i < Game.MAX_Y; i++)
            for (int j = 0; j < Game.MAX_X; j++)
                if (game.getActualPlayer().canSeeCell(new Point(i, j), game.getCells()))
                    selectablePoints.add(new Point(i, j));
    }

    private void removeUnselectable(@NotNull List<UUID> selectableTargets, @NotNull List<UUID> unselectableTargets) {
        unselectableTargets.stream().filter(selectableTargets::contains).forEach(selectableTargets::remove);
    }

    private void selectBasicMovementPoint(@NotNull Game game, int step) throws FileNotFoundException, InterruptedException {
        System.out.println(Utils.getStrings("cli", "weapons_details", weapon.name().toLowerCase(), "fire_details").get("select_point_basic"));
        List<Point> selectablePoints = new ArrayList<>();
        addMovementPoint(game, selectablePoints, step);
        printDestinations(game, selectablePoints);
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= selectablePoints.size())
            basicTargetPoint = selectablePoints.get(Integer.parseInt(choice) - 1);
        else {
            System.out.println(invalidChoice);
            selectBasicMovementPoint(game, step);
        }
    }

    private void selectBasicTargetMovementInDirection(@NotNull Game game, int step) throws InterruptedException {
        System.out.println(Utils.getStrings("cli", "weapons_details", weapon.name().toLowerCase(), "fire_details").get("select_point_basic"));
        List<Point> selectablePoints = new ArrayList<>();
        for (int i = 0; i < Game.MAX_Y; i++)
            for (int j = 0; j < Game.MAX_X; j++) {
                var point = new Point(i, j);
                if (game.getPlayers().stream().anyMatch(e -> e.getUuid().equals(basicTarget.get(0)) &&
                        Stream.of(Bounds.Direction.values()).anyMatch(d -> e.isPointAtMaxDistanceInDirection(point, game.getCells(), step, d))))
                    selectablePoints.add(point);
            }
        printSelectablePoints(selectablePoints);
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= selectablePoints.size())
            basicTargetPoint = selectablePoints.get(Integer.parseInt(choice) - 1);
        else {
            System.out.println(invalidChoice);
            selectBasicTargetMovementInDirection(game, step);
        }
    }

    private void selectVisibleBasicPoint(@NotNull Game game) throws InterruptedException {
        System.out.println(Utils.getStrings("cli", "weapons_details", weapon.name().toLowerCase(), "fire_details").get("select_point_basic"));
        List<Point> selectablePoints = new ArrayList<>();
        addVisiblePoint(game, selectablePoints);
        printDestinations(game, selectablePoints);
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= selectablePoints.size())
            basicTargetPoint = selectablePoints.get(Integer.parseInt(choice) - 1);
        else {
            System.out.println(invalidChoice);
            selectVisibleBasicPoint(game);
        }
    }

    private void selectVisibleFirstPoint(@NotNull Game game) throws InterruptedException {
        System.out.println(Utils.getStrings("cli", "weapons_details", weapon.name().toLowerCase(), "fire_details").get("select_point_first"));
        List<Point> selectablePoints = new ArrayList<>();
        addVisiblePoint(game, selectablePoints);
        printDestinations(game, selectablePoints);
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= selectablePoints.size())
            firstAdditionalTargetPoint = selectablePoints.get(Integer.parseInt(choice) - 1);
        else {
            System.out.println(invalidChoice);
            selectVisibleFirstPoint(game);
        }
    }

    private void doneQuestion() throws InterruptedException {
        System.out.println("Vuoi selezionare altri bersagli per questo effetto? [y/n]");
        System.out.println(Utils.getStrings("cli").get("back_to_menu").getAsString());
        String choice = getLine();
        if (choice.charAt(0) == 'y' || choice.charAt(0) == 'Y') done = false;
        else if (choice.charAt(0) == 'n' || choice.charAt(0) == 'N') done = true;
        else {
            System.out.println(invalidChoice);
            doneQuestion();
        }
    }

    private void addMovementPoint(@NotNull Game game, @NotNull List<Point> selectablePoints, int step) {
        for (int i = 0; i < Game.MAX_Y; i++)
            for (int j = 0; j < Game.MAX_X; j++)
                if (game.getCell(new Point(i, j)) != null && game.canMove(game.getActualPlayer().getPosition(), new Point(i, j), step))
                    selectablePoints.add(new Point(i, j));
    }

    private void simpleBasicTargetSelection(@NotNull List<UUID> selectableTargets) throws InterruptedException {
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= selectableTargets.size())
            basicTarget.add(selectableTargets.get(Integer.parseInt(choice) - 1));
        else {
            System.out.println(invalidChoice);
            simpleBasicTargetSelection(selectableTargets);
        }
    }

    private void simpleBasicPointSelection(@NotNull List<Point> selectablePoints) throws InterruptedException {
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= selectablePoints.size())
            basicTargetPoint = selectablePoints.get(Integer.parseInt(choice) - 1);
        else {
            System.out.println(invalidChoice);
            simpleBasicPointSelection(selectablePoints);
        }
    }

    private void simpleFirstTargetSelection(@NotNull List<UUID> selectableTargets) throws InterruptedException {
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= selectableTargets.size())
            firstAdditionalTarget.add(selectableTargets.get(Integer.parseInt(choice) - 1));
        else {
            System.out.println(invalidChoice);
            simpleFirstTargetSelection(selectableTargets);
        }
    }

    private void simpleSecondTargetSelection(@NotNull List<UUID> selectableTargets) throws InterruptedException {
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= selectableTargets.size())
            secondAdditionalTarget.add(selectableTargets.get(Integer.parseInt(choice) - 1));
        else {
            System.out.println(invalidChoice);
            simpleSecondTargetSelection(selectableTargets);
        }
    }

    private void selectColor(@NotNull Game game, @NotNull List<Cell.Color> selectableColor) throws InterruptedException {
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= selectableColor.size())
            for (int i = 0; i < Game.MAX_Y; i++)
                for (int j = 0; j < Game.MAX_X; j++)
                    if (game.getCell(new Point(i, j)) != null &&
                            game.getCell(new Point(i, j)).getColor().equals(selectableColor.get(Integer.parseInt(choice) - 1)))
                        basicTargetPoint = new Point(i, j);
                    else {
                        System.out.println(invalidChoice);
                        selectColor(game, selectableColor);
                    }
    }
}
