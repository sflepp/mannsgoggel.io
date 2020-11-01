package io.mannsgoggel.tournamentserver.games.jass.dto;

import lombok.Value;

@Value
public class WebsocketMessage {
    String messageType;
    Object payload;
}
