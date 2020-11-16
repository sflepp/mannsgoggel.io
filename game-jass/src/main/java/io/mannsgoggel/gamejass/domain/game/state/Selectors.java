package io.mannsgoggel.gamejass.domain.game.state;

import java.util.List;

import static io.mannsgoggel.gamejass.domain.CollectionShortcuts.*;
import static java.util.stream.Collectors.toUnmodifiableList;

public class Selectors {
    public static boolean stichFinished(State state) {
        return tableStack(state).size() == 4;
    }

    public static boolean roundFinished(State state) {
        return state.getCards().stream().noneMatch(State.CardState::queryIsOnPlayer);
    }

    public static boolean gameEnded(State state) {
        return state.getTeams().stream().anyMatch(team -> team.getPoints() > 1500);
    }

    public static String teamMateFor(State state, String playerName) {
        return state.getTeams().stream()
                .filter(team -> team.getPlayers().stream()
                                .anyMatch(player -> player.equals(playerName)))
                .map(team -> team.getPlayers().stream()
                                .filter(player -> !player.equals(playerName))
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("Player " + playerName + " not found in team")))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player with name " + playerName + " not found."));
    }

    public static State.Team teamWith(State state, String playerName) {
        return state.getTeams().stream()
                .filter(t -> t.getPlayers().stream().anyMatch(player -> player.equals(playerName)))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player with name " + playerName + " not found."));
    }

    public static State playerView(State state, String player) {
        return state.toBuilder()
                .playerName(player)
                .cards(state.getCards() == null ? null : map(state.getCards(), c -> {
                    if (c.player == null || c.playOrder != null || c.player.equals(player)) {
                        return c;
                    } else {
                        return c.toBuilder().player("").build();
                    }
                }))
                .build();
    }

    public static List<State.CardState> playedCards(State state) {
        return filter(state.getCards(), c -> c.getPlayOrder() != null);
    }

    public static List<State.CardState> tableStack(State state) {
        return filter(state.getCards(), State.CardState::queryIsOnTable);
    }

    public static List<State.Card> tableStackCards(State state) {
        return state.getCards().stream()
                .filter(State.CardState::queryIsOnTable)
                .map(State.CardState::getCard)
                .collect(toUnmodifiableList());
    }

    public static State.CardState cardState(State state, State.Card card) {
        return any(state.getCards(), c -> c.getCard().equals(card));
    }

    public static List<State.Card> handCards(State state, String player) {
        return state.getCards().stream()
                .filter(c -> c.queryIsOnPlayer() && c.getPlayer().equals(player))
                .map(State.CardState::getCard)
                .collect(toUnmodifiableList());
    }

    public static List<String> players(State state) {
        return state.getTeams().stream()
                .flatMap(team -> team.getPlayers().stream())
                .collect(toUnmodifiableList());
    }

    public static boolean isNotNextPlayer(State state, String player) {
        return (state.getNextPlayer() != null || player != null) && (player == null || !player.equals(state.getNextPlayer()));
    }
}
