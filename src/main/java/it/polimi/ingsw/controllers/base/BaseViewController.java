package it.polimi.ingsw.controllers.base;

import it.polimi.ingsw.views.base.JNavigationFrame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Optional;

/**
 * Base class to create a ViewController associated to a view {@link E}.
 *
 * @param <E> The view associated
 */
public abstract class BaseViewController<E extends JNavigationFrame> {
    private E frame;
    private @Nullable NavigationController navigationController;

    /**
     * Attach the frame E to this controller.
     * A frame must be unique, so it can't be reused between navigation controllers.
     *
     * @param frame The frame
     */
    public BaseViewController(@NotNull E frame) {
        this.frame = frame;
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                Optional.ofNullable(getNavigationController()).ifPresent(NavigationController::popViewController);
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

    /**
     * @return The frame associated with this controller.
     */
    public E getFrame() {
        return frame;
    }

    /**
     * @return The {@link NavigationController} associated with this controller.
     */
    public @Nullable NavigationController getNavigationController() {
        return navigationController;
    }

    /**
     * @return true if this view controller is attached to a {@link NavigationController}. True not means the controller is visible.
     */
    public boolean isAttachedToNavigationController() {
        return navigationController != null;
    }

    /**
     * NEVER USE THIS METHOD.
     */
    void setNavigationController(@Nullable NavigationController navigationController) {
        this.navigationController = navigationController;
    }
}