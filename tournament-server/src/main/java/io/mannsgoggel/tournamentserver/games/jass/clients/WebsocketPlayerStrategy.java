package io.mannsgoggel.tournamentserver.games.jass.clients;

import io.mannsgoggel.gamejass.domain.action.RemoteAction;
import io.mannsgoggel.gamejass.domain.action.RequestRemoteAction;
import io.mannsgoggel.gamejass.domain.game.GameState;
import io.mannsgoggel.gamejass.domain.player.RemotePlayerStrategy;
import io.mannsgoggel.tournamentserver.games.jass.dto.WebsocketMessage;
import lombok.Data;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.function.Function;

@Data
public class WebsocketPlayerStrategy implements RemotePlayerStrategy {
    private final String userName;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private Function<RemoteAction, Void> onRemoteAction;


    @Override
    public void nextState(GameState gameState) {
        simpMessagingTemplate.convertAndSendToUser(userName, "/game", new WebsocketMessage("state", gameState));
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
