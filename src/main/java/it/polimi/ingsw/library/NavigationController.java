package it.polimi.ingsw.library;

import java.util.ArrayList;

/**
 * You can use NavigationController to create a root-child navigation behaviour between BaseViewController(s), with a LIFO logic.
 */
public class NavigationController {
    private ArrayList<BaseViewController> baseViewControllers = new ArrayList<>();

    /**
     * Crate new NavigationController with as root specified.
     *
     * @param rootViewController The root view controller, a BaseViewController can't be reused.
     */
    public NavigationController(BaseViewController rootViewController) {
        baseViewControllers.add(rootViewController);
        rootViewController.getFrame().setVisible(true);
        rootViewController.setNavigationController(this);
    }

    /**
     * When you have to present a new BaseViewController, you can use this method.
     * After the new viewController is presented, the previous is hided.
     *
     * @param viewController The BaseViewController to present.
     */
    public void presentViewController(BaseViewController viewController) {
        baseViewControllers.get(baseViewControllers.size() - 1).getFrame().setVisible(false);
        baseViewControllers.add(viewController);
        viewController.getFrame().setVisible(true);
        viewController.setNavigationController(this);
    }

    /**
     * This hide the view controller is now visible and show the previous view controller.
     */
    public void popViewController() {
        if (baseViewControllers.size() < 2) baseViewControllers.get(0).getFrame().dispose();
        else {
            var viewController = baseViewControllers.remove(baseViewControllers.size() - 1);
            viewController.getFrame().setVisible(false);
            viewController.setNavigationController(null);
            baseViewControllers.get(baseViewControllers.size() - 1).getFrame().setVisible(true);
        }
    }

    /**
     * Navigation is set back to the root view controller.
     */
    public void popToRootViewController() {
        if (baseViewControllers.size() < 2) return;
        var rootViewController = baseViewControllers.remove(0);
        rootViewController.getFrame().setVisible(true);
        baseViewControllers.forEach(e -> {
            e.getFrame().setVisible(false);
            e.setNavigationController(null);
        });
        baseViewControllers.clear();
        baseViewControllers.add(rootViewController);
    }
}