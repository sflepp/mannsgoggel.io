package com.manoggeli.gamejass.domain.gameObjects;

import com.manoggeli.gamejass.domain.Team;
import com.manoggeli.gamejass.domain.action.Action;
import org.javatuples.Pair;

import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class GameState {
    private Action.ActionType nextAction;
    private String currentPlayer;
    private Boolean isShift;

    private List<Team> teams;
    private Stack<Pair<String, Card>> tableStack = new Stack<>();

    public GameState(Action.ActionType nextAction, String currentPlayer, List<Team> playersInPlayingOrder) {
        this.nextAction = nextAction;
        this.currentPlayer = currentPlayer;
        this.teams = playersInPlayingOrder;
    }

    public void setNextAction(Action.ActionType currentAction) {
        this.nextAction = currentAction;
    }

    public Boolean isShifted() {
        return isShift;
    }

    public void setShifted(Boolean shift) {
        isShift = shift;
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

    public Stack<Pair<String, Card>> getTableStack() {
        return tableStack;
    }

    public void setTableStack(Stack<Pair<String, Card>> tableStack) {
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

    public Action.ActionType getNextAction() {
        return nextAction;
    }
}
