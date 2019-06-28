package it.polimi.ingsw.client.views.cli.base;

import it.polimi.ingsw.common.models.Cell;
import org.jetbrains.annotations.NotNull;

public class TypeCell {
    private Character character;
    private String color;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(@NotNull Character character) {
        this.character = character;
        this.color = Cell.Color.WHITE.escape();
    }

    public void setAll(@NotNull Character character, @NotNull String color) {
        this.color = color;
        this.character = character;
    }

}
