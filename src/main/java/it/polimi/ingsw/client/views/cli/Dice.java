package it.polimi.ingsw.client.views.cli;

import java.util.Random;

public class Dice {
    // U+2680 to U+2685

    public static final String[] faces = {

            "\u2680",
            "\u2681",
            "\u2682",
            "\u2683",
            "\u2684",
            "\u2685"
    };
    String face = "";
    private Color color;

    public Dice(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        String escape = this.color.escape();
        return escape + "[" + face + "]" + Color.RESET;
    }

    void dump() {
        System.out.println(this);
    }

    public void roll() {
        int count = faces.length;
        Random rand = new Random();
        int index = rand.nextInt(count);
        this.face = faces[index];
    }


}