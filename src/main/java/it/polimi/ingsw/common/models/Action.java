package it.polimi.ingsw.common.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.UUID;

public class Action implements Serializable {
    private static final long serialVersionUID = 1;

    private @NotNull UUID gameUuid;
    private @NotNull Type[] actions = {Type.NOTHING, Type.NOTHING};
    private @Nullable Weapon.Name weaponName;

    public @NotNull Type getFirstAction() {
        return actions[0];
    }

    public @NotNull Type getSecondAction() {
        return actions[1];
    }

    public @Nullable Weapon.Name getWeaponName() {
        return weaponName;
    }

    public @NotNull UUID getGameUuid() {
        return gameUuid;
    }

    public enum Type {
        NOTHING, FIRE
    }
}