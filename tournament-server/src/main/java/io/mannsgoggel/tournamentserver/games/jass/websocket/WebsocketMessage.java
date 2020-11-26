package io.mannsgoggel.tournamentserver.games.jass.websocket;

import lombok.Value;

@Value
public class WebsocketMessage {
    String messageType;
    Object payload;
}
