package io.mannsgoggel.gamejass.domain.game;

import io.mannsgoggel.gamejass.domain.Team;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class GameState {
    private JassActions.ActionType nextAction;
    private String currentPlayer;

    private GameMode gameMode;
    private Boolean shifted;

    private List<Team> teams;
    private List<Pair<String, Card>> tableStack = new ArrayList<>();

    public GameState(JassActions.ActionType nextAction, String currentPlayer, List<Team> playersInPlayingOrder) {
        this.nextAction = nextAction;
        this.currentPlayer = currentPlayer;
        this.teams = playersInPlayingOrder;
    }

    public void setNextAction(JassActions.ActionType currentAction) {
        this.nextAction = currentAction;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public Boolean getShifted() {
        return shifted;
    }

    public Boolean isShifted() {
        return shifted;
    }

    public void setShifted(Boolean shifted) {
        this.shifted = shifted;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public List<Pair<String, Card>> getTableStack() {
        return tableStack;
    }

    public List<Card> getTableStackWithoutPlayer() {
        return tableStack.stream().map(Pair::getValue1).collect(Collectors.toList());
    }

    public void setTableStack(List<Pair<String, Card>> tableStack) {
        this.tableStack = tableStack;
    }

    public boolean isStichFinished() {
        return tableStack.size() == 4;
    }

    public boolean isRoundFinished() {
        return getPlayers().stream()
                .mapToInt(player -> player.getHandCards().size())
                .sum() == 0;
    }


    public Player getPlayerByName(String playerName) {
        return teams.stream()
                .flatMap(team -> team.getPlayers().stream())
                .filter(player -> player.getName().equals(playerName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player with name " + playerName + " not found."));
    }

    public Player getTeamMateFor(String playerName) {
        return teams.stream()
                .filter(team -> team.containsPlayer(playerName))
                .map(team -> team.getTeamMate(playerName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player with name " + playerName + " not found."));
    }

    public List<Player> getPlayers() {
        return teams.stream()
                .flatMap(team -> team.getPlayers().stream())
                .collect(Collectors.toList());
    }

    public Team getTeamWith(String playerName) {
        return teams.stream()
                .filter(team ->
                        team.getPlayers().stream()
                                .anyMatch(player -> player.getName().equals(currentPlayer)))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player with name " + playerName + " not found."));
    }

    public String getPlayerNameForPlayedCard(Card card) {
        return tableStack.stream()
                .filter(c -> c.getValue1().equals(card))
                .map(Pair::getValue0)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Card not found."));
    }

    public JassActions.ActionType getNextAction() {
        return nextAction;
    }
}
