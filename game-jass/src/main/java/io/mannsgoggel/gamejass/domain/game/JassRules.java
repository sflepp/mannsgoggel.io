package com.manoggeli.gamejass.domain.game;

import com.manoggeli.gamejass.domain.Team;

import java.util.ArrayList;
import java.util.List;

public class JassRules {

    public static Player nextPlayer(Player currentPlayer, List<Team> teams) {
        List<Player> playerOrder = new ArrayList<>();
        playerOrder.add(teams.get(0).getPlayers().get(0));
        playerOrder.add(teams.get(1).getPlayers().get(0));
        playerOrder.add(teams.get(0).getPlayers().get(1));
        playerOrder.add(teams.get(1).getPlayers().get(1));

        var currentPlayerIndex = playerOrder.indexOf(currentPlayer);
        var nextPlayerIndex = (currentPlayerIndex + 1) % 4;

        return playerOrder.get(nextPlayerIndex);
    }
}
