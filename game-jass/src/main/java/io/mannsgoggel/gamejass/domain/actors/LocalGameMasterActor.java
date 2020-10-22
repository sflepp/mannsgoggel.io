package io.mannsgoggel.gamejass.domain.actors;

import io.mannsgoggel.gamejass.domain.game.Card;
import io.mannsgoggel.gamejass.domain.game.GameState;

import static io.mannsgoggel.gamejass.domain.game.JassActions.*;

public class LocalGameMasterActor extends GameActor {
    public LocalGameMasterActor() {
        super("game-master");
    }

    @Override
    public void next(GameState state) {
        var players = state.queryPlayers();
        var nextAction = switch (state.getNextAction()) {
            case JOIN_PLAYER            -> none();
            case START_GAME             -> with(new StartGame(getName()));
            case START_ROUND            -> with(new StartRound(getName()));
            case HAND_OUT_CARDS         -> with(new HandOutCards(getName(), Card.CardDeckBuilder.buildAndShuffle(players)));
            case SET_STARTING_PLAYER    -> with(new SetStartingPlayer(getName(), players.get(0)));
            case DECIDE_SHIFT           -> none();
            case SET_PLAYING_MODE       -> none();
            case START_STICH            -> none();
            case PLAY_CARD              -> none();
            case END_STICH              -> with(new EndStich(getName()));
            case END_ROUND              -> with(new EndRound(getName()));
            case END_GAME               -> with(new EndGame(getName()));
        };

        setNextAction(nextAction);
    }
}
