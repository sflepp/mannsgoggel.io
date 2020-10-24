package io.mannsgoggel.gamejass.domain.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JassRules {
    public static String nextPlayer(String currentPlayer, List<Team> teams) {
        List<String> playerOrder = new ArrayList<>();
        playerOrder.add(teams.get(0).getPlayers().get(0));
        playerOrder.add(teams.get(1).getPlayers().get(0));
        playerOrder.add(teams.get(0).getPlayers().get(1));
        playerOrder.add(teams.get(1).getPlayers().get(1));

        var currentPlayerIndex = playerOrder.indexOf(currentPlayer);
        var nextPlayerIndex = (currentPlayerIndex + 1) % 4;

        return playerOrder.get(nextPlayerIndex);
    }

    public static Card winningCard(GameMode.PlayingMode playingMode, List<Card> tableStack) {
        return GameMode.Builder.build(playingMode)
                .winningCard(tableStack);
    }

    public static Integer tableStackPoints(GameMode.PlayingMode playingMode, List<Card> tableStack) {
        return tableStack.stream()
                .mapToInt(card -> cardPoints(playingMode, card))
                .sum();
    }

    public static Integer cardPoints(GameMode.PlayingMode playingMode, Card card) {
        return GameMode.Builder.build(playingMode)
                .getPoints(card);
    }

    public static boolean isTrump(GameMode.PlayingMode playingMode, Card card) {
        return GameMode.Builder.build(playingMode)
                .isTrump(card);
    }

    public static List<Card> playableCards(GameMode.PlayingMode playingMode, List<Card> handCards, List<Card> tableStack) {
        if (playingMode == null) {
            return Collections.emptyList();
        }
        return GameMode.Builder.build(playingMode)
                .playableCards(handCards, tableStack);
    }
}
