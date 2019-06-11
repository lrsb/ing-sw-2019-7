package it.polimi.ingsw.client.views.cli.base;

import it.polimi.ingsw.client.controllers.pregame.RoomViewController;
import it.polimi.ingsw.client.others.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CliMenuManager {
    public static <T> void startCli(@NotNull Class<T> aClass, boolean withFollettina) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        @Nullable Clip clip = null;
        if (withFollettina) try {
            clip = AudioSystem.getClip();
            var audioInputStream = AudioSystem.getAudioInputStream(Utils.getUrl(RoomViewController.class, "follettina", "wav"));
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        invoke(aClass.getMethod("start"), null);
        if (clip != null) clip.stop();
    }

    private static void invoke(@NotNull Method method, @Nullable Object object) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        @NotNull Object result;
        if (object == null) result = method.invoke(null);
        else result = method.invoke(null, object);
        if (result instanceof Segue) {
            var segue = (Segue) result;
            if (segue.getAClass() != null) {
                if (segue.getObject() != null)
                    invoke(segue.getAClass().getMethod(segue.getMenu(), segue.getObject().getClass()), segue.getObject());
                else invoke(segue.getAClass().getMethod(segue.getMenu()), null);
            } else {
                if (segue.getObject() != null)
                    invoke(method.getDeclaringClass().getMethod(segue.getMenu(), segue.getObject().getClass()), segue.getObject());
                else invoke(method.getDeclaringClass().getMethod(segue.getMenu()), null);
            }
        } else throw new IllegalArgumentException("You must return a segue");
    }
}