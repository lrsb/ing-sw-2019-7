package it.polimi.ingsw.common.models.wrappers;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Wrapper class, a triplet!
 *
 * @param <T> The type of the triplet.
 */
public class Triplet<T> {
    private @Nullable T first;
    private @Nullable T second;
    private @Nullable T third;

    /**
     * Create a triplet from a list, the list must have at least three elements.
     *
     * @param list The list
     */
    public Triplet(@NotNull List<T> list) {
        this.first = list.get(0);
        this.second = list.get(1);
        this.third = list.get(2);
    }

    /**
     * Create a triplet from three elements.
     *
     * @param first  The first element.
     * @param second The second element.
     * @param third  The third element.
     */
    @Contract(pure = true)
    public Triplet(@Nullable T first, @Nullable T second, @Nullable T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    /**
     * Return the first element.
     *
     * @return The first element.
     */
    public @Nullable T getFirst() {
        return first;
    }

    /**
     * Return the second element.
     *
     * @return The second element.
     */
    public @Nullable T getSecond() {
        return second;
    }

    /**
     * Return the third element.
     *
     * @return The third element.
     */
    public @Nullable T getThird() {
        return third;
    }

    /**
     * Get the i-element.
     *
     * @param i The index, will be evaluated in modulo 3.
     * @return The i element.
     */
    public @Nullable T get(int i) {
        switch (i % 3) {
            case 0:
                return first;
            case 1:
                return second;
            case 2:
                return third;
            default:
                return null;
        }
    }
}