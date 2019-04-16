package it.polimi.ingsw.client.controllers.base;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * You can use NavigationController to create a root-child navigation behaviour between BaseViewController(s), with a LIFO logic.
 */
public class NavigationController implements Closeable {
    private final @NotNull ArrayList<ArrayList<BaseViewController>> viewControllers = new ArrayList<>();

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
            var controllerList = new ArrayList<BaseViewController>();
            controllerList.add(viewController);
            viewControllers.add(controllerList);
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
            var controllerList = new ArrayList<BaseViewController>();
            controllerList.add(viewController);
            appendViewControllers(controllerList);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * When you have to present a list of new BaseViewController, you can use this method.
     * After the new viewControllers are presented, the previous is hided.
     *
     * @param controllerClasses The BaseViewController to present.
     */
    public void presentViewControllers(@NotNull Class<?>... controllerClasses) {
        var controllers = Stream.of(controllerClasses).map(e -> {
            try {
                return (BaseViewController) e.getDeclaredConstructors()[0].newInstance(this);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                ex.printStackTrace();
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        if (controllers.size() == 0) return;
        appendViewControllers(new ArrayList<>(controllers));
    }

    private void appendViewControllers(ArrayList<BaseViewController> controllersList) {
        viewControllers.add(controllersList);
        viewControllers.get(viewControllers.size() - 2).forEach(e -> e.nextViewControllersInstantiated(controllersList));
        viewControllers.get(viewControllers.size() - 2).forEach(e -> e.setVisible(false));
        viewControllers.get(viewControllers.size() - 1).forEach(e -> e.setVisible(true));
    }

    /**
     * This hide the view controller is now visible and show the previous view controller.
     */
    public void popViewController() {
        if (viewControllers.size() < 2) close();
        else {
            var controller = viewControllers.remove(viewControllers.size() - 1);
            controller.forEach(BaseViewController::controllerPopped);
            controller.forEach(e -> e.setVisible(false));
            viewControllers.get(viewControllers.size() - 1).forEach(e -> e.setVisible(true));
        }
    }

    /**
     * Navigation is set back to the root view controller.
     */
    public void popToRootViewController() {
        if (viewControllers.size() < 2) return;
        var rootViewController = viewControllers.remove(0);
        rootViewController.forEach(e -> e.setVisible(true));
        viewControllers.forEach(e -> {
            e.forEach(f -> {
                f.controllerPopped();
                f.setVisible(false);
            });
            e.clear();
        });
        viewControllers.clear();
        viewControllers.add(rootViewController);
    }

    /**
     * Closes the controller.
     */
    @Override
    public void close() {
        viewControllers.forEach(e -> {
            e.forEach(f -> {
                f.controllerPopped();
                f.setVisible(false);
                f.dispose();
            });
            e.clear();
        });
        viewControllers.clear();
    }
}