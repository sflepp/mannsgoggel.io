package io.mannsgoggel.gamejass.domain.game.action;

import io.mannsgoggel.gamejass.domain.game.state.State;
import lombok.Data;

import java.util.List;

@Data
public class RequestRemoteAction {
    final Actions.ActionType action;
    final List<State.Card> handCards;
    final List<State.Card> playableCards;
    final List<State.Card> tableStack;
    final State state;
}
