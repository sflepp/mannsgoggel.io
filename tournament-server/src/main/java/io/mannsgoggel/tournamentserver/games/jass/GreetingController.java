package io.mannsgoggel.tournamentserver.games.jass;

import io.mannsgoggel.gamejass.domain.actors.LocalGameMasterActor;
import io.mannsgoggel.gamejass.domain.actors.LocalPlayerActor;
import io.mannsgoggel.gamejass.domain.game.JassGame;
import io.mannsgoggel.gamejass.strategy.RandomJassStrategy;
import io.mannsgoggel.tournamentserver.games.jass.clients.RemotePlayerActor;
import io.mannsgoggel.tournamentserver.games.jass.dto.HelloMessage;
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
public class GreetingController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private JassGame jassGame;
    private RemotePlayerActor player;

    public GreetingController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/jass/new-game")
    public void greeting(Principal principal, HelloMessage message) throws Exception {
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

    @Scheduled(fixedDelay = 500)
    public void scheduled() {
        if (jassGame != null) {
            jassGame.dispatchAllPlayers();
        }
    }

}