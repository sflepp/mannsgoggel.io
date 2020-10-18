package io.mannsgoggel.gamejass.domain.actors;

import io.mannsgoggel.gamejass.domain.game.GameState;
import io.mannsgoggel.gamejass.domain.Store;
import io.mannsgoggel.gamejass.domain.game.CardDeckBuilder;
import io.mannsgoggel.gamejass.domain.game.JassActions;
import io.mannsgoggel.gamejass.observer.Observer;

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
