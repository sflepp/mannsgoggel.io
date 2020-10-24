package io.mannsgoggel.gamejass.domain.player;

import io.mannsgoggel.gamejass.domain.action.RemoteAction;
import io.mannsgoggel.gamejass.domain.action.RequestRemoteAction;
import io.mannsgoggel.gamejass.domain.game.GameState;

import java.util.function.Function;

public interface RemotePlayerStrategy {
    void nextState(GameState gameState);
    void requestRemoteAction(RequestRemoteAction action);
    void registerOnRemoteAction(Function<RemoteAction, Void> task);
}
