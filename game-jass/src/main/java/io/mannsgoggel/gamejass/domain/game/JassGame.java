package io.mannsgoggel.gamejass.domain.game;

import io.mannsgoggel.gamejass.domain.actors.GameActor;
import lombok.Data;

import java.util.List;

@Data
public class JassGame {
    private final List<GameActor> gameActors;
    private final GameStateHandler gameStateHandler = new GameStateHandler();

    public void start() {
        gameActors.forEach(actor -> actor.setHandler(gameStateHandler));
        gameActors.forEach(GameActor::connect);
    }
}
