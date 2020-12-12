package io.mannsgoggel.gamejass.domain.game.player;

import io.mannsgoggel.gamejass.domain.game.state.State;
import io.mannsgoggel.gamejass.domain.game.strategy.PlayerStrategy;

import static io.mannsgoggel.gamejass.domain.game.JassRules.playableCards;
import static io.mannsgoggel.gamejass.domain.game.action.Actions.*;
import static io.mannsgoggel.gamejass.domain.game.state.Selectors.*;

public class LocalPlayer extends Player {
    private final PlayerStrategy strategy;

    public LocalPlayer(String name, PlayerStrategy strategy) {
        super(name);
        this.strategy = strategy;
    }

    @Override
    public void accept(State state) {
        if (isNotNextPlayer(state, getName())) {
            return;
        }

        var tableStack = tableStackCards(state);
        var playerView = playerView(state, getName());

        switch (state.getNextAction()) {
            case DECIDE_SHIFT -> getStore().dispatch(
                    new DecideShift(getName(), strategy.decideShift(handCards(state, getName()), playerView))
            );

            case SET_PLAYING_MODE -> getStore().dispatch(
                    new SetPlayingMode(getName(), strategy.choosePlayingMode(handCards(state, getName()), playerView))
            );

            case PLAY_CARD -> getStore().dispatch(
                    new PlayCard(
                            getName(),
                            strategy.playCard(handCards(state, getName()), playableCards(state.getPlayingMode(), handCards(state, getName()), tableStack), tableStack, playerView)
                    )
            );
        }
    }

}
