package io.mannsgoggel.gamejass.domain.actors;

import io.mannsgoggel.gamejass.domain.Store;
import io.mannsgoggel.gamejass.domain.game.GameState;
import io.mannsgoggel.gamejass.observer.Observer;
import io.mannsgoggel.gamejass.observer.Subscription;

public abstract class GameActor implements Observer<GameState> {
    private final Store store;
    private Subscription<GameState> subscription;

    GameActor(Store store) {
        this.store = store;
    }

    public void connect(String playerName) {
        this.subscription = store.getState()
                .filter(state -> playerName.equals(state.getNextPlayer()))
                .subscribe(this);
    }

    public void disconnect() {
        this.subscription.unsubscribe();
    }

    public Store getStore() {
        return store;
    }
}
