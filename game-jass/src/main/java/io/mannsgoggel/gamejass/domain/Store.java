package io.mannsgoggel.gamejass.domain;

import io.mannsgoggel.gamejass.domain.action.Action;
import io.mannsgoggel.gamejass.domain.game.GameState;
import io.mannsgoggel.gamejass.observer.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Store {
    private List<Action> actions = new ArrayList<>();
    private Subject<GameState> state = new Subject<>(null);

    public void dispatchAction(Action action) {
        state.next(
                Reducer.reduceAll(
                        Stream.concat(actions.stream(), List.of(action).stream()).collect(Collectors.toList())
                )
        );

        actions.add(action);
    }

    public Subject<GameState> getState() {
        return this.state;
    }
}
