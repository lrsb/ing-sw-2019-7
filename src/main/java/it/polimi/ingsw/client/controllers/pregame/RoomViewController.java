package it.polimi.ingsw.client.controllers.pregame;

import com.intellij.uiDesigner.core.GridLayoutManager;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.client.controllers.base.BaseViewController;
import it.polimi.ingsw.client.controllers.base.NavigationController;
import it.polimi.ingsw.client.controllers.game.GameViewController;
import it.polimi.ingsw.client.others.Preferences;
import it.polimi.ingsw.client.others.Utils;
import it.polimi.ingsw.common.models.Room;
import it.polimi.ingsw.common.network.exceptions.UserRemoteException;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.UUID;

public class RoomViewController extends BaseViewController {
    private @NotNull UUID roomUuid;

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    public RoomViewController(@NotNull NavigationController navigationController, @NotNull Object... params) {
        super("", 600, 400, navigationController);
        var room = (Room) params[0];
        roomUuid = room.getUuid();
        Preferences.getTokenOrJumpBack(getNavigationController()).ifPresent(e -> {
            try {
                Client.API.addRoomListener(e, room.getUuid(), f -> {
                    //TODO: update room
                    if (f.isGameCreated())
                        getNavigationController().presentViewController(true, GameViewController.class, Client.API.getActiveGame(e));
                });
            } catch (UserRemoteException ex) {
                ex.printStackTrace();
                Utils.jumpBackToLogin(getNavigationController());
            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });
    }

    @Override
    protected void controllerPopped() {
        Preferences.getTokenOrJumpBack(getNavigationController()).ifPresent(e -> {
            try {
                Client.API.removeRoomListener(e, roomUuid);
            } catch (UserRemoteException ex) {
                ex.printStackTrace();
                Utils.jumpBackToLogin(getNavigationController());
            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    }
}
