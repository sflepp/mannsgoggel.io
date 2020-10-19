package io.mannsgoggel.gamejass.domain.observer;

@FunctionalInterface
public interface Observer<T> {
    void next(T state);
}
