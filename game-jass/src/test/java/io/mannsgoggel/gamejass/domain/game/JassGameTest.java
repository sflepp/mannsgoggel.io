package io.mannsgoggel.gamejass.domain.game;

import io.mannsgoggel.gamejass.domain.player.LocalPlayer;
import io.mannsgoggel.gamejass.strategy.RandomJassStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.mannsgoggel.gamejass.domain.game.JassActions.ActionType.END_ROUND;
import static io.mannsgoggel.gamejass.domain.game.JassActions.ActionType.EXIT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class JassGameTest {

    @Test
    public void testJassGame_untilEndOfFirstRound() {
        var testee = new JassGame(
                List.of(
                        new LocalPlayer("player-1", new RandomJassStrategy()),
                        new LocalPlayer("player-2", new RandomJassStrategy()),
                        new LocalPlayer("player-3", new RandomJassStrategy()),
                        new LocalPlayer("player-4", new RandomJassStrategy())
                ));


        testee.start();

        var result = testee.getState$()
                .filter(state -> state.getNextAction().equals(END_ROUND))
                .blockFirst();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        var totalPoints = result.getTeams().stream()
                .mapToInt(Team::getPoints)
                .sum();

        assertThat(totalPoints, equalTo(157));
    }

    @Test
    public void testJassGame() {
        var testee = new JassGame(
                List.of(
                        new LocalPlayer("player-1", new RandomJassStrategy()),
                        new LocalPlayer("player-2", new RandomJassStrategy()),
                        new LocalPlayer("player-3", new RandomJassStrategy()),
                        new LocalPlayer("player-4", new RandomJassStrategy())
                ));

        testee.start();

        var totalPoints = testee.latestState().getTeams().stream()
                .mapToInt(Team::getPoints)
                .sum();

        var result = testee.getState$()
                .filter(state -> state.getNextAction().equals(EXIT))
                .blockFirst();

        assertThat(totalPoints % 157, equalTo(0));
    }

}