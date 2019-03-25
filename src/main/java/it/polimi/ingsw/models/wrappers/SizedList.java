package it.polimi.ingsw.models.wrappers;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractList;
import java.util.ArrayList;

public class SizedList<E> extends AbstractList<E> {
    private ArrayList<E> list;
    private int capacity;

    public SizedList(int capacity) {
        this.capacity = capacity;
        list = new ArrayList<>();
    }

    @Contract(pure = true)
    public boolean isFull() {
        return size() > 2;
    }

    public void insert(@NotNull E object) {
        if (!isFull()) list.add(object);
    }

    public void insert(@NotNull E object, int index) {
        if (!isFull() && index < list.size() && index < capacity) list.add(index, object);
        throw new IndexOutOfBoundsException();
    }


    public E swap(@NotNull E object, int index) {
        if (index < list.size() && index < capacity) {
            var removedObject = list.remove(index);
            list.add(index, object);
            return removedObject;
        }
        throw new IndexOutOfBoundsException();
    }

    @NotNull
    @Override
    public E remove(int index) {
        if (index < capacity) return list.remove(index);
        throw new IndexOutOfBoundsException();
    }

    @Contract(pure = true)
    @Override
    public E get(int index) {
        if (index < capacity) return list.get(index);
        throw new IndexOutOfBoundsException();
    }

    @Contract(pure = true)
    @Override
    public int size() {
        return list.size();
    }

    @Contract(pure = true)
    public int getCapacity() {
        return capacity;
    }
}