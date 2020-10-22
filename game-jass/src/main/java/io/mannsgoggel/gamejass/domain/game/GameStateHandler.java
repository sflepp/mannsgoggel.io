package io.mannsgoggel.gamejass.domain.game;

import io.mannsgoggel.gamejass.domain.action.Action;
import io.mannsgoggel.gamejass.domain.action.ActionNotAllowed;
import io.mannsgoggel.gamejass.domain.observer.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameStateHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameStateHandler.class);
    private final Subject<GameState> stateSubject = new Subject<>(new GameState());

    public void dispatchAction(Action<?> action) {
        LOGGER.info(action.getPlayer() + " | " + action.getAction() + (action.getPayload() == null ? "" : (" | " + action.getPayload())));


        if (!action.getAction().equals(stateSubject.getState().getNextAction())) {
            throw new ActionNotAllowed(
                    "Expected next action " + stateSubject.getState().getNextAction() + " but got " + action.getAction()
            );
        }

        if (!action.getPlayer().equals(stateSubject.getState().getNextPlayer())) {
            throw new ActionNotAllowed(
                    "Expected next player " + stateSubject.getState().getNextPlayer() + " but got " + action.getPlayer()
            );
        }

        action.apply(stateSubject.getState());
        stateSubject.getState().getHistory().add(action);
        stateSubject.next(stateSubject.getState());
    }

    public GameState getState() {
        return stateSubject.getState();
    }

    public Subject<GameState> getStateSubject() {
        return stateSubject;
    }
}
