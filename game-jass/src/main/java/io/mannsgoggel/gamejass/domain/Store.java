package io.mannsgoggel.gamejass.domain;

import io.mannsgoggel.gamejass.domain.action.Action;
import io.mannsgoggel.gamejass.domain.game.GameState;
import io.mannsgoggel.gamejass.domain.observer.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Store {
    private static final Logger LOGGER = LoggerFactory.getLogger(Store.class);
    private final List<Action> actions = new ArrayList<>();
    private final Subject<GameState> state = new Subject<>(null);
    private GameState currentState;

    public void dispatchAction(Action action) {

        LOGGER.info("[" + action.getPlayer() + ":" + action.getAction() + "] " + action.getPayload());

        currentState = Reducer.reduceAll(
                Stream.concat(actions.stream(), List.of(action).stream()).collect(Collectors.toList())
        );

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
