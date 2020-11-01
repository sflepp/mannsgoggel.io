package io.mannsgoggel.gamejass.domain.game;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static io.mannsgoggel.gamejass.domain.CollectionShortcuts.*;
import static java.util.stream.Collectors.toUnmodifiableList;

@Data
@Builder(toBuilder = true)
public class GameState implements Cloneable {
    private final String playerName;
    private final Integer revision;
    private final JassActions.ActionType nextAction;
    private final String nextPlayer;
    private final GameMode.PlayingMode playingMode;
    private final Boolean shifted;
    private final List<Team> teams;
    private final List<CardState> cards;

    public boolean queryStichFinished() {
        return queryTableStack().size() == 4;
    }

    public boolean queryRoundFinished() {
        return cards.stream().noneMatch(CardState::queryIsOnPlayer);
    }

    public boolean queryGameEnded() {
        return teams.stream().anyMatch(team -> team.getPoints() > 1500);
    }

    public String queryPlayerByName(String playerName) {
        return teams.stream()
                .flatMap(team -> team.getPlayers().stream())
                .filter(player -> player.equals(playerName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player with name " + playerName + " not found."));
    }

    public String queryTeamMateFor(String playerName) {
        return teams.stream()
                .filter(team -> team.containsPlayer(playerName))
                .map(team -> team.getTeamMate(playerName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player with name " + playerName + " not found."));
    }

    public Team queryTeamWith(String playerName) {
        return teams.stream()
                .filter(t -> t.getPlayers().stream().anyMatch(player -> player.equals(playerName)))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player with name " + playerName + " not found."));
    }

    public static GameState toPlayerView(GameState state, String player) {
        return state.toBuilder()
                .playerName(player)
                .cards(state.getCards() == null ? null : map(state.getCards(), c -> c.toPlayerView(player)))
                .build();
    }

    public static GameState withTeams(List<String> players) {
        return GameState.builder()
                .revision(0)
                .nextAction(JassActions.ActionType.START_GAME)
                .cards(Card.CardDeckBuilder.buildInitial())
                .teams(List.of(
                        new Team("team-1", new ArrayList<>(players.subList(0, 2)), 0),
                        new Team("team-2", new ArrayList<>(players.subList(2, 4)), 0)
                )).build();
    }

    public List<CardState> queryPlayedCards() {
        return filter(cards, c -> c.getPlayOrder() != null);
    }

    public List<CardState> queryTableStack() {
        return filter(cards, CardState::queryIsOnTable);
    }

    public List<Card> queryTableStackCards() {
        return cards.stream()
                .filter(CardState::queryIsOnTable)
                .map(CardState::getCard)
                .collect(toUnmodifiableList());
    }

    public GameState team(Function<Team, Team> fn) {
        return toBuilder().teams(map(teams, fn)).build();
    }

    public GameState cardState(Function<CardState, CardState> fn) {
        return toBuilder().cards(map(cards, fn)).build();
    }

    public CardState queryCardState(Card card) {
        return any(cards, c -> c.getCard().equals(card));
    }

    public List<Card> queryHandCards(String player) {
        return cards.stream()
                .filter(c -> c.queryIsOnPlayer() && c.getPlayer().equals(player))
                .map(CardState::getCard)
                .collect(toUnmodifiableList());
    }

    public List<String> queryPlayers() {
        return teams.stream()
                .flatMap(team -> team.getPlayers().stream())
                .collect(toUnmodifiableList());
    }

    public boolean isNotNextPlayer(String player) {
        return (nextPlayer != null || player != null) && (player == null || !player.equals(nextPlayer));
    }

    @Override
    protected GameState clone() {
        return toBuilder().build();
    }
}
