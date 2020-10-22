package io.mannsgoggel.gamejass.domain.game;

import io.mannsgoggel.gamejass.domain.actors.LocalGameMaster;
import io.mannsgoggel.gamejass.domain.actors.LocalPlayer;
import io.mannsgoggel.gamejass.strategy.RandomJassStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static io.mannsgoggel.gamejass.domain.game.JassActions.ActionType.EXIT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class JassGameTest {

    @Test
    public void testJassGame_untilEndOfFirstRound() throws InterruptedException {
        var gameMaster = new LocalGameMaster();
        gameMaster.setPlayUntil(Optional.of(JassActions.ActionType.END_ROUND));

        var testee = new JassGame(
                List.of(
                        gameMaster,
                        new LocalPlayer("player-1", new RandomJassStrategy()),
                        new LocalPlayer("player-2", new RandomJassStrategy()),
                        new LocalPlayer("player-3", new RandomJassStrategy()),
                        new LocalPlayer("player-4", new RandomJassStrategy())
                ));

        testee.start();

        while (testee.getGameStateHandler().getState().getNextAction() != JassActions.ActionType.END_ROUND) {
            Thread.sleep(10);
        }

        var totalPoints = testee.getGameStateHandler().getState().getTeams().stream()
                .mapToInt(Team::getPoints)
                .sum();

        assertThat(totalPoints, equalTo(157));
    }

    @Test
    public void testJassGame() throws InterruptedException {
        var testee = new JassGame(
                List.of(
                        new LocalGameMaster(),
                        new LocalPlayer("player-1", new RandomJassStrategy()),
                        new LocalPlayer("player-2", new RandomJassStrategy()),
                        new LocalPlayer("player-3", new RandomJassStrategy()),
                        new LocalPlayer("player-4", new RandomJassStrategy())
                ));

        testee.start();

        var totalPoints = testee.getGameStateHandler().getState().getTeams().stream()
                .mapToInt(Team::getPoints)
                .sum();

        while (testee.getGameStateHandler().getState().getNextAction() != EXIT) {
            Thread.sleep(10);
        }

        assertThat(totalPoints % 157, equalTo(0));
    }

}