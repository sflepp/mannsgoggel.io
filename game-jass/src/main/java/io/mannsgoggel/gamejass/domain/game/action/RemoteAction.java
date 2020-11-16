package io.mannsgoggel.gamejass.domain.game.action;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class RemoteAction {
    private Actions.ActionType actionType;
    private JsonNode payload;
}
