package io.mannsgoggel.tournamentserver.games.jass.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mannsgoggel.gamejass.domain.actors.GameActor;
import io.mannsgoggel.gamejass.domain.game.*;
import io.mannsgoggel.tournamentserver.games.jass.dto.RemoteAction;
import io.mannsgoggel.tournamentserver.games.jass.dto.RequestRemoteAction;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

import static io.mannsgoggel.gamejass.domain.game.JassRules.playableCards;

public class RemotePlayerActor extends GameActor {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public RemotePlayerActor(String name, SimpMessagingTemplate simpMessagingTemplate) {
        super(name);
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void next(GameState state) {
        this.simpMessagingTemplate.convertAndSend("/game/state", state.toPlayerView(getName()));

        var playingMode = state.getPlayingMode();
        var handCards = state.queryHandCards(getName());
        var tableCards = state.queryTableStackCards();
        var playableCards = playableCards(playingMode, handCards, tableCards);

        this.simpMessagingTemplate.convertAndSend("/game/request-action", new RequestRemoteAction(
                state.getNextAction(),
                handCards,
                playableCards,
                tableCards,
                state.toPlayerView(getName())
        ));
    }

    public void next(RemoteAction remoteAction) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        var action = switch (remoteAction.getActionType()) {
            case JOIN_PLAYER -> new JassActions.JoinPlayer(getName());
            case START_GAME -> new JassActions.StartGame(getName());
            case START_ROUND -> new JassActions.StartRound(getName());
            case HAND_OUT_CARDS -> new JassActions.HandOutCards(
                    getName(),
                    objectMapper.treeToValue(remoteAction.getPayload(), CardHandout.class)
            );
            case SET_STARTING_PLAYER -> new JassActions.SetStartingPlayer(
                    getName(),
                    objectMapper.treeToValue(remoteAction.getPayload(), String.class)
            );
            case DECIDE_SHIFT -> new JassActions.DecideShift(
                    getName(),
                    objectMapper.treeToValue(remoteAction.getPayload(), Boolean.class)
            );
            case SET_PLAYING_MODE -> new JassActions.SetPlayingMode(
                    getName(),
                    objectMapper.treeToValue(remoteAction.getPayload(), GameMode.PlayingMode.class)
            );
            case START_STICH -> new JassActions.StartStich(
                    getName(),
                    objectMapper.treeToValue(remoteAction.getPayload(), Card.class)
            );
            case PLAY_CARD -> new JassActions.PlayCard(
                    getName(),
                    objectMapper.treeToValue(remoteAction.getPayload(), Card.class)
            );
            case END_STICH -> new JassActions.EndStich(getName());
            case END_ROUND -> new JassActions.EndRound(getName());
            case END_GAME -> new JassActions.EndGame(getName());
        };

        setNextAction(Optional.of(action));
    }
}
