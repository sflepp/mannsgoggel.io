package io.mannsgoggel.gamejass.observer;

@FunctionalInterface
public interface Observer<T> {
    void next(T state);
}
