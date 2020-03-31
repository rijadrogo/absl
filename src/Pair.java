// This is a personal academic project. Dear PVS-Studio, please check it.
// PVS-Studio Static Code Analyzer for C, C++, C#, and Java: http://www.viva64.com

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Pair<T, U> {
    private T first;
    private U second;

    public Pair() {
        this.first = null;
        this.second = null;
    }

    public Pair(final T first, final U second) {
        this.first = first;
        this.second = second;
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static <U, V> @NotNull Pair<U, V> of(final U first, final V second) {
        return new Pair<>(first, second);
    }

    @Contract(value = "_ -> param1", pure = true)
    public static <U, V> @NotNull Pair<U, V> of(final @NotNull Pair<U, V> pair) {
        return pair;
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(final T first) {
        this.first = first;
    }

    public U getSecond() {
        return second;
    }

    public void setSecond(final U second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "Pair{"
            + "first= " + first + ", second= " + second + '}';
    }
}
