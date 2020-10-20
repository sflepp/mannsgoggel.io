package io.mannsgoggel.gamejass.domain.game;

import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class Player {
    private final String name;
    private List<Card> handCards;

    public void removeHandCard(Card card) {
        handCards = handCards.stream()
                .filter(c -> !c.equals(card))
                .collect(Collectors.toList());
    }
}
