package io.mannsgoggel.gamejass.domain.game;

import lombok.Value;

import java.io.Serializable;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Value
public class CardHandout implements Serializable {
    List<CardState> cards;

    CardHandout toPlayerView(String player) {
        var newCardState = cards.stream()
                .map(cardState -> {
                    if (cardState.getPlayer() == null || cardState.getPlayer().equals(player)) {
                        return cardState;
                    } else {
                        return cardState.toBuilder().player("unknown").build();
                    }
                })
                .collect(toList());

        return new CardHandout(newCardState);
    }
}
