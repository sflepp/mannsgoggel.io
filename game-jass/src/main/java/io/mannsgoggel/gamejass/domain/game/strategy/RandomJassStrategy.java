package io.mannsgoggel.gamejass.domain.game.strategy;

import io.mannsgoggel.gamejass.domain.game.GameModes;
import io.mannsgoggel.gamejass.domain.game.state.State;

import java.util.List;
import java.util.Random;

public class RandomJassStrategy implements PlayerStrategy {
    @Override
    public Boolean decideShift(List<State.Card> handCards, State state) {
        return false;
    }

    @Override
    public GameModes.GameMode.PlayingMode choosePlayingMode(List<State.Card> handCards, State state) {
        return GameModes.GameMode.PlayingMode.values()[new Random().nextInt(GameModes.GameMode.PlayingMode.values().length)];
    }

    @Override
    public State.Card startStich(List<State.Card> handCards, State state) {
        return handCards.get(new Random().nextInt(handCards.size()));
    }

    @Override
    public State.Card playCard(List<State.Card> handCards, List<State.Card> playableCards, List<State.Card> tableStack, State state) {
        return playableCards.get(new Random().nextInt(playableCards.size()));
    }
}
