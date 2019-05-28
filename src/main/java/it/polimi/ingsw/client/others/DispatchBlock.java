package it.polimi.ingsw.client.others;

import org.jetbrains.annotations.Nullable;

public class DispatchBlock {
    private int call = 0;
    private @Nullable Callable callable;

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

    public void setCallable(@Nullable Callable callable) {
        this.callable = callable;
    }

    public interface Callable {
        void call();
    }
}