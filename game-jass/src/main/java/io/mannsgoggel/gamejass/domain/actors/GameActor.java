package io.mannsgoggel.gamejass.domain.actors;

import io.mannsgoggel.gamejass.domain.Store;
import io.mannsgoggel.gamejass.domain.action.Action;
import io.mannsgoggel.gamejass.domain.game.GameState;
import io.mannsgoggel.gamejass.domain.observer.Observer;
import io.mannsgoggel.gamejass.domain.observer.Subscription;

import java.util.Optional;

public abstract class GameActor implements Observer<GameState> {
    private final Store store;
    private Subscription<GameState> subscription;
    private Optional<? extends Action> nextAction = Optional.empty();
    private Boolean gameEnded = false;

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

    public Optional<? extends Action> getNextAction() {
        return nextAction;
    }

    public void setNextAction(Optional<? extends Action> nextAction) {
        this.nextAction = nextAction;
    }

    public Boolean getGameEnded() {
        return gameEnded;
    }

    public void setGameEnded(Boolean gameEnded) {
        this.gameEnded = gameEnded;
    }

    Optional<? extends Action> none() {
        return Optional.empty();
    }

    Optional<? extends Action> with(Action action) {
        return Optional.of(action);
    }

    public void dispatchAction() {
        if (nextAction.isPresent()) {
            var next = nextAction;
            nextAction = Optional.empty();
            store.dispatchAction(next.get());
        }
    }
}
