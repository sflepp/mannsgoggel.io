package io.mannsgoggel.gamejass.domain;

import io.mannsgoggel.gamejass.domain.game.Card;
import io.mannsgoggel.gamejass.domain.game.GameMode;
import io.mannsgoggel.gamejass.domain.game.GameState;

import java.util.List;

public interface PlayerStrategy {
    Boolean decideShift(List<Card> handCards, GameState state);
    GameMode.PlayingMode choosePlayingMode(List<Card> handCards, GameState state);
    Card startStich(List<Card> handCards, GameState state);
    Card playCard(List<Card> handCards, List<Card> tableStack, GameState state);
}
