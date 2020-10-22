package io.mannsgoggel.gamejass.domain.game;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toUnmodifiableList;

@Data
public class GameState {
    private ActionHistory history;
    private JassActions.ActionType nextAction;
    private String nextPlayer;
    private GameMode.PlayingMode playingMode;
    private Boolean shifted;
    private List<Team> teams;
    private List<CardState> cards;

    public GameState() {
        this.nextAction = JassActions.ActionType.JOIN_PLAYER;
        this.nextPlayer = "any";
        this.shifted = false;
        this.history = new ActionHistory();
        this.teams = new ArrayList<>();
        this.cards = Card.CardDeckBuilder.build()
                .stream()
                .map(card -> CardState.builder().card(card).build())
                .collect(toUnmodifiableList());
    }

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

    public GameState toPlayerView(String player) {
        GameState state = new GameState();
        state.setNextPlayer(nextPlayer);
        state.setTeams(teams);
        state.setNextAction(nextAction);
        state.setPlayingMode(playingMode);
        state.setShifted(shifted);
        state.setHistory(history.toPlayerView(player));

        state.setCards(
                getCards().stream()
                        .map(card -> card.toPlayerView(player))
                        .collect(toUnmodifiableList())
        );
        return state;
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

    public void transformTeam(Function<Team, Team> fn) {
        teams = teams.stream()
                .map(fn)
                .collect(toUnmodifiableList());
    }

    public void transformCardState(Function<CardState, CardState> fn) {
        cards = cards.stream()
                .map(fn)
                .collect(toUnmodifiableList());
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
}
