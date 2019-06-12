package it.polimi.ingsw.client.views.cli.base;

import it.polimi.ingsw.common.models.Cell;

public class TypeCell {
    private Character character;
    private Cell.Color color;

    public Cell.Color getColor() {
        return color;
    }

    public void setColor(Cell.Color color) {
        this.color = color;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
        this.color = Cell.Color.WHITE;
    }

    public void setAll(Character character, Cell.Color color) {
        this.color = color;
        this.character = character;
    }

}
