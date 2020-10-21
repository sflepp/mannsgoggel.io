package io.mannsgoggel.gamejass.domain.action;

import io.mannsgoggel.gamejass.domain.game.GameState;
import io.mannsgoggel.gamejass.domain.game.JassActions;
import lombok.Data;

public interface Action<T> {
    JassActions.ActionType getAction();
    Action<T> toPlayerView(String player);
    String getPlayer();
    T getPayload();

    void apply(GameState state);

    @Data
    abstract class BaseAction<T> implements Action<T> {
        JassActions.ActionType action;
        String player;
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




