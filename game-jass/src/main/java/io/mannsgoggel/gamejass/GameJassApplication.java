package io.mannsgoggel.gamejass;

import io.mannsgoggel.gamejass.domain.actors.LocalGameMasterActor;
import io.mannsgoggel.gamejass.domain.actors.LocalPlayerActor;
import io.mannsgoggel.gamejass.domain.game.GameResult;
import io.mannsgoggel.gamejass.domain.game.JassGame;
import io.mannsgoggel.gamejass.strategy.RandomJassStrategy;

import java.util.List;

public class GameJassApplication {

    public static void main(String[] args) {
        JassGame game = new JassGame(
                new LocalGameMasterActor(),
                List.of(
                        new LocalPlayerActor("player-1", new RandomJassStrategy()),
                        new LocalPlayerActor("player-2", new RandomJassStrategy()),
                        new LocalPlayerActor("player-3", new RandomJassStrategy()),
                        new LocalPlayerActor("player-4", new RandomJassStrategy())
                ));

        GameResult result = game.play();

        System.out.println(result);
    }
}
