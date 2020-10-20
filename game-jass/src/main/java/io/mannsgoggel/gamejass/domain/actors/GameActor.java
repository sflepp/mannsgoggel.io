package io.mannsgoggel.gamejass.domain.actors;

import io.mannsgoggel.gamejass.domain.game.GameStateHandler;
import io.mannsgoggel.gamejass.domain.action.Action;
import io.mannsgoggel.gamejass.domain.game.GameState;
import io.mannsgoggel.gamejass.domain.observer.Observer;
import io.mannsgoggel.gamejass.domain.observer.Subscription;
import lombok.Data;

import java.util.Optional;

@Data
public abstract class GameActor implements Observer<GameState> {
    private final String name;
    private GameStateHandler gameStateHandler;
    private Subscription<GameState> subscription;
    private Optional<Action<?>> nextAction = Optional.empty();
    private Boolean gameEnded = false;

    public void connect() {
        this.subscription = gameStateHandler.getStateSubject()
                .filter(state -> name.equals(state.getNextPlayer()))
                .subscribe(this);
    }

    public void disconnect() {
        this.subscription.unsubscribe();
    }

    Optional<Action<?>> none() {
        return Optional.empty();
    }

    Optional<Action<?>> with(Action<?> action) {
        return Optional.of(action);
    }

    public void dispatchAction() {
        if (nextAction.isPresent()) {
            var next = nextAction;
            nextAction = Optional.empty();
            gameStateHandler.dispatchAction(next.get());
        }
    }
}
