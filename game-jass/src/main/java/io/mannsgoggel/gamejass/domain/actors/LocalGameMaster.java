package io.mannsgoggel.gamejass.domain.actors;

import io.mannsgoggel.gamejass.domain.game.Card;
import io.mannsgoggel.gamejass.domain.game.GameState;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.mannsgoggel.gamejass.domain.game.JassActions.*;

@Getter
@Setter
public class LocalGameMaster extends GameActor {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private Optional<ActionType> playUntil = Optional.empty();

    public LocalGameMaster() {
        super("game-master");
    }

    @Override
    public void next(GameState state) {
        var nextAction = state.getNextAction();
        var players = state.queryPlayers();

        if (playUntil.isPresent() && playUntil.get() == nextAction) {
            return;
        }

        switch (state.getNextAction()) {
            case START_GAME -> executor.submit(
                    () -> handler.dispatchAction(new StartGame(getName())));
            case START_ROUND -> executor.submit(
                    () -> handler.dispatchAction(new StartRound(getName())));
            case HAND_OUT_CARDS -> executor.submit(
                    () -> handler.dispatchAction(new HandOutCards(getName(), Card.CardDeckBuilder.buildAndShuffle(players))));
            case SET_STARTING_PLAYER -> executor.submit(
                    () -> handler.dispatchAction(new SetStartingPlayer(getName(), players.get(0))));
            case END_STICH -> executor.submit(
                    () -> handler.dispatchAction(new EndStich(getName())));
            case END_ROUND -> executor.submit(
                    () -> handler.dispatchAction(new EndRound(getName())));
            case END_GAME -> executor.submit(
                    () -> handler.dispatchAction(new EndGame(getName())));
        }
    }

    public void resume() {
        playUntil = Optional.empty();
        next(handler.getState());
    }
}
