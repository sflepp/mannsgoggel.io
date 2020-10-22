package io.mannsgoggel.gamejass.strategy;

import io.mannsgoggel.gamejass.domain.PlayerStrategy;
import io.mannsgoggel.gamejass.domain.game.Card;
import io.mannsgoggel.gamejass.domain.game.GameMode;
import io.mannsgoggel.gamejass.domain.game.GameState;

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
    public Card playCard(List<Card> handCards, List<Card> playableCards, List<Card> tableStack, GameState state) {
        return playableCards.get(new Random().nextInt(playableCards.size()));
    }
}
