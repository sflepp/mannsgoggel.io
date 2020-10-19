package io.mannsgoggel.gamejass.domain;

import io.mannsgoggel.gamejass.domain.game.Card;
import io.mannsgoggel.gamejass.domain.game.GameMode;
import io.mannsgoggel.gamejass.domain.game.GameState;

import java.util.List;
import java.util.Set;

public interface PlayerStrategy {
    Boolean decideShift(Set<Card> handCards, GameState state);
    GameMode.PlayingMode choosePlayingMode(Set<Card> handCards, GameState state);
    Card startStich(Set<Card> handCards, GameState state);
    Card playCard(Set<Card> handCards, List<Card> tableStack, GameState state);
}
