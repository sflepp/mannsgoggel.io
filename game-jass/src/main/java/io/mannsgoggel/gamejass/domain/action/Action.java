package io.mannsgoggel.gamejass.domain.action;

import io.mannsgoggel.gamejass.domain.game.GameState;
import io.mannsgoggel.gamejass.domain.game.JassActions;
import lombok.Data;

public interface Action<T> {
    String getPlayer();
    JassActions.ActionType getAction();
    T getPayload();
    Action<T> toPlayerView(String player);

    void apply(GameState state);

    @Data
    abstract class BaseAction<T> implements Action<T> {
        String player;
        JassActions.ActionType action;
        T payload;

        public BaseAction(JassActions.ActionType action, String player, T payload) {
            this.action = action;
            this.player = player;
            this.payload = payload;
        }

        @Override
        public Action<T> toPlayerView(String player) {
            return this;
        }
    }
}




