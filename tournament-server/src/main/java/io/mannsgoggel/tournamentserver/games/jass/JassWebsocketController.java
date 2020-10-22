package io.mannsgoggel.tournamentserver.games.jass;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.mannsgoggel.gamejass.domain.actors.LocalGameMasterActor;
import io.mannsgoggel.gamejass.domain.actors.LocalPlayerActor;
import io.mannsgoggel.gamejass.domain.game.JassGame;
import io.mannsgoggel.gamejass.strategy.RandomJassStrategy;
import io.mannsgoggel.tournamentserver.games.jass.clients.RemotePlayerActor;
import io.mannsgoggel.tournamentserver.games.jass.dto.HelloMessage;
import io.mannsgoggel.tournamentserver.games.jass.dto.RemoteAction;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@EnableScheduling
public class JassWebsocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private JassGame jassGame;
    private RemotePlayerActor player;

    public JassWebsocketController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/jass/new-game")
    public void greeting(Principal principal, HelloMessage message) {
        System.out.println(message + " " + principal.toString());

        player = new RemotePlayerActor(principal.getName(), simpMessagingTemplate);

        jassGame = new JassGame(
                new LocalGameMasterActor(),
                List.of(
                        new LocalPlayerActor(UUID.randomUUID().toString(), new RandomJassStrategy()),
                        new LocalPlayerActor(UUID.randomUUID().toString(), new RandomJassStrategy()),
                        new LocalPlayerActor(UUID.randomUUID().toString(), new RandomJassStrategy()),
                        player
                )
        );

        jassGame.start();
    }

    @MessageMapping("/jass/action")
    public void action(Principal principal, RemoteAction action) throws JsonProcessingException {
        player.next(action);
    }

    @Scheduled(fixedDelay = 100)
    public void scheduled() {
        if (jassGame != null) {
            jassGame.dispatchAllPlayers();
        }
    }

}