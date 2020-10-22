package io.mannsgoggel.tournamentserver.games.jass.dto;

import io.mannsgoggel.gamejass.domain.game.Card;
import io.mannsgoggel.gamejass.domain.game.GameState;
import io.mannsgoggel.gamejass.domain.game.JassActions;
import lombok.Data;

import java.util.List;

@Data
public class RequestRemoteAction {
    final JassActions.ActionType action;
    final List<Card> handCards;
    final List<Card> tableStack;
    final GameState gameState;
}
