package io.mannsgoggel.gamejass.domain.actors;

import io.mannsgoggel.gamejass.domain.PlayerStrategy;
import io.mannsgoggel.gamejass.domain.game.GameState;
import io.mannsgoggel.gamejass.domain.game.JassActions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.mannsgoggel.gamejass.domain.game.JassActions.*;
import static io.mannsgoggel.gamejass.domain.game.JassRules.playableCards;

public class LocalPlayer extends GameActor {
    private final PlayerStrategy strategy;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public LocalPlayer(String name, PlayerStrategy strategy) {
        super(name);
        this.strategy = strategy;
    }

    @Override
    public void connect() {
        super.connect();
        handler.dispatchAction(new JassActions.JoinPlayer(getName()));
    }

    @Override
    public void next(GameState state) {
        var handCards = state.queryHandCards(getName());
        var tableStack = state.queryTableStackCards();
        var playerView = state.toPlayerView(getName());

        switch (state.getNextAction()) {
            case DECIDE_SHIFT -> executor.submit(
                    () -> handler.dispatchAction(
                            new DecideShift(getName(), strategy.decideShift(handCards, playerView))
                    )
            );
            case SET_PLAYING_MODE -> executor.submit(
                    () -> handler.dispatchAction(
                            new SetPlayingMode(getName(), strategy.choosePlayingMode(handCards, playerView))
                    )
            );
            case START_STICH -> executor.submit(
                    () -> handler.dispatchAction(
                            new StartStich(getName(), strategy.startStich(handCards, playerView))
                    )
            );
            case PLAY_CARD -> executor.submit(
                    () -> handler.dispatchAction(
                            new PlayCard(
                                    getName(),
                                    strategy.playCard(handCards, playableCards(state.getPlayingMode(), handCards, tableStack), tableStack, playerView)
                            )
                    )
            );
        }
    }

}
