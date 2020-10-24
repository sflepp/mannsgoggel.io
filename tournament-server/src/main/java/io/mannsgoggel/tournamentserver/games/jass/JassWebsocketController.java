package io.mannsgoggel.tournamentserver.games.jass;

import io.mannsgoggel.gamejass.domain.action.RemoteAction;
import io.mannsgoggel.gamejass.domain.player.LocalGameMaster;
import io.mannsgoggel.gamejass.domain.player.LocalPlayer;
import io.mannsgoggel.gamejass.domain.player.RemotePlayer;
import io.mannsgoggel.gamejass.domain.game.JassGame;
import io.mannsgoggel.gamejass.strategy.RandomJassStrategy;
import io.mannsgoggel.tournamentserver.games.jass.clients.WebsocketPlayerStrategy;
import io.mannsgoggel.tournamentserver.games.jass.dto.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@EnableScheduling
public class JassWebsocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private JassGame jassGame;
    private RemotePlayer player;

    public JassWebsocketController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/jass/new-game")
    public void greeting(Principal principal, HelloMessage message) {
        System.out.println(message + " " + principal.toString());

        player = new RemotePlayer(principal.getName(), new WebsocketPlayerStrategy(simpMessagingTemplate));

        jassGame = new JassGame(
                List.of(
                        new LocalGameMaster(),
                        new LocalPlayer(UUID.randomUUID().toString(), new RandomJassStrategy()),
                        new LocalPlayer(UUID.randomUUID().toString(), new RandomJassStrategy()),
                        new LocalPlayer(UUID.randomUUID().toString(), new RandomJassStrategy()),
                        player
                )
        );

        jassGame.start();
    }

    @MessageMapping("/jass/action")
    public void action(Principal principal, RemoteAction action) {
        player.onRemoteAction(action);
    }
}