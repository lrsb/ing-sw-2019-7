package it.polimi.ingsw.client.controllers.base;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * You can use NavigationController to create a root-child navigation behaviour between BaseViewController(s), with a LIFO logic.
 */
public class NavigationController {
    private final @NotNull ArrayList<BaseViewController> viewControllers = new ArrayList<>();

    /**
     * Crate new NavigationController with as root specified.
     *
     * @param controllerClass The root view controller, a BaseViewController can't be reused.
     * @param <T> View controller type.
     */
    public <T extends BaseViewController> NavigationController(@NotNull Class<T> controllerClass) {
        try {
            @SuppressWarnings("unchecked")
            var viewController = (T) controllerClass.getDeclaredConstructors()[0].newInstance(this);
            viewControllers.add(viewController);
            viewController.setVisible(true);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * When you have to present a new BaseViewController, you can use this method.
     * After the new viewController is presented, the previous is hided.
     *
     * @param controllerClass The BaseViewController to present.
     * @param <T> View controller type.
     */
    public <T extends BaseViewController> void presentViewController(@NotNull Class<T> controllerClass) {
        try {
            //noinspection unchecked
            var viewController = (T) controllerClass.getDeclaredConstructors()[0].newInstance(this);
            viewControllers.add(viewController);
            viewControllers.get(viewControllers.size() - 2).nextViewControllerInstantiated(viewController);
            viewControllers.get(viewControllers.size() - 2).setVisible(false);
            viewControllers.get(viewControllers.size() - 1).setVisible(true);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * This hide the view controller is now visible and show the previous view controller.
     */
    public void popViewController() {
        if (viewControllers.size() < 2) {
            viewControllers.get(0).dispose();
            viewControllers.clear();
        } else {
            viewControllers.remove(viewControllers.size() - 1).setVisible(false);
            viewControllers.get(viewControllers.size() - 1).setVisible(true);
        }
    }

    /**
     * Navigation is set back to the root view controller.
     */
    public void popToRootViewController() {
        if (viewControllers.size() < 2) return;
        var rootViewController = viewControllers.remove(0);
        rootViewController.setVisible(true);
        viewControllers.forEach(e -> e.setVisible(false));
        viewControllers.clear();
        viewControllers.add(rootViewController);
    }
}