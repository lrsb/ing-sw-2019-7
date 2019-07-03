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

public class ActionManager {

    private final String stdColor = "\u001b[0m";
    private final String invalidChoice = "Scelta non valida";
    private @Nullable Weapon weapon;
    private @Nullable Weapon discardedWeapon;
    private @Nullable Point destination;
    private @Nullable PowerUp.Type powerUpType;
    private @Nullable AmmoCard.Color color;
    private @Nullable ArrayList<PowerUp> powerUpPayment;

    private boolean alternativeFire;
    private int options;
    private @Nullable ArrayList<UUID> basicTarget;
    private @Nullable Point basicTargetPoint;

    private @Nullable ArrayList<UUID> firstAdditionalTarget;
    private @Nullable Point firstAdditionalTargetPoint;

    private @Nullable ArrayList<UUID> secondAdditionalTarget;
    private @Nullable Point secondAdditionalTargetPoint;

    private @Nullable UUID target;

    private void clearAll() {
        weapon = null;
        discardedWeapon = null;
        destination = null;
        powerUpType = null;
        color = null;
        powerUpPayment = null;
        alternativeFire = false;
        options = 0;
        basicTarget = null;
        basicTargetPoint = null;
        firstAdditionalTarget = null;
        firstAdditionalTargetPoint = null;
        secondAdditionalTarget = null;
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
            System.out.println((1+i) + ". " + game.getActualPlayer().getPowerUps().get(i).getAmmoColor().escape() +
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

    private void selectAlternativePayment(@NotNull Game game) throws InterruptedException, FileNotFoundException {
        List<PowerUp> selectablePowerUps = new ArrayList<>(game.getActualPlayer().getPowerUps());
        if (powerUpPayment != null) powerUpPayment.forEach(selectablePowerUps::remove);
        if (selectablePowerUps.isEmpty()) return;
        for (int i = 0; i < selectablePowerUps.size(); i++)
            System.out.println((1+i) + ". " + selectablePowerUps.get(i).getAmmoColor().escape() +
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
            System.out.println((i+1) + ". " + selectableTagbackGrenade.get(i).getAmmoColor().escape() +
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
        if (game.getSkulls() > 0) System.out.println(Utils.getStrings("cli", "possible_actions").get(game.getActualPlayer().getDamagesTaken().size() < 3 ?
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
                    //todo: manda azione
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
                    //todo: manda azione
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

    private void selectMyDestination(@NotNull Game game, int step) throws RemoteException, InterruptedException, FileNotFoundException {
        List<Point> possibleDestination = new ArrayList<>();
        for (int i = 0; i < Game.MAX_Y; i++)
            for (int j = 0; j < Game.MAX_X; j++)
                if (game.getCell(new Point(i, j)) != null && game.canMove(game.getActualPlayer().getPosition(), new Point(i, j), step))
                    possibleDestination.add(new Point(i, j));
        printDestinations(game, possibleDestination);
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) <= possibleDestination.size())
            destination = possibleDestination.get(Integer.parseInt(choice) - 1);
        else if (choice.charAt(0) != '*') {
            System.out.println(invalidChoice);
            selectMyDestination(game, step);
        }
    }

    private void selectGrabAmmoDestination(@NotNull Game game, int step) throws RemoteException, InterruptedException, FileNotFoundException {
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

    private void selectGrabWeaponDestination(@NotNull Game game, int step) throws FileNotFoundException, InterruptedException {
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

    private void selectSPWeapon(@NotNull Game game) throws FileNotFoundException, RemoteException, InterruptedException {
        for (int i = 0; i < game.getWeapons(game.getCell(destination).getColor()).size(); i++)
            System.out.println((i+1) + ". " + game.getWeapons(game.getCell(destination).getColor()).get(i).getColor().escape() +
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

    private void selectMyDiscardWeapon(@NotNull Game game) throws FileNotFoundException, InterruptedException {
        List<Weapon> selectableWeapon = new ArrayList<>(game.getActualPlayer().getWeapons());
        for (int i = 0; i < selectableWeapon.size(); i++)
            System.out.println((i+1) + ". " + selectableWeapon.get(i).getColor().escape() +
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

    private void selectMyFireWeapon(@NotNull Game game) throws FileNotFoundException, InterruptedException {
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

    private void buildUsePowerUp(@NotNull Game game) throws FileNotFoundException, InterruptedException, RemoteException {
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

    private void selectPowerUpToUse(@NotNull Game game) throws FileNotFoundException, InterruptedException {
        List<PowerUp> usablePowerUps = new ArrayList<>();
        game.getActualPlayer().getPowerUps().stream().filter(e -> !e.getType().equals(PowerUp.Type.TAGBACK_GRENADE))
                .forEach(usablePowerUps::add);
        if (usablePowerUps.isEmpty()) System.out.println("Non hai powerups da utilizzare");
        for (int i = 0; i < usablePowerUps.size(); i++)
            System.out.println((i+1) + ". " + usablePowerUps.get(i).getAmmoColor().escape() + usablePowerUps.get(i).getType().name() +
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

    private void selectTargetingScopeTarget(@NotNull Game game) throws FileNotFoundException, InterruptedException {
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

    private void selectNewtonTarget(@NotNull Game game) throws FileNotFoundException, InterruptedException {
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

    private void selectNewtonDestination(@NotNull Game game) throws FileNotFoundException, InterruptedException {
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

    private void selectTeleporterDestination(@NotNull Game game) throws FileNotFoundException, InterruptedException {
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

    private void selectMyReloadWeapon(@NotNull Game game) throws FileNotFoundException, InterruptedException {
        List<Weapon> selectableWeapons = new ArrayList<>();
        game.getActualPlayer().getWeapons().parallelStream()
                .filter(e -> !game.getActualPlayer().isALoadedGun(e)).forEach(selectableWeapons::add);
        if (selectableWeapons.isEmpty())
            System.out.println("Non hai armi da ricaricare");
        for (int i = 0; i < selectableWeapons.size(); i++)
            System.out.println((i+1) + ". " + selectableWeapons.get(i).getColor().escape() +
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

    private void printDestinations(@NotNull Game game, @NotNull List<Point> possibleDestination) throws FileNotFoundException {
        for (int i = 0; i < possibleDestination.size(); i++)
            System.out.println((i + 1) + ". " + (possibleDestination.get(i).equals(game.getActualPlayer().getPosition()) ?
                    "Rimani fermo" : possibleDestination.get(i)));
        System.out.println(Utils.getStrings("cli").get("back_to_menu").getAsString());
    }

    private void printTargets(@NotNull Game game, @NotNull List<UUID> possibleTargets) throws FileNotFoundException {
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
                //todo: lockrifle
                System.out.println(Utils.getStrings("cli", "weapons_details", "lock_rifle").get("fire_description").getAsString());
                alternativeFire = false;
                doubleChoice(game);
                selectBasicVisibleTarget(game, new ArrayList<>());
                if (options == 1) selectFirstVisibleTarget(game, basicTarget);
                break;
            case MACHINE_GUN:
                //todo: machinegun
                break;
            case THOR:
                //todo: thor
                break;
            case PLASMA_GUN:
                //todo:
                break;
            case WHISPER:
                //todo:
                break;
            case ELECTROSCYTHE:
                //todo:
                break;
            case TRACTOR_BEAM:
                //todo:
                break;
            case VORTEX_CANNON:
                //todo:
                break;
            case FURNACE:
                //todo:
                break;
            case HEATSEEKER:
                //todo:
                break;
            case HELLION:
                //todo:
                break;
            case FLAMETHROWER:
                //todo:
                break;
            case GRENADE_LAUNCHER:
                //todo:
                break;
            case ROCKET_LAUNCHER:
                //todo:
                break;
            case RAILGUN:
                //todo:
                break;
            case CYBERBLADE:
                //todo:
                break;
            case ZX2:
                //todo:
                break;
            case SHOTGUN:
                //todo:
                break;
            case POWER_GLOVE:
                //todo:
                break;
            case SHOCKWAVE:
                //todo:
                break;
            case SLEDGEHAMMER:
                //todo:
                break;
        }
    }

    private void doubleChoice(@NotNull Game game) throws FileNotFoundException, InterruptedException {
        System.out.println(Utils.getStrings("cli", "actions", "fire_action", "select_fire_mode").get("standard_message").getAsString());
        System.out.println(Utils.getStrings("cli", "actions", "fire_action", "select_fire_mode").get(weapon.name().toLowerCase()).getAsString());
        System.out.println(Utils.getStrings("cli").get("back_to_menu").getAsString());
        String choice = getLine();
        if (Integer.parseInt(choice) == 1 || Integer.parseInt(choice) == 2)
            options = Integer.parseInt(choice) - 1;
        else {
            System.out.println(invalidChoice);
            doubleChoice(game);
        }
    }

    private void selectBasicVisibleTarget(@NotNull Game game, @NotNull List<UUID> unselectable) throws FileNotFoundException, InterruptedException {
        System.out.println(Utils.getStrings("cli", "weapons_details", weapon.name().toLowerCase(), "fire_details").get("select_target_basic"));
        if (basicTarget == null) basicTarget = new ArrayList<>();
        List<UUID> selectableTargets = new ArrayList<>();
        game.getPlayers().stream().filter(e -> !e.equals(game.getActualPlayer())).map(Player::getUuid).filter(e -> !unselectable.contains(e)).forEach(selectableTargets::add);
        if (selectableTargets.isEmpty())
            System.out.println("Non ci sono giocatori selezionabili");
        for (int i = 0; i < selectableTargets.size(); i++) {
            for (Player player : game.getPlayers()) {
                if (player.getUuid().equals(selectableTargets.get(i))) System.out.println((i+1) + player.getBoardType().escape() + player.getNickname() + stdColor);
            }
        }
        System.out.println(Utils.getStrings("cli").get("back_to_menu").getAsString());
        String choice = getLine();
        if (Integer.parseInt(choice) > 0 && Integer.parseInt(choice) < selectableTargets.size())
            basicTarget.add(selectableTargets.get(Integer.parseInt(choice) - 1));
        else {
            System.out.println(invalidChoice);
            selectBasicVisibleTarget(game, unselectable);
        }
    }

    private void selectFirstVisibleTarget(@NotNull Game game, @NotNull List<UUID> unselectable) {

    }
}
