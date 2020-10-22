package io.mannsgoggel.gamejass.strategy;

import io.mannsgoggel.gamejass.domain.PlayerStrategy;
import io.mannsgoggel.gamejass.domain.game.*;

import java.util.List;
import java.util.Random;

public class RandomJassStrategy implements PlayerStrategy {
    @Override
    public Boolean decideShift(List<Card> handCards, GameState state) {
        return false;
    }

    @Override
    public GameMode.PlayingMode choosePlayingMode(List<Card> handCards, GameState state) {
        return GameMode.PlayingMode.values()[new Random().nextInt(GameMode.PlayingMode.values().length)];
    }

    @Override
    public Card startStich(List<Card> handCards, GameState state) {
        return handCards.get(new Random().nextInt(handCards.size()));
    }

    @Override
    public Card playCard(List<Card> handCards, List<Card> tableStack, GameState state) {
        var playableCards = JassRules.playableCards(state.getPlayingMode(), handCards, tableStack);
        return playableCards.get(new Random().nextInt(playableCards.size()));
    }
}
