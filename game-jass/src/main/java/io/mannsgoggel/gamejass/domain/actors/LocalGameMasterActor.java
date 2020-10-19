package io.mannsgoggel.gamejass.domain.actors;

import io.mannsgoggel.gamejass.domain.Store;
import io.mannsgoggel.gamejass.domain.game.Card;
import io.mannsgoggel.gamejass.domain.game.GameState;
import io.mannsgoggel.gamejass.domain.game.JassActions;

public class LocalGameMasterActor extends GameActor {

    public static final String NAME = "game-master";

    public LocalGameMasterActor(Store store) {
        super(store);
    }

    @Override
    public void next(GameState state) {
        var players = state.getPlayers();

        getStore().dispatchAction(
                switch (state.getNextAction()) {
                    case START_ROUND            -> new JassActions.StartRound(NAME, null);
                    case HAND_OUT_CARDS         -> new JassActions.HandOutCards(NAME, Card.CardDeckBuilder.buildAndShuffleFor(players));
                    case SET_STARTING_PLAYER    -> new JassActions.SetStartingPlayer(NAME, players.get(0).getName());
                    case DECIDE_SHIFT           -> null;
                    case SET_PLAYING_MODE       -> null;
                    case START_STICH            -> null;
                    case PLAY_CARD              -> null;
                    case END_STICH              -> new JassActions.EndStich(NAME, null);
                    case END_ROUND              -> new JassActions.EndRound(NAME, null);
                    case END_GAME               -> null;
                }
        );
    }
}
