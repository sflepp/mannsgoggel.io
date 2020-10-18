package io.mannsgoggel.gamejass.domain.game;

import java.util.HashSet;
import java.util.Set;

public class CardDeckBuilder {

    public static Set<Card> buildWithoutTrump() {
        Set<Card> cards = new HashSet<>();

        Set.of(Card.Color.values()).forEach(color -> {
            Set.of(Card.Suit.values()).forEach(suit -> {
                cards.add(new Card(color, suit));
            });
        });

        return cards;
    }
}
