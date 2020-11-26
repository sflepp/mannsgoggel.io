package io.mannsgoggel.tournamentserver.games.jass.websocket;

import io.mannsgoggel.gamejass.domain.game.action.RemoteAction;
import io.mannsgoggel.gamejass.domain.game.action.RequestRemoteAction;
import io.mannsgoggel.gamejass.domain.game.state.State;
import io.mannsgoggel.gamejass.domain.game.action.Actions;
import io.mannsgoggel.gamejass.domain.game.strategy.RemotePlayerStrategy;
import io.mannsgoggel.tournamentserver.games.jass.dto.GameOptions;
import lombok.Data;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.function.Function;

@Data
public class WebsocketPlayerStrategy implements RemotePlayerStrategy {
    private final String userName;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameOptions.FilterType filter;
    private Function<RemoteAction, Void> onRemoteAction;

    @Override
    public void nextState(State state) {

        boolean shouldEmit = switch (filter) {
            case ALL:
                yield true;
            case PLAYER_ONLY:
                yield state.getPlayerName().equals(state.getNextPlayer()) || state.getNextAction().equals(Actions.ActionType.EXIT);
        };

        if (shouldEmit) {
            simpMessagingTemplate.convertAndSendToUser(userName, "/game", new WebsocketMessage("state", state));
        }
    }

    @Override
    public void requestRemoteAction(RequestRemoteAction action) {
        simpMessagingTemplate.convertAndSendToUser(userName, "/game", new WebsocketMessage("action-request", action));

    }

    public void onRemoteAction(RemoteAction action) {
        onRemoteAction.apply(action);
    }

    @Override
    public void registerOnRemoteAction(Function<RemoteAction, Void> task) {
        this.onRemoteAction = task;
    }
}
