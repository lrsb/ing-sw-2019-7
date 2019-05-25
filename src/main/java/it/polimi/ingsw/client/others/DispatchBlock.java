package it.polimi.ingsw.client.others;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;

public class DispatchBlock {
    private int call = 0;
    private @Nullable Callable<Void> callable;

    public void add() {
        call++;
    }

    public void remove() {
        call--;
        if (call == 0 && callable != null) {
            try {
                callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setCallable(@Nullable Callable<Void> callable) {
        this.callable = callable;
    }
}