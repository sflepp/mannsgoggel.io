package io.mannsgoggel.tournamentserver.games.jass.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.mannsgoggel.gamejass.domain.game.JassActions;
import lombok.Data;

@Data
public class RemoteAction {
    private JassActions.ActionType actionType;
    private JsonNode payload;
}
