package fr.maxlego08.menu.api.utils;

public class Tuples<T,U> {
    public final T first;
    public final U second;

    public Tuples(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return this.first;
    }

    public U getSecond() {
        return this.second;
    }
}
