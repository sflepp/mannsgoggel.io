package com.manoggeli.gamejass.domain;

import com.manoggeli.gamejass.domain.action.Action;
import com.manoggeli.gamejass.domain.gameObjects.GameState;
import com.manoggeli.gamejass.domain.gameObjects.Player;

import java.util.List;

import static com.manoggeli.gamejass.domain.action.Action.ActionType.START_ROUND;


public class Reducer {
    public static GameState reduceAll(List<Action> actions) {
        GameState state = new GameState(
                START_ROUND,
                "game-master",
                List.of(
                        new Team(List.of(new Player("player-1"), new Player("player-2"))),
                        new Team(List.of(new Player("player-3"), new Player("player-4")))
                )
        );

        for (Action action : actions) {
            state = action.reduce(state);
        }

        return state;
    }
}
