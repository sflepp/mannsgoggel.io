package io.mannsgoggel.gamejass.domain.game;

import io.mannsgoggel.gamejass.domain.game.player.Player;
import io.mannsgoggel.gamejass.domain.game.state.CardDeck;
import io.mannsgoggel.gamejass.domain.game.state.State;

import java.util.List;
import java.util.function.Consumer;

import static io.mannsgoggel.gamejass.domain.CollectionShortcuts.map;
import static io.mannsgoggel.gamejass.domain.game.action.Actions.*;
import static io.mannsgoggel.gamejass.domain.game.state.Selectors.players;

public class JassGame implements Consumer<State> {
    private final Store store;
    private final List<Player> players;

    public JassGame(List<Player> players) {
        this.store = new Store();
        this.store.subscribe(this);

        this.players = players;
        this.players.forEach(player -> {
            player.setStore(store);
            store.subscribe(player);
        });
    }

    public Store getStore() {
        return store;
    }

    public void start() {
        var playerNames = map(players, Player::getName);

        var teams = List.of(
                State.Team.builder().name("team-1").players(playerNames.subList(0, 2)).points(0).build(),
                State.Team.builder().name("team-2").players(playerNames.subList(2, 4)).points(0).build()
        );

        store.dispatch(new StartGame(teams));
    }

    @Override
    public void accept(State state) {
        if (state.getNextPlayer() != null) {
            return;
        }

        var nextAction = state.getNextAction();
        var players = players(state);

        switch (nextAction) {
            case START_GAME -> {}
            case START_ROUND -> store.dispatch(new StartRound());
            case HAND_OUT_CARDS -> {
                store.dispatch(new HandOutCards(CardDeck.buildAndShuffle(players, state.getPlayingMode())));
            }
            case SET_STARTING_PLAYER -> {
                var startingPlayer = state.getCards().stream()
                        .filter(c -> c.getCard().equals(new State.Card(State.Card.Color.SPADES, State.Card.Suit.TEN)))
                        .findAny()
                        .orElseThrow()
                        .getPlayer();

                store.dispatch(new SetStartingPlayer(startingPlayer));
            }
            case END_STICH -> store.dispatch(new EndStich());
            case END_ROUND -> store.dispatch(new EndRound());
            case END_GAME -> store.dispatch(new EndGame());
        }
    }
}
