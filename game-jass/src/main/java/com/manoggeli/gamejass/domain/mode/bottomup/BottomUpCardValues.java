package com.manoggeli.gamejass.domain.mode.bottomup;

import com.manoggeli.gamejass.domain.gameObjects.Card;
import com.manoggeli.gamejass.domain.gameObjects.CardValues;
import org.javatuples.Pair;

import java.util.List;

import static com.manoggeli.gamejass.domain.gameObjects.Card.Suit.*;

public class BottomUpCardValues implements CardValues {

    private static final List<Pair<Card.Suit, Integer>> VALUES = List.of(
            Pair.with(ACE, 11),
            Pair.with(KING, 4),
            Pair.with(QUEEN, 3),
            Pair.with(JACK, 2),
            Pair.with(TEN, 10),
            Pair.with(NINE, 0),
            Pair.with(EIGHT, 8),
            Pair.with(SEVEN, 0),
            Pair.with(SIX, 0)
    );

    private Pair<Card.Suit, Integer> getPairFor(Card card) {
        return VALUES.stream()
                .filter(p -> p.getValue0().equals(card.getSuit()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Card not found"));
    }

    @Override
    public Integer getRankOrder(Card card) {
        return VALUES.indexOf(getPairFor(card));
    }

    @Override
    public Integer getPoints(Card card) {
        return getPairFor(card).getValue1();
    }
}
