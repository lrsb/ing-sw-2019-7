package it.polimi.ingsw.client.controllers.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * You can use NavigationController to create a root-child navigation behaviour between BaseViewController(s), with a LIFO logic.
 */
public class NavigationController {
    private final @NotNull ArrayList<BaseViewController> viewControllers = new ArrayList<>();

    /**
     * Create new NavigationController with as root specified.
     *
     * @param controllerClass The root view controller, a BaseViewController can't be reused.
     * @param <T>             View controller type.
     */
    @SuppressWarnings("unchecked")
    public @NotNull <T extends BaseViewController> NavigationController(@NotNull Class<T> controllerClass, @Nullable Object... params) {
        try {
            BaseViewController viewController;
            if (params == null || params.length == 0)
                viewController = (T) controllerClass.getDeclaredConstructors()[0].newInstance(this);
            else viewController = (T) controllerClass.getDeclaredConstructors()[0].newInstance(this, params);
            viewControllers.add(viewController);
            viewController.setVisible(true);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * When you have to present a new BaseViewController, you can use this method.
     * After the new viewController is presented, the previous is hidden.
     *
     * @param controllerClass The BaseViewController to present.
     * @param <T>             View controller type.
     * @param deleteFromStack If the actual view controller need to be removed from history.
     */
    @SuppressWarnings("unchecked")
    public <T extends BaseViewController> void presentViewController(boolean deleteFromStack, @NotNull Class<T> controllerClass, @Nullable Object... params) {
        try {
            BaseViewController viewController;
            if (params == null || params.length == 0)
                viewController = (T) controllerClass.getDeclaredConstructors()[0].newInstance(this);
            else viewController = (T) controllerClass.getDeclaredConstructors()[0].newInstance(this, params);
            viewControllers.add(viewController);
            viewControllers.get(viewControllers.size() - 2).setVisible(false);
            viewControllers.get(viewControllers.size() - 1).setVisible(true);
            if (deleteFromStack) disposeViewController(viewControllers.get(viewControllers.size() - 2));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * When you have to present a new BaseViewController, you can use this method.
     * After the new viewController is presented, the previous is hidden.
     *
     * @param controllerClass The BaseViewController to present.
     * @param <T>             View controller type.
     */
    public <T extends BaseViewController> void presentViewController(@NotNull Class<T> controllerClass, Object... params) {
        presentViewController(false, controllerClass, params);
    }

    /**
     * This hide the view controller is now visible and show the previous view controller.
     */
    public void popViewController() {
        if (viewControllers.size() < 2) close();
        else {
            disposeViewController(viewControllers.remove(viewControllers.size() - 1));
            viewControllers.get(viewControllers.size() - 1).setVisible(true);
        }
    }

    /**
     * Navigation is set back to the root view controller.
     */
    public void popToRootViewController() {
        if (viewControllers.size() < 2) return;
        var viewControllersToDispose = new ArrayList<>(viewControllers);
        var rootViewController = viewControllersToDispose.remove(0);
        viewControllersToDispose.forEach(this::disposeViewController);
        rootViewController.setVisible(true);
    }

    /**
     * Get the view controller at index.
     *
     * @param index Index of view controller.
     * @return The view controller.
     * @throws IndexOutOfBoundsException Thrown when index is wrong.
     */
    public BaseViewController getViewController(int index) throws IndexOutOfBoundsException {
        return viewControllers.get(index);
    }

    /**
     * Closes the controller.
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    public void close() {
        for (var i = 0; i < viewControllers.size(); i++) disposeViewController(viewControllers.get(i));
        viewControllers.clear();
        System.exit(0);
    }

    private void disposeViewController(@NotNull BaseViewController viewController) {
        viewController.controllerPopped();
        viewController.setVisible(false);
        viewController.dispose();
        viewControllers.remove(viewController);
    }
}
