package io.mannsgoggel.gamejass.domain.actors;

import io.mannsgoggel.gamejass.domain.PlayerStrategy;
import io.mannsgoggel.gamejass.domain.game.GameState;

import java.util.List;

import static io.mannsgoggel.gamejass.domain.game.JassActions.*;

public class LocalPlayerActor extends GameActor {
    private final PlayerStrategy strategy;

    public LocalPlayerActor(String name, PlayerStrategy strategy) {
        super(name);
        this.strategy = strategy;
    }

    @Override
    public void next(GameState state) {
        var handCards = List.copyOf(state.queryPlayerByName(getName()).getHandCards());
        var tableStack = List.copyOf(state.getTableStack());
        var playerView = state.toPlayerView(getName());
        var action = switch (state.getNextAction()) {
            case START_GAME
                    -> none();
            case START_ROUND
                    -> none();
            case HAND_OUT_CARDS
                    -> none();
            case SET_STARTING_PLAYER
                    -> none();
            case DECIDE_SHIFT
                    -> with(new DecideShift(getName(), strategy.decideShift(handCards, playerView)));
            case SET_PLAYING_MODE
                    -> with(new SetPlayingMode(getName(), strategy.choosePlayingMode(handCards, playerView)));
            case START_STICH
                    -> with(new StartStich(getName(), strategy.startStich(handCards, playerView)));
            case PLAY_CARD
                    -> with(new PlayCard(getName(), strategy.playCard(handCards, tableStack, playerView)));
            case END_STICH
                    -> none();
            case END_ROUND
                    -> none();
            case END_GAME
                    -> none();
        };

        setNextAction(action);
    }
}
