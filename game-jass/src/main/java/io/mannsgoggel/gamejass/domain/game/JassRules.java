package io.mannsgoggel.gamejass.domain.game;

import io.mannsgoggel.gamejass.domain.game.state.State;

import java.util.ArrayList;
import java.util.List;

import static io.mannsgoggel.gamejass.domain.game.GameModes.Builder.build;
import static io.mannsgoggel.gamejass.domain.game.GameModes.GameMode.*;
import static java.util.Collections.emptyList;

public class JassRules {
    public static String nextPlayer(String currentPlayer, List<State.Team> teams) {
        List<String> playerOrder = new ArrayList<>();
        playerOrder.add(teams.get(0).getPlayers().get(0));
        playerOrder.add(teams.get(1).getPlayers().get(0));
        playerOrder.add(teams.get(0).getPlayers().get(1));
        playerOrder.add(teams.get(1).getPlayers().get(1));

        var currentPlayerIndex = playerOrder.indexOf(currentPlayer);
        var nextPlayerIndex = (currentPlayerIndex + 1) % 4;

        return playerOrder.get(nextPlayerIndex);
    }

    public static State.Card winningCard(PlayingMode playingMode, List<State.Card> tableStack) {
        return build(playingMode).winningCard(tableStack);
    }

    public static Integer tableStackPoints(PlayingMode playingMode, List<State.Card> tableStack) {
        return tableStack.stream()
                .mapToInt(card -> cardPoints(playingMode, card))
                .sum();
    }

    public static Integer cardPoints(PlayingMode playingMode, State.Card card) {
        return build(playingMode).getPoints(card);
    }

    public static boolean isTrump(PlayingMode playingMode, State.Card card) {
        return build(playingMode).isTrump(card);
    }

    public static List<State.Card> playableCards(PlayingMode playingMode, List<State.Card> handCards, List<State.Card> tableStack) {
        if (playingMode == null) {
            return emptyList();
        }
        return build(playingMode).playableCards(handCards, tableStack);
    }
}
