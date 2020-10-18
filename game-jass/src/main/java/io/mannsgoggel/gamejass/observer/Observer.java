package com.manoggeli.gamejass.observer;

@FunctionalInterface
public interface Observer<T> {
    void next(T state);
}
