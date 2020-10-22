package io.mannsgoggel.tournamentserver.games.jass.dto;

import io.mannsgoggel.gamejass.domain.game.JassActions;
import lombok.Data;

@Data
public class RemoteAction {
    private JassActions.ActionType actionType;
    private String payload;
}
