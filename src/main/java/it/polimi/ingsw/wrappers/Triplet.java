package it.polimi.ingsw.wrappers;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Wrapper class, a triplet!
 *
 * @param <E> The type of the triplet.
 */
public class Triplet<E> {
    private @Nullable E first;
    private @Nullable E second;
    private @Nullable E third;

    /**
     * Create a triplet from a list, the list must have at least three elements.
     *
     * @param list The list
     */
    public Triplet(@NotNull List<E> list) {
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
    public Triplet(@Nullable E first, @Nullable E second, @Nullable E third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    /**
     * Return the first element.
     *
     * @return The first element.
     */
    public @Nullable E getFirst() {
        return first;
    }

    /**
     * Return the second element.
     *
     * @return The second element.
     */
    public @Nullable E getSecond() {
        return second;
    }

    /**
     * Return the third element.
     *
     * @return The third element.
     */
    public @Nullable E getThird() {
        return third;
    }

    /**
     * Get the i-element.
     *
     * @param i The index, will be evaluated in modulo 3.
     * @return The i element.
     */
    public @Nullable E get(int i) {
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