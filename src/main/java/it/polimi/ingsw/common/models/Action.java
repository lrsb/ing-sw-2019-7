package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class Action implements Serializable {
    private static final long serialVersionUID = 1;

    private @NotNull Type[] actions = {Type.NOTHING, Type.NOTHING};

    public Type getFirstAction() {
        return actions[0];
    }

    public Type getSecondAction() {
        return actions[1];
    }

    public enum Type {
        NOTHING
    }
}