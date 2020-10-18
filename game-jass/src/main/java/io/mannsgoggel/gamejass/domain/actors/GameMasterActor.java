package com.manoggeli.gamejass.domain.actors;

import com.manoggeli.gamejass.domain.game.GameState;
import com.manoggeli.gamejass.domain.Store;
import com.manoggeli.gamejass.domain.game.CardDeckBuilder;
import com.manoggeli.gamejass.domain.game.JassActions;
import com.manoggeli.gamejass.observer.Observer;

public class GameMasterActor implements Observer<GameState> {

    private Store store;

    public GameMasterActor(Store store) {
        this.store = store;
    }

    @Override
    public void next(GameState state) {
        switch (state.getNextAction()) {
            case START_ROUND -> startRound(state);
            case HAND_OUT_CARDS -> handOutCards(state);
        }
    }

    private void startRound(GameState state) {
        store.dispatchAction(new JassActions.StartRound("game", CardDeckBuilder.buildWithoutTrump()));
    }

    private void handOutCards(GameState state) {




    }
}
