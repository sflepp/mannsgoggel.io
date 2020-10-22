package io.mannsgoggel.gamejass.domain.actors;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mannsgoggel.gamejass.domain.RemotePlayerStrategy;
import io.mannsgoggel.gamejass.domain.action.RemoteAction;
import io.mannsgoggel.gamejass.domain.action.RequestRemoteAction;
import io.mannsgoggel.gamejass.domain.game.Card;
import io.mannsgoggel.gamejass.domain.game.GameMode;
import io.mannsgoggel.gamejass.domain.game.GameState;
import io.mannsgoggel.gamejass.domain.game.JassActions;

import static io.mannsgoggel.gamejass.domain.game.JassRules.playableCards;

public class RemotePlayer extends GameActor {
    private final RemotePlayerStrategy strategy;

    public RemotePlayer(String name, RemotePlayerStrategy strategy) {
        super(name);
        this.strategy = strategy;
        this.strategy.registerOnRemoteAction(this::onRemoteAction);
    }

    @Override
    public void next(GameState state) {
        this.strategy.nextState(state.toPlayerView(getName()));

        var playingMode = state.getPlayingMode();
        var handCards = state.queryHandCards(getName());
        var tableCards = state.queryTableStackCards();
        var playableCards = playableCards(playingMode, handCards, tableCards);

        this.strategy.requestRemoteAction(new RequestRemoteAction(
                state.getNextAction(),
                handCards,
                playableCards,
                tableCards,
                state.toPlayerView(getName())
        ));
    }

    @Override
    public void connect() {
        super.connect();
        handler.dispatchAction(new JassActions.JoinPlayer(getName()));
    }

    public Void onRemoteAction(RemoteAction remoteAction) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            switch (remoteAction.getActionType()) {
                case DECIDE_SHIFT -> handler.dispatchAction(
                        new JassActions.DecideShift(
                                getName(),
                                objectMapper.treeToValue(remoteAction.getPayload(), Boolean.class)
                        )
                );
                case SET_PLAYING_MODE -> handler.dispatchAction(
                        new JassActions.SetPlayingMode(
                                getName(),
                                objectMapper.treeToValue(remoteAction.getPayload(), GameMode.PlayingMode.class)
                        )
                );
                case START_STICH -> handler.dispatchAction(new JassActions.StartStich(
                                getName(),
                                objectMapper.treeToValue(remoteAction.getPayload(), Card.class)
                        )
                );
                case PLAY_CARD -> handler.dispatchAction(new JassActions.PlayCard(
                                getName(),
                                objectMapper.treeToValue(remoteAction.getPayload(), Card.class)
                        )
                );
            }
        } catch (Exception e) {
            System.err.println(e);
        }

        return null;
    }
}
