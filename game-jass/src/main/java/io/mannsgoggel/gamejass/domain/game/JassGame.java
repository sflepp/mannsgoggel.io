package io.mannsgoggel.gamejass.domain.game;

import io.mannsgoggel.gamejass.domain.action.Action;
import io.mannsgoggel.gamejass.domain.action.ActionNotAllowed;
import io.mannsgoggel.gamejass.domain.player.Player;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;

import java.util.List;
import java.util.Random;

import static io.mannsgoggel.gamejass.domain.CollectionShortcuts.map;

@Data
public class JassGame {
    private static final Logger LOGGER = LoggerFactory.getLogger(JassGame.class);
    private final List<Player> actors;
    private final ReplayProcessor<GameState> state$ = ReplayProcessor.create();
    private GameState currentState;

    public void start() {
        currentState = GameState.withTeams(map(actors, Player::getName));

        actors.forEach(state$::subscribe);
        actors.forEach(actor -> actor.setGame(this));

        state$.subscribe(this::gameAction);
        state$.onNext(currentState);
    }

    private void gameAction(GameState state) {
        if (state.getNextPlayer() != null) {
            return;
        }

        var nextAction = state.getNextAction();
        var players = state.queryPlayers();

        switch (nextAction) {
            case START_GAME -> dispatchAction(new JassActions.StartGame());
            case START_ROUND -> dispatchAction(new JassActions.StartRound());
            case HAND_OUT_CARDS -> dispatchAction(new JassActions.HandOutCards(Card.CardDeckBuilder.buildAndShuffle(players, state.getPlayingMode())));
            case SET_STARTING_PLAYER -> dispatchAction(new JassActions.SetStartingPlayer(players.get(new Random().nextInt(players.size()))));
            case END_STICH -> dispatchAction(new JassActions.EndStich());
            case END_ROUND -> dispatchAction(new JassActions.EndRound());
            case END_GAME -> dispatchAction(new JassActions.EndGame());
        }
    }

    public void dispatchAction(Action<?> action) {
        LOGGER.info(currentState.getRevision() + ": " + (action.getPlayer() == null ? "" : action.getPlayer() + " | ") + action.getAction() + (action.getPayload() == null ? "" : (" | " + action.getPayload())));

        if (!action.getAction().equals(currentState.getNextAction())) {
            throw new ActionNotAllowed(
                    "Expected next action " + currentState.getNextAction() + " but got " + action.getAction()
            );
        }

        if (currentState.isNotNextPlayer(action.getPlayer())) {
            throw new ActionNotAllowed(
                    "Expected next player " + currentState.getNextPlayer() + " but got " + action.getPlayer()
            );
        }

        var newState = action.build(currentState.clone())
                .revision(currentState.getRevision() + 1)
                .build();

        currentState = newState;
        state$.onNext(newState);
    }

    public GameState latestState() {
        return currentState;
    }

    public Flux<GameState> getState$() {
        return state$;
    }
}
