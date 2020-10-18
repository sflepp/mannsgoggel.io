package io.mannsgoggel.gamejass.observer;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class Subject<T> {
    private T state;
    private Set<Observer<T>> observers = new HashSet<>();

    public Subject(T initial) {
        this.state = initial;
    }

    public void subscribe(Observer<T> observer) {
        observers.add(observer);
    }

    public void next(T state) {
        this.state = state;

        observers.forEach(observer -> observer.next(state));
    }

    public Subject<T> filter(Function<T, Boolean> filterFunction) {
        var newSubject = new Subject<>(state);

        subscribe(state -> {
            if (filterFunction.apply(state)) {
                newSubject.next(state);
            }
        });

        return newSubject;
    }
}
