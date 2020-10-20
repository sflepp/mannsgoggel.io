package io.mannsgoggel.gamejass.domain.action;

import io.mannsgoggel.gamejass.domain.game.GameState;
import io.mannsgoggel.gamejass.domain.game.JassActions;

public interface Action<T> {
    JassActions.ActionType getAction();

    String getPlayer();

    T getPayload();

    void apply(GameState state);

    abstract class BaseAction<T> implements Action<T> {
        private final JassActions.ActionType action;
        private final String player;
        private T payload;

        public BaseAction(JassActions.ActionType action, String player, T payload) {
            this.action = action;
            this.player = player;
            this.payload = payload;
        }

        @Override
        public JassActions.ActionType getAction() {
            return action;
        }

        @Override
        public String getPlayer() {
            return player;
        }

        @Override
        public T getPayload() {
            return payload;
        }
    }
}




