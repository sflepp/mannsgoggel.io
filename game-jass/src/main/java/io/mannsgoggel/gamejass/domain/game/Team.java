package io.mannsgoggel.gamejass.domain.game;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class Team {
    private final List<Player> players;
    private final List<PlayedCard> obtainedCards = new ArrayList<>();
    private Integer points = 0;

    public void addPoints(Integer points) {
        this.points += points;
    }

    public boolean containsPlayer(String playerName) {
        return players.stream()
                .anyMatch(player -> player.getName().equals(playerName));
    }

    public Player getTeamMate(String playerName) {
        return players.stream()
                .filter(player -> !player.getName().equals(playerName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player " + playerName + " not found in team"));
    }
}
