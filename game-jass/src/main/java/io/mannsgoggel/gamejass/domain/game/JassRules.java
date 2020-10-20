package io.mannsgoggel.gamejass.domain.game;

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

    public static PlayedCard winningCard(GameMode.PlayingMode playingMode, List<PlayedCard> tableStack) {
        return GameMode.Builder.build(playingMode)
                .winningCard(tableStack);
    }

    public static Integer tableStackPoints(GameMode.PlayingMode playingMode, List<PlayedCard> tableStack) {
        return tableStack.stream()
                .map(PlayedCard::getCard)
                .mapToInt(card -> cardPoints(playingMode, card))
                .sum();
    }

    public static Integer cardPoints(GameMode.PlayingMode playingMode, Card card) {
        return GameMode.Builder.build(playingMode)
                .getPoints(card);
    }

    public static List<Card> playableCards(GameMode.PlayingMode playingMode, List<Card> handCards, List<PlayedCard> tableStack) {
        return GameMode.Builder.build(playingMode)
                .playableCards(handCards, tableStack);
    }

    public static Card higherCard(GameMode.PlayingMode playingMode, Card a, Card b) {
        return GameMode.Builder.build(playingMode)
                .higherCard(a, b);
    }
}
