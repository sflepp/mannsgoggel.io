package io.mannsgoggel.gamejass.domain.player;

import io.mannsgoggel.gamejass.domain.game.GameState;

import static io.mannsgoggel.gamejass.domain.game.JassActions.*;
import static io.mannsgoggel.gamejass.domain.game.JassRules.playableCards;

public class LocalPlayer extends Player {
    private final PlayerStrategy strategy;

    public LocalPlayer(String name, PlayerStrategy strategy) {
        super(name);
        this.strategy = strategy;
    }

    @Override
    public void accept(GameState state) {
        if (state.isNotNextPlayer(getName())) {
            return;
        }

        var tableStack = state.queryTableStackCards();
        var playerView = GameState.toPlayerView(state, getName());

        switch (state.getNextAction()) {
            case DECIDE_SHIFT -> getGame().dispatchAction(
                    new DecideShift(getName(), strategy.decideShift(state.queryHandCards(getName()), playerView))
            );

            case SET_PLAYING_MODE -> getGame().dispatchAction(
                    new SetPlayingMode(getName(), strategy.choosePlayingMode(state.queryHandCards(getName()), playerView))
            );

            case START_STICH -> getGame().dispatchAction(
                    new StartStich(getName(), strategy.startStich(state.queryHandCards(getName()), playerView))
            );
            case PLAY_CARD -> getGame().dispatchAction(
                    new PlayCard(
                            getName(),
                            strategy.playCard(state.queryHandCards(getName()), playableCards(state.getPlayingMode(), state.queryHandCards(getName()), tableStack), tableStack, playerView)
                    )
            );
        }
    }

}
