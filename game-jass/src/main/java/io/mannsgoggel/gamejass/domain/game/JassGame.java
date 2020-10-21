package io.mannsgoggel.gamejass.domain.game;

import io.mannsgoggel.gamejass.domain.actors.GameActor;
import lombok.Data;

import java.util.List;

@Data
public class JassGame {
    private final GameActor gameMaster;
    private final List<GameActor> players;
    private GameStateHandler gameStateHandler;

    public void start() {
        gameStateHandler = new GameStateHandler();

        gameMaster.setGameStateHandler(gameStateHandler);
        players.forEach(player -> player.setGameStateHandler(gameStateHandler));

        gameMaster.connect();
        players.forEach(GameActor::connect);

        gameStateHandler.dispatchAction(new JassActions.StartGame());
    }

    public GameState playUntil(JassActions.ActionType actionType) {
        var actionHistory = gameStateHandler.getState().getHistory();

        while (!actionHistory.getLast().getAction().equals(actionType)) {
            gameMaster.dispatchAction();
            players.forEach(GameActor::dispatchAction);
        }

        return gameStateHandler.getState();
    }

    public GameResult play() {
        while (!gameStateHandler.getState().getGameEnded()) {
            gameMaster.dispatchAction();
            players.forEach(GameActor::dispatchAction);
        }

        return new GameResult(gameStateHandler.getState().getTeams());
    }

}
