package it.polimi.ingsw.client.views.cli.base;

import org.jetbrains.annotations.Contract;

public enum Color {
    ANSI_BLUE("\u001B[34m"),
    ANSI_CYAN("\u001B[36m"),
    ANSI_GREEN("\u001B[32m"),
    ANSI_PURPLE("\u001B[35m"),
    ANSI_RED("\u001B[31m"),
    ANSI_RESET("\u001B[0m"),
    ANSI_WHITE("\u001B[37m"),
    ANSI_YELLOW("\u001B[33m");

    private String escape;

    @Contract(pure = true)
    Color(String escape) {
        this.escape = escape;
    }

    @Contract(pure = true)
    public String escape() {
        return escape;
    }
}