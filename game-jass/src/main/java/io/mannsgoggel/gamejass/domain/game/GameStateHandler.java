package io.mannsgoggel.gamejass.domain.game;

import io.mannsgoggel.gamejass.domain.action.Action;
import io.mannsgoggel.gamejass.domain.action.Action.BaseAction;
import io.mannsgoggel.gamejass.domain.observer.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameStateHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameStateHandler.class);
    private final Subject<GameState> stateSubject = new Subject<>(new GameState());

    public void dispatchAction(Action<?> action) {
        var baseAction = (BaseAction<?>) action;
        LOGGER.info(baseAction.getPlayer() + " | " + baseAction.getAction() + (baseAction.getPayload() == null ? "" : (" | " + baseAction.getPayload())));

        action.apply(stateSubject.getState());
        stateSubject.getState().getActionHistory().add(action);
        stateSubject.next(stateSubject.getState());
    }

    public GameState getState() {
        return stateSubject.getState();
    }

    public Subject<GameState> getStateSubject() {
        return stateSubject;
    }
}
