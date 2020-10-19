package io.mannsgoggel.gamejass.domain.actors;

import io.mannsgoggel.gamejass.domain.Store;
import io.mannsgoggel.gamejass.domain.game.Card;
import io.mannsgoggel.gamejass.domain.game.GameState;

import static io.mannsgoggel.gamejass.domain.game.JassActions.*;

public class LocalGameMasterActor extends GameActor {

    public static final String NAME = "game-master";

    public LocalGameMasterActor(Store store) {
        super(store);
    }

    @Override
    public void next(GameState state) {
        var players = state.getPlayers();
        var nextAction = switch (state.getNextAction()) {
            case START_GAME             -> none();
            case START_ROUND            -> with(new StartRound(NAME));
            case HAND_OUT_CARDS         -> with(new HandOutCards(NAME, Card.CardDeckBuilder.buildAndShuffleFor(players)));
            case SET_STARTING_PLAYER    -> with(new SetStartingPlayer(NAME, players.get(0).getName()));
            case DECIDE_SHIFT           -> none();
            case SET_PLAYING_MODE       -> none();
            case START_STICH            -> none();
            case PLAY_CARD              -> none();
            case END_STICH              -> with(new EndStich(NAME));
            case END_ROUND              -> with(new EndRound(NAME));
            case END_GAME               -> with(new EndGame(NAME));
        };

        setNextAction(nextAction);
    }

    public void connect() {
        super.connect(NAME);
    }
}
