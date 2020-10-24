package io.mannsgoggel.gamejass.domain.game;

import lombok.Value;

import java.util.List;

import static io.mannsgoggel.gamejass.domain.CollectionShortcuts.map;

@Value
public class CardHandout {
    List<CardState> cards;

    CardHandout toPlayerView(String player) {
        return new CardHandout(
                map(cards, cardState -> cardState.getPlayer() == null || cardState.getPlayer().equals(player) ?
                        cardState :
                        cardState.toBuilder().player("").build()
                )
        );
    }
}
