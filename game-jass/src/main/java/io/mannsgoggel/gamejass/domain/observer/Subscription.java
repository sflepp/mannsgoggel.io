package io.mannsgoggel.gamejass.domain.observer;

public class Subscription<T> {

    private final Observer<T> observer;
    private final Subject<T> subject;

    public Subscription(Observer<T> observer, Subject<T> subject) {
        this.observer = observer;
        this.subject = subject;
    }

    public void unsubscribe() {
        this.subject.unsubscribe(this);
    }

    public Observer<T> getObserver() {
        return observer;
    }
}
