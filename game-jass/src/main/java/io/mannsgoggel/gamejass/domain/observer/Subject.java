package io.mannsgoggel.gamejass.domain.observer;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Data
public class Subject<T> {
    private T state;

    private List<Subscription<T>> subscriptions = new ArrayList<>();

    public Subject(T initial) {
        this.state = initial;
    }

    public Subscription<T> subscribe(Observer<T> observer) {
        Subscription<T> subscription = new Subscription<>(observer, this);
        subscriptions.add(subscription);
        return subscription;
    }

    void unsubscribe(Subscription<T> subscription) {
        this.subscriptions.remove(subscription);
    }

    public void next(T state) {
        this.state = state;

        subscriptions.forEach(subscription -> subscription.getObserver().next(state));
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
