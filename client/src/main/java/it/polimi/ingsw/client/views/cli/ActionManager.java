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
import java.util.List;
import java.util.*;

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
            selectMyDiscardWeapon(game);
        }
    }

    private void selectAlternativePayment(@NotNull Game game) throws InterruptedException, FileNotFoundException, RemoteException{
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
        }
        else if (Integer.parseInt(choice) != selectablePowerUps.size() + 1) {
            System.out.println(invalidChoice);
            selectAlternativePayment(game);
        }
        else if (choice.charAt(0) == '*') actionMenu(game);
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

    private void selectStandardAction(@NotNull Game game) throws InterruptedException, FileNotFoundException {
        System.out.println(Utils.getStrings("cli", "possible_actions").get("standard_actions").getAsString());
        //todo: da qui reinderizza alle varie mosse
        String choice = getLine();

    }

    private void selectLastAction(@NotNull Game game) {
        System.out.println("1. Usa una powerup\n2. Ricarica un' arma\n3. Passa il turno");
        //todo: da qui reindirizza alla mossa
    }

    private void selectUltimateAction(@NotNull Game game) {
        System.out.println("1. Muoviti (opzionalmente e di massimo 3 passi) e raccogli rifornimenti\n2. " +
                "Muoviti (opzionalmente e di massimo 3 passi) e raccogli un'arma\n3. " +
                "Muoviti (opzionalmente e di massimo 2 passi), ricarica se l'arma che vuoi usare è scarica " +
                "e spara\n4. Usa una powerup\n5. Passa il turno");
        //todo: da qui reinderizza alle mosse
    }

    private void selectMyFireWeapon(@NotNull Game game) {
        List<Weapon> selectableWeapon = new ArrayList<>();
        if (game.getSkulls() != 0)
            game.getActualPlayer().getWeapons().parallelStream()
                    .filter(e -> game.getActualPlayer().isALoadedGun(e)).forEach(selectableWeapon::add);
        else selectableWeapon.addAll(game.getActualPlayer().getWeapons());
        if (selectableWeapon.isEmpty()) {
            System.out.println("Non hai armi con cui fare fuoco");
            return;
        }
        for (int i = 0; i < selectableWeapon.size(); i++)
            System.out.println((i + 1) + ". " + selectableWeapon.get(i).getColor().escape() + selectableWeapon.get(i).getName() +
                    "\u001b[0m" + (game.getActualPlayer().isALoadedGun(selectableWeapon.get(i)) ? "" : " (scarica)"));
        System.out.println((selectableWeapon.size() + 1) + ". Torna indietro");
        //todo: inserire in weapon
    }

    private void selectMyReloadWeapon(@NotNull Game game) {
        List<Weapon> selectableWeapon = new ArrayList<>();
        game.getActualPlayer().getWeapons().parallelStream()
                .filter(e -> !game.getActualPlayer().isALoadedGun(e)).forEach(selectableWeapon::add);
        if (selectableWeapon.isEmpty()) {
            System.out.println("Non hai armi da ricaricare");
            return;
        }
        for (int i = 0; i < selectableWeapon.size(); i++)
            System.out.println((i+1) + ". " + selectableWeapon.get(i).getColor().escape() +
                    selectableWeapon.get(i).getName() + "\u001b[0m");
        System.out.println((selectableWeapon.size()+1) + ". Torna indietro");
        //todo inserire in weapon o torna indietro
    }

    private void selectSPWeapon(@NotNull Game game) {
        if (game.getWeapons(game.getCell(destination).getColor()).isEmpty()) {
            System.out.println("Non ci sono armi da raccogliere");
            //todo: sistemare
            return;
        }
        for (int i = 0; i < game.getWeapons(game.getCell(destination).getColor()).size(); i++)
            System.out.println((i+1) + ". " + game.getWeapons(game.getCell(destination).getColor()).get(i).getColor().escape() +
                    game.getWeapons(game.getCell(destination).getColor()).get(i).getName() + "\u001b[0m");

        //todo insert in weapon
    }

    private void selectMyDiscardWeapon(@NotNull Game game) {
        List<Weapon> selectableWeapon = new ArrayList<>();
        game.getActualPlayer().getWeapons().parallelStream()
                .filter(e -> !game.getActualPlayer().isALoadedGun(e)).forEach(selectableWeapon::add);
        for (int i = 0; i < selectableWeapon.size(); i++)
            System.out.println((i+1) + ". " + selectableWeapon.get(i).getColor().escape() +
                    selectableWeapon.get(i).getName() + "\u001b[0m");
        System.out.println((selectableWeapon.size()+1) + ". Torna indietro");
    }

    private void selectPowerUpToUse(@NotNull Game game) {
        List<PowerUp> usablePowerUps = new ArrayList<>();
        game.getActualPlayer().getPowerUps().stream().filter(e -> !e.getType().equals(PowerUp.Type.TAGBACK_GRENADE))
                .forEach(usablePowerUps::add);
        if (usablePowerUps.isEmpty()) {
            System.out.println("Non hai powerups da utilizzare");
            //todo: sistemare
            return;
        }
        for (int i = 0; i < usablePowerUps.size(); i++)
            System.out.println((i+1) + ". " + usablePowerUps.get(i).getAmmoColor().escape() + usablePowerUps.get(i).getType().name() +
                    "\u001b[0m");
        System.out.println((usablePowerUps.size() + 1) + ". Torna a \"seleziona mossa\"");
    }

    private void selectMyDestination(@NotNull Game game, int step) {
        List<Point> possibleDestination = new ArrayList<>();
        for (int i = 0; i < Game.MAX_Y; i++)
            for (int j = 0; j < Game.MAX_X; j++)
                if (game.getCell(new Point(i, j)) != null && game.canMove(game.getActualPlayer().getPosition(), new Point(i, j), step))
                    possibleDestination.add(new Point(i, j));
        for (int i = 0; i < possibleDestination.size(); i++)
            System.out.println((i+1) + ". " + possibleDestination.get(i));
        System.out.println((possibleDestination.size()+1) + ". Rimani fermo");
        //todo: inserire destination
    }

    private void selectGrabAmmoDestination(@NotNull Game game, int step) {
        List<Point> possibleDestination = new ArrayList<>();
        for (int i = 0; i < Game.MAX_Y; i++)
            for (int j = 0; j < Game.MAX_X; j++)
                if (game.getCell(new Point(i, j)) != null && !game.getCell(new Point(i, j)).isSpawnPoint() &&
                        game.canMove(game.getActualPlayer().getPosition(), new Point(i, j), step))
                    possibleDestination.add(new Point(i, j));
        for (int i = 0; i < possibleDestination.size(); i++)
            System.out.println((i+1) + ". " + possibleDestination.get(i));
        if (!game.getCell(game.getActualPlayer().getPosition()).isSpawnPoint())
            System.out.println((possibleDestination.size()+1) + ". Rimani fermo");
        System.out.println(!game.getCell(game.getActualPlayer().getPosition()).isSpawnPoint() ?
                (possibleDestination.size()+2) : (possibleDestination.size()+1) + ". Torna indietro");
        //todo: inserisci destinatione e buildala pure qui l'action
    }

    private void selectGrabWeaponDestination(@NotNull Game game, int step) {
        List<Point> possibleDestination = new ArrayList<>();
        for (int i = 0; i < Game.MAX_Y; i++)
            for (int j = 0; j < Game.MAX_X; j++)
                if (game.getCell(new Point(i, j)) != null && game.getCell(new Point(i, j)).isSpawnPoint() &&
                        game.canMove(game.getActualPlayer().getPosition(), new Point(i, j), step))
                    possibleDestination.add(new Point(i, j));
        for (int i = 0; i < possibleDestination.size(); i++)
            System.out.println((i+1) + ". " + possibleDestination.get(i));
        if (game.getCell(game.getActualPlayer().getPosition()).isSpawnPoint())
            System.out.println((possibleDestination.size()+1) + ". Rimane fermo");
        System.out.println(game.getCell(game.getActualPlayer().getPosition()).isSpawnPoint() ?
                (possibleDestination.size()+2) : (possibleDestination.size()+1) + ". Torna indietro");
        //todo: insert in destination
    }

    private void buildMoveAction(@NotNull Game game) {
        var action = Action.Builder.create(game.getUuid()).buildMoveAction(Objects.requireNonNull(destination));
        try {
            Client.API.doAction(Objects.requireNonNull(Preferences.getToken()), action);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void buildGrabWeaponAction(@NotNull Game game, int step) throws InterruptedException, FileNotFoundException, RemoteException {
        selectGrabWeaponDestination(game, step);
        selectSPWeapon(game);
        if (game.getActualPlayer().getWeaponsSize() > 2) selectMyDiscardWeapon(game);
        selectAlternativePayment(game);
        var action = Action.Builder.create(game.getUuid())
                .buildWeaponGrabAction(destination, weapon, discardedWeapon, powerUpPayment);
        try {
            Client.API.doAction(Objects.requireNonNull(Preferences.getToken()), action);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void buildReloadAction(@NotNull Game game) {

    }

    private @NotNull String getLine() throws InterruptedException {
        var line = new Scanner(System.in).nextLine();
        if (line.equals("*")) throw new InterruptedException();
        return line;
    }
}
