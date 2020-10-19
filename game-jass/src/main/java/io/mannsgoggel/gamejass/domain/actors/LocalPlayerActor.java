package io.mannsgoggel.gamejass.domain.actors;

import io.mannsgoggel.gamejass.domain.PlayerStrategy;
import io.mannsgoggel.gamejass.domain.Store;
import io.mannsgoggel.gamejass.domain.game.GameState;
import io.mannsgoggel.gamejass.domain.game.JassActions;

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
        var handCards = state.getPlayerByName(name).getHandCards();
        var tableStack = state.getTableStackWithoutPlayer();

        getStore().dispatchAction(
                switch (state.getNextAction()) {
                    case START_ROUND            -> null;
                    case HAND_OUT_CARDS         -> null;
                    case SET_STARTING_PLAYER    -> null;
                    case DECIDE_SHIFT           -> new JassActions.DecideShift(name, strategy.decideShift(handCards, state));
                    case SET_PLAYING_MODE       -> new JassActions.SetPlayingMode(name, strategy.choosePlayingMode(handCards, state));
                    case START_STICH            -> new JassActions.StartStich(name, strategy.startStich(handCards, state));
                    case PLAY_CARD              -> new JassActions.PlayCard(name, strategy.playCard(handCards, tableStack, state));
                    case END_STICH              -> null;
                    case END_ROUND              -> null;
                    case END_GAME               -> null;
                }
        );
    }
}
