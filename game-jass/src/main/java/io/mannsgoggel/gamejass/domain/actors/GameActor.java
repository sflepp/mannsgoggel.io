package io.mannsgoggel.gamejass.domain.actors;

import io.mannsgoggel.gamejass.domain.game.GameState;
import io.mannsgoggel.gamejass.domain.game.GameStateHandler;
import io.mannsgoggel.gamejass.domain.observer.Observer;
import io.mannsgoggel.gamejass.domain.observer.Subscription;
import lombok.Data;

@Data
public abstract class GameActor implements Observer<GameState> {
    private final String name;
    GameStateHandler handler;
    private Subscription<GameState> subscription;

    public void connect() {
        this.subscription = handler.getStateSubject()
                .filter(state -> name.equals(state.getNextPlayer()))
                .subscribe(this);
    }

    public void disconnect() {
        this.subscription.unsubscribe();
    }
}
