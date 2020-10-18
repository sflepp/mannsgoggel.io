package com.manoggeli.gamejass.domain;

import com.manoggeli.gamejass.domain.gameObjects.Card;
import com.manoggeli.gamejass.domain.gameObjects.Player;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Team {
    private List<Player> players;
    private List<Pair<String, Card>> obtainedCards = new ArrayList<>();

    public Team(List<Player> players) {
        this.players = players;
    }

    public List<Player> getPlayers() {
        return players;
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

    public void obtainCards(Collection<Pair<String, Card>> cards) {
        obtainedCards.addAll(cards);
    }

    public Integer getPoints() {
        return obtainedCards
                .stream()
                .map(card -> card.getValue1().getPoints())
                .reduce(0, Integer::sum);
    }

    public boolean hasWon() {
        return getPoints() >= 1500;
    }
}
