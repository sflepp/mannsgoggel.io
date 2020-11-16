package io.mannsgoggel.gamejass.domain.game.player;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mannsgoggel.gamejass.domain.game.GameModes;
import io.mannsgoggel.gamejass.domain.game.action.RemoteAction;
import io.mannsgoggel.gamejass.domain.game.action.RequestRemoteAction;
import io.mannsgoggel.gamejass.domain.game.state.State;
import io.mannsgoggel.gamejass.domain.game.strategy.RemotePlayerStrategy;

import static io.mannsgoggel.gamejass.domain.game.JassRules.playableCards;
import static io.mannsgoggel.gamejass.domain.game.action.Actions.*;
import static io.mannsgoggel.gamejass.domain.game.state.Selectors.*;

public class RemotePlayer extends Player {
    private final RemotePlayerStrategy strategy;

    public RemotePlayer(String name, RemotePlayerStrategy strategy) {
        super(name);
        this.strategy = strategy;
        this.strategy.registerOnRemoteAction(this::onRemoteAction);
    }

    @Override
    public void accept(State state) {
        this.strategy.nextState(playerView(state, getName()));

        if (isNotNextPlayer(state, getName())) {
            return;
        }

        var playingMode = state.getPlayingMode();
        var handCards = handCards(state, getName());
        var tableCards = tableStackCards(state);
        var playableCards = playableCards(playingMode, handCards, tableCards);

        this.strategy.requestRemoteAction(new RequestRemoteAction(
                state.getNextAction(),
                handCards,
                playableCards,
                tableCards,
                playerView(state, getName())
        ));
    }

    public Void onRemoteAction(RemoteAction remoteAction) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            switch (remoteAction.getActionType()) {
                case DECIDE_SHIFT -> getStore().dispatch(
                        new DecideShift(
                                getName(),
                                objectMapper.treeToValue(remoteAction.getPayload(), Boolean.class)
                        )
                );
                case SET_PLAYING_MODE -> getStore().dispatch(
                        new SetPlayingMode(
                                getName(),
                                objectMapper.treeToValue(remoteAction.getPayload(), GameModes.GameMode.PlayingMode.class)
                        )
                );
                case START_STICH -> getStore().dispatch(new StartStich(
                                getName(),
                                objectMapper.treeToValue(remoteAction.getPayload(), State.Card.class)
                        )
                );
                case PLAY_CARD -> getStore().dispatch(new PlayCard(
                                getName(),
                                objectMapper.treeToValue(remoteAction.getPayload(), State.Card.class)
                        )
                );
            }
        } catch (Exception e) {
            System.err.println(e);
        }

        return null;
    }
}
