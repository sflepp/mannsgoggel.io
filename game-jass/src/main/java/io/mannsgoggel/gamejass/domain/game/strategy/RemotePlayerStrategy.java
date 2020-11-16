package io.mannsgoggel.gamejass.domain.game.strategy;

import io.mannsgoggel.gamejass.domain.game.action.RemoteAction;
import io.mannsgoggel.gamejass.domain.game.action.RequestRemoteAction;
import io.mannsgoggel.gamejass.domain.game.state.State;

import java.util.function.Function;

public interface RemotePlayerStrategy {
    void nextState(State state);
    void requestRemoteAction(RequestRemoteAction action);
    void registerOnRemoteAction(Function<RemoteAction, Void> task);
}
