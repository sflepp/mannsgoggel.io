package io.mannsgoggel.tournamentserver.games.jass;

import io.mannsgoggel.gamejass.domain.game.JassGame;
import io.mannsgoggel.gamejass.domain.game.action.RemoteAction;
import io.mannsgoggel.gamejass.domain.game.player.LocalPlayer;
import io.mannsgoggel.gamejass.domain.game.player.RemotePlayer;
import io.mannsgoggel.gamejass.domain.game.strategy.RandomJassStrategy;
import io.mannsgoggel.tournamentserver.games.jass.clients.WebsocketPlayerStrategy;
import io.mannsgoggel.tournamentserver.games.jass.dto.GameOptions;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@EnableScheduling
public class JassWebsocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final Map<String, RemotePlayer> players = new HashMap<>();

    public JassWebsocketController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/jass/new-game")
    public void greeting(Principal principal, GameOptions message) {
        RemotePlayer player = new RemotePlayer(principal.getName(), new WebsocketPlayerStrategy(principal.getName(), simpMessagingTemplate, message.getFilter()));

        players.put(principal.getName(), player);

        JassGame game = new JassGame(
                List.of(
                        player,
                        new LocalPlayer(UUID.randomUUID().toString(), new RandomJassStrategy()),
                        new LocalPlayer(UUID.randomUUID().toString(), new RandomJassStrategy()),
                        new LocalPlayer(UUID.randomUUID().toString(), new RandomJassStrategy())
                )
        );

        game.start();
    }

    @MessageMapping("/jass/action")
    public void action(Principal principal, RemoteAction action) {
        players.get(principal.getName()).onRemoteAction(action);
    }
}