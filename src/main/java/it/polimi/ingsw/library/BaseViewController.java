package it.polimi.ingsw.library;

import org.jetbrains.annotations.NotNull;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Base class to create a ViewController associated to a view {@link E}.
 *
 * @param <E> The view associated
 */
public abstract class BaseViewController<E extends JNavigationFrame> {
    private E frame;
    private NavigationController navigationController;

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
                getNavigationController().popViewController();
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
    public NavigationController getNavigationController() {
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
    void setNavigationController(NavigationController navigationController) {
        this.navigationController = navigationController;
    }
}