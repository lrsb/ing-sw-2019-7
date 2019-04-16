package it.polimi.ingsw.client.controllers.base;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

public abstract class BaseViewController extends JFrame {
    private @NotNull NavigationController navigationController;

    public BaseViewController(int width, int height, @NotNull NavigationController navigationController) {
        this.navigationController = navigationController;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - width / 2, Toolkit.getDefaultToolkit().getScreenSize().height / 2 - height / 2, width, height);
        setResizable(false);
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                navigationController.popViewController();
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) onShow();
    }

    protected void onShow() {
    }

    protected @NotNull NavigationController getNavigationController() {
        return navigationController;
    }

    public <T extends BaseViewController> void nextViewControllersInstantiated(ArrayList<T> viewControllers) {
    }

    public void controllerPopped() {
    }
}