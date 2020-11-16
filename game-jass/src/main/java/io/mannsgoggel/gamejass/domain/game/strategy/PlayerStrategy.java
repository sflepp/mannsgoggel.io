package io.mannsgoggel.gamejass.domain.game.strategy;

import io.mannsgoggel.gamejass.domain.game.GameModes;
import io.mannsgoggel.gamejass.domain.game.state.State;

import java.util.List;

import static io.mannsgoggel.gamejass.domain.game.state.State.*;

public interface PlayerStrategy {
    Boolean decideShift(List<Card> handCards, State state);
    GameModes.GameMode.PlayingMode choosePlayingMode(List<Card> handCards, State state);
    Card startStich(List<Card> handCards, State state);
    Card playCard(List<Card> handCards, List<Card> playableCards, List<Card> tableStack, State state);
}
