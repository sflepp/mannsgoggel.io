package com.manoggeli.gamejass.domain;

import com.manoggeli.gamejass.domain.action.Action;
import com.manoggeli.gamejass.domain.game.GameState;
import com.manoggeli.gamejass.observer.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Store {
    private List<Action> actions = new ArrayList<>();
    private Subject<GameState> currentState = new Subject<>(null);

    public void dispatchAction(Action action) {
        currentState.next(
                Reducer.reduceAll(
                        Stream.concat(actions.stream(), List.of(action).stream()).collect(Collectors.toList())
                )
        );

        actions.add(action);
    }
}
