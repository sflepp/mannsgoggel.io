package com.manoggeli.gamejass.domain.actors;

import com.manoggeli.gamejass.domain.gameObjects.GameState;
import com.manoggeli.gamejass.domain.Jass;
import com.manoggeli.gamejass.domain.action.Action;
import com.manoggeli.gamejass.domain.gameObjects.CardDeckBuilder;
import com.manoggeli.gamejass.observer.Observer;

public class GameActor implements Observer<GameState> {

    private Jass jass;

    public GameActor(Jass jass) {
        this.jass = jass;
    }

    @Override
    public void next(GameState state) {
        switch (state.getNextAction()) {
            case START_ROUND -> startRound(state);
            case HAND_OUT_CARDS -> handOutCards(state);
        }
    }

    private void startRound(GameState state) {
        jass.dispatchAction(new Action.StartRound("game", CardDeckBuilder.buildWithoutTrump()));
    }

    private void handOutCards(GameState state) {




    }
}
