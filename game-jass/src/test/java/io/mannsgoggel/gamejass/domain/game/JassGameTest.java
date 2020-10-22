package io.mannsgoggel.gamejass.domain.game;

import io.mannsgoggel.gamejass.domain.actors.LocalGameMasterActor;
import io.mannsgoggel.gamejass.domain.actors.LocalPlayerActor;
import io.mannsgoggel.gamejass.strategy.RandomJassStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class JassGameTest {

    @Test
    public void testJassGame_untilEndOfFirstRound() {
        JassGame testee = new JassGame(
                new LocalGameMasterActor(),
                List.of(
                        new LocalPlayerActor("player-1", new RandomJassStrategy()),
                        new LocalPlayerActor("player-2", new RandomJassStrategy()),
                        new LocalPlayerActor("player-3", new RandomJassStrategy()),
                        new LocalPlayerActor("player-4", new RandomJassStrategy())
                ));

        testee.start();
        GameState result = testee.playUntil(JassActions.ActionType.END_ROUND);

        var totalPoints = result.getTeams().stream()
                .mapToInt(Team::getPoints)
                .sum();

        assertThat(totalPoints, equalTo(157));
    }

    @Test
    public void testJassGame() {
        JassGame testee = new JassGame(
                new LocalGameMasterActor(),
                List.of(
                        new LocalPlayerActor("player-1", new RandomJassStrategy()),
                        new LocalPlayerActor("player-2", new RandomJassStrategy()),
                        new LocalPlayerActor("player-3", new RandomJassStrategy()),
                        new LocalPlayerActor("player-4", new RandomJassStrategy())
                ));

        testee.start();
        GameResult result = testee.play();

        var totalPoints = result.getTeams().stream()
                .mapToInt(Team::getPoints)
                .sum();

        assertThat(totalPoints % 157, equalTo(0));
    }

}