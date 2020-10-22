package io.mannsgoggel.gamejass.domain.game;

import io.mannsgoggel.gamejass.domain.actors.GameActor;
import lombok.Data;

import java.util.List;

@Data
public class JassGame {
    private final GameActor gameMaster;
    private final List<GameActor> players;
    private final GameStateHandler gameStateHandler = new GameStateHandler();

    public void start() {
        gameMaster.setGameStateHandler(gameStateHandler);
        players.forEach(player -> player.setGameStateHandler(gameStateHandler));

        gameMaster.connect();
        players.forEach(GameActor::connect);

        players.forEach(player -> gameStateHandler.dispatchAction(
                new JassActions.JoinPlayer(player.getName())
        ));
    }

    public GameState playUntil(JassActions.ActionType actionType) {
        var actionHistory = gameStateHandler.getState().getHistory();

        while (!actionHistory.getLast().getAction().equals(actionType)) {
            dispatchAllPlayers();
        }

        return gameStateHandler.getState();
    }

    public void dispatchAllPlayers() {
        gameMaster.dispatchAction();
        players.forEach(GameActor::dispatchAction);
    }

    public GameResult play() {
        while (!gameStateHandler.getState().queryGameEnded()) {
            gameMaster.dispatchAction();
            players.forEach(GameActor::dispatchAction);
        }

        return new GameResult(gameStateHandler.getState().getTeams());
    }

}
