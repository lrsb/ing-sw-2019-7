package it.polimi.ingsw.models;

public class Bounds {
    private char bounds;

    public Bounds(char bounds) {
        this.bounds = bounds;
    }

    public boolean isDoor(Direction direction) {
        if (!isOpen(direction)) return false;
        switch (direction) {
            case N:
                return (bounds & 0x40) == 0x40;
            case S:
                return (bounds & 0x04) == 0x04;
            case W:
                return (bounds & 0x01) == 0x01;
            case E:
                return (bounds & 0x10) == 0x10;
        }
        return false;
    }

    public boolean isOpen(Direction direction) {
        switch (direction) {
            case N:
                return (bounds & 0x80) == 0x80;
            case S:
                return (bounds & 0x08) == 0x08;
            case W:
                return (bounds & 0x02) == 0x02;
            case E:
                return (bounds & 0x20) == 0x20;
        }
        return false;
    }

    public void setDoor(Direction direction, boolean isDoor) {
        //TODO: implement
    }

    public void setOpen(Direction direction, boolean isOpen) {
        //TODO: implement
    }

    enum Direction {
        N, S, W, E
    }
}
