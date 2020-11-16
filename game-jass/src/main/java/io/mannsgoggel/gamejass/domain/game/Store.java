package io.mannsgoggel.gamejass.domain.game;

import io.mannsgoggel.gamejass.domain.game.action.ActionNotAllowed;
import io.mannsgoggel.gamejass.domain.game.action.Actions;
import io.mannsgoggel.gamejass.domain.game.state.Selectors;
import io.mannsgoggel.gamejass.domain.game.state.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;

import java.util.function.Consumer;

public class Store {
    private static final Logger LOGGER = LoggerFactory.getLogger(Store.class);

    private final ReplayProcessor<State> state$ = ReplayProcessor.create();
    private State state = State.initialState();

    public void subscribe(Consumer<State> consumer) {
        state$.subscribe(consumer);
    }

    public void dispatch(Actions.Action<?> action) {
        LOGGER.info(state.getRevision() + ": " + (action.getPlayer() == null ? "" : action.getPlayer() + " | ") + action.getAction() + (action.getPayload() == null ? "" : (" | " + action.getPayload())));

        if (!action.getAction().equals(state.getNextAction())) {
            throw new ActionNotAllowed(
                    "Expected next action " + state.getNextAction() + " but got " + action.getAction()
            );
        }

        if (Selectors.isNotNextPlayer(state, action.getPlayer())) {
            throw new ActionNotAllowed(
                    "Expected next player " + state.getNextPlayer() + " but got " + action.getPlayer()
            );
        }

        var newState = action.reduce(state)
                .revision(state.getRevision() + 1)
                .build();

        state = newState;
        state$.onNext(newState);
    }

    public State latestState() {
        return state;
    }

    public Flux<State> getState$() {
        return state$;
    }
}
