package io.mannsgoggel.gamejass.domain;

import io.mannsgoggel.gamejass.domain.game.Card;
import io.mannsgoggel.gamejass.domain.game.GameMode;
import io.mannsgoggel.gamejass.domain.game.GameState;

import java.util.List;
import java.util.concurrent.Future;

public interface AsyncPlayerStrategy {
    Future<Boolean> decideShift(List<Card> handCards, GameState state);
    Future<GameMode.PlayingMode> choosePlayingMode(List<Card> handCards, GameState state);
    Future<Card> startStich(List<Card> handCards, GameState state);
    Future<Card> playCard(List<Card> handCards, List<Card> playableCards, List<Card> tableStack, GameState state);
}
