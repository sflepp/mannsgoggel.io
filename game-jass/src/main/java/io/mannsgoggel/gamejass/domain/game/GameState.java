package io.mannsgoggel.gamejass.domain.game;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toUnmodifiableList;

@Data
@Builder(toBuilder = true)
public class GameState implements Cloneable {
    private final Integer revision;
    private final ActionHistory history;
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
                .filter(team ->
                        team.getPlayers().stream()
                                .anyMatch(player -> player.equals(playerName)))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player with name " + playerName + " not found."));
    }

    public static GameState toPlayerView(GameState state, String player) {
        return state.toBuilder()
                .history(state.history.toPlayerView(player))
                .cards(state.getCards().stream()
                        .map(card -> card.toPlayerView(player))
                        .collect(toUnmodifiableList()))
                .build();
    }

    public static GameState withTeams(List<String> players) {
        return GameState.builder()
                .revision(0)
                .nextAction(JassActions.ActionType.START_GAME)
                .history(new ActionHistory(Collections.emptyList()))
                .teams(List.of(
                        new Team("team-1",
                                new ArrayList<>(players.subList(0, 2)),
                                0
                        ),
                        new Team("team-1",
                                new ArrayList<>(players.subList(2, 4)),
                                0
                        )
                )).build();
    }

    public List<CardState> queryTableStack() {
        return cards.stream()
                .filter(CardState::queryIsOnTable)
                .collect(toUnmodifiableList());
    }

    public List<Card> queryTableStackCards() {
        return cards.stream()
                .filter(CardState::queryIsOnTable)
                .map(CardState::getCard)
                .collect(toUnmodifiableList());
    }

    public GameState transformTeam(Function<Team, Team> fn) {
        return toBuilder()
                .teams(
                        teams.stream()
                                .map(fn)
                                .collect(toUnmodifiableList())
                ).build();
    }

    public GameState transformCardState(Function<CardState, CardState> fn) {
        return toBuilder()
                .cards(cards.stream()
                        .map(fn)
                        .collect(toUnmodifiableList()))
                .build();
    }

    public CardState queryCardState(Card card) {
        return cards.stream()
                .filter(cardState -> cardState.getCard().equals(card))
                .findAny()
                .orElseThrow();
    }

    public List<Card> queryHandCards(String player) {
        return cards.stream()
                .filter(cardState -> cardState.queryIsOnPlayer() && cardState.getPlayer().equals(player))
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
