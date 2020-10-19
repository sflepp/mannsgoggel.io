package io.mannsgoggel.gamejass.domain.actors;

import io.mannsgoggel.gamejass.domain.PlayerStrategy;
import io.mannsgoggel.gamejass.domain.Store;
import io.mannsgoggel.gamejass.domain.game.GameState;

import java.util.List;

import static io.mannsgoggel.gamejass.domain.game.JassActions.*;

public class LocalPlayerActor extends GameActor {
    private final String name;
    private final PlayerStrategy strategy;

    public LocalPlayerActor(String name, PlayerStrategy strategy, Store store) {
        super(store);
        this.name = name;
        this.strategy = strategy;
    }

    @Override
    public void next(GameState state) {
        var handCards = List.copyOf(state.getPlayerByName(name).getHandCards());
        var tableStack = List.copyOf(state.getTableStackWithoutPlayer());
        var action = switch (state.getNextAction()) {
            case START_GAME             -> none();
            case START_ROUND            -> none();
            case HAND_OUT_CARDS         -> none();
            case SET_STARTING_PLAYER    -> none();
            case DECIDE_SHIFT           -> with(new DecideShift(name, strategy.decideShift(handCards, state)));
            case SET_PLAYING_MODE       -> with(new SetPlayingMode(name, strategy.choosePlayingMode(handCards, state)));
            case START_STICH            -> with(new StartStich(name, strategy.startStich(handCards, state)));
            case PLAY_CARD              -> with(new PlayCard(name, strategy.playCard(handCards, tableStack, state)));
            case END_STICH              -> none();
            case END_ROUND              -> none();
            case END_GAME               -> none();
        };

        setNextAction(action);
    }

    public void connect() {
        super.connect(name);
    }
}
