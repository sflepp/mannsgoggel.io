package io.mannsgoggel.gamejass.domain.game;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
public class GameState {
    private JassActions.ActionType nextAction;
    private String nextPlayer;

    private GameMode.PlayingMode playingMode;
    private Boolean shifted;
    private Boolean gameEnded = false;

    private List<Team> teams;

    private List<PlayedCard> tableStack = new ArrayList<>();

    public boolean isStichFinished() {
        return tableStack.size() == 4;
    }

    public boolean isRoundFinished() {
        return queryPlayers().stream()
                .mapToInt(player -> player.getHandCards().size())
                .sum() == 0;
    }

    public Player queryPlayerByName(String playerName) {
        return teams.stream()
                .flatMap(team -> team.getPlayers().stream())
                .filter(player -> player.getName().equals(playerName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player with name " + playerName + " not found."));
    }

    public Player queryTeamMateFor(String playerName) {
        return teams.stream()
                .filter(team -> team.containsPlayer(playerName))
                .map(team -> team.getTeamMate(playerName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player with name " + playerName + " not found."));
    }

    public List<Player> queryPlayers() {
        return teams.stream()
                .flatMap(team -> team.getPlayers().stream())
                .collect(toList());
    }

    public Team queryTeamWith(String playerName) {
        return teams.stream()
                .filter(team ->
                        team.getPlayers().stream()
                                .anyMatch(player -> player.getName().equals(playerName)))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player with name " + playerName + " not found."));
    }
}
