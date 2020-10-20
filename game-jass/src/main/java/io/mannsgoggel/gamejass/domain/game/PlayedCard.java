package io.mannsgoggel.gamejass.domain.game;

import lombok.Value;

@Value
public class PlayedCard {
    String player;
    Card card;
}
