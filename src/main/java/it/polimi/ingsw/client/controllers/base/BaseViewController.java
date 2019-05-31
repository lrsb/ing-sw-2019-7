package it.polimi.ingsw.client.controllers.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public abstract class BaseViewController extends JFrame {
    private @Nullable NavigationController navigationController;

    public BaseViewController(@NotNull String title, int width, int height, @Nullable NavigationController navigationController) {
        this.navigationController = navigationController;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setBounds(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - width / 2, Toolkit.getDefaultToolkit().getScreenSize().height / 2 - height / 2, width, height);
        setResizable(false);
        setTitle(title);
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                if (navigationController != null) navigationController.popViewController();
                else dispose();
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

    protected @Nullable NavigationController getNavigationController() {
        return navigationController;
    }

    protected void controllerPopped() {
    }

    protected void setEnableFullscreen(boolean value) {
        getRootPane().putClientProperty("apple.awt.fullscreenable", value);
    }
}