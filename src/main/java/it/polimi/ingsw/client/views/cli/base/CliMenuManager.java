package it.polimi.ingsw.client.views.cli.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CliMenuManager {
    public static <T> void startCli(@NotNull Class<T> aClass) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        invoke(aClass.getMethod("start"), null);
    }

    private static void invoke(@NotNull Method method, @Nullable Object object) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        @NotNull Object result;
        if (object == null) result = method.invoke(null);
        else result = method.invoke(null, object);
        if (result instanceof Segue) {
            var segue = (Segue) result;
            if (segue.getAClass() != null) {
                if (segue.getObject() != null)
                    invoke(segue.getAClass().getMethod(segue.getMenu(), Object.class), segue.getObject());
                else invoke(segue.getAClass().getMethod(segue.getMenu()), null);
            } else {
                if (segue.getObject() != null)
                    invoke(method.getDeclaringClass().getMethod(segue.getMenu(), Object.class), segue.getObject());
                else invoke(method.getDeclaringClass().getMethod(segue.getMenu()), null);
            }
        } else throw new IllegalArgumentException("You must return a segue");
    }
}