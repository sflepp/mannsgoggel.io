package io.mannsgoggel.gamejass.domain.game;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class Team {
    String name;
    List<String> players;
    Integer points;

    public boolean containsPlayer(String playerName) {
        return players.stream()
                .anyMatch(player -> player.equals(playerName));
    }

    public String getTeamMate(String playerName) {
        return players.stream()
                .filter(player -> !player.equals(playerName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player " + playerName + " not found in team"));
    }

    @Override
    protected Team clone() throws CloneNotSupportedException {
        return (Team) super.clone();
    }
}
