package com.manoggeli.gamejass.domain.gameObjects;

import com.manoggeli.gamejass.domain.Team;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class JassRules {

    public static boolean canPlayCard(Card card, Stack<Pair<String, Card>> tableStack, Set<Card> playerCards) {
        var cards = tableStack.stream().map(Pair::getValue1).collect(Collectors.toList());

        return cards.isEmpty()
                || card.getColor() == cards.get(0).getColor()
                || playerCards.stream().noneMatch(playerCard -> playerCard.getColor() == cards.get(0).getColor());
    }

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

    public static Pair<String, Card> winningCard(Stack<Pair<String, Card>> tableStack) {
        var highestPlayerCard = tableStack.get(0);
        var firstCardColor = tableStack.get(0).getValue1().getColor();

        for (var playerCard : tableStack) {
            var card = playerCard.getValue1();
            var highestCard = highestPlayerCard.getValue1();

           if ((card.getColor() == firstCardColor || card.isTrump()) && card.isHigherThan(highestCard)) {
               highestPlayerCard = playerCard;
           }
        }

        return highestPlayerCard;
    }

}
