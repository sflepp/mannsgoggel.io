package io.mannsgoggel.gamejass.domain;

import io.mannsgoggel.gamejass.domain.action.Action;
import io.mannsgoggel.gamejass.domain.game.GameState;
import io.mannsgoggel.gamejass.domain.observer.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Store {
    private static final Logger LOGGER = LoggerFactory.getLogger(Store.class);
    private final List<Action> actions = new ArrayList<>();
    private final Subject<GameState> state = new Subject<>(null);

    private GameState currentState = new GameState();

    public void dispatchAction(Action action) {
        LOGGER.info(action.getPlayer() + " | " + action.getAction() + (action.getPayload() == null ? "" : (" | " + action.getPayload())));

        action.apply(currentState);
        state.next(currentState);

        actions.add(action);
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public Subject<GameState> getState() {
        return state;
    }
}
