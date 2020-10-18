package com.manoggeli.gamejass.domain.mode.trump;

import com.manoggeli.gamejass.domain.gameObjects.Card;
import com.manoggeli.gamejass.domain.gameObjects.CardValues;
import org.javatuples.Triplet;

import java.util.List;

import static com.manoggeli.gamejass.domain.gameObjects.Card.Suit.*;

public class TrumpCardValues implements CardValues {

    private static final List<Triplet<Boolean, Card.Suit, Integer>> VALUES = List.of(
            Triplet.with(false, SIX, 0),
            Triplet.with(false, SEVEN, 0),
            Triplet.with(false, EIGHT, 0),
            Triplet.with(false, NINE, 0),
            Triplet.with(false, TEN, 10),
            Triplet.with(false, JACK, 2),
            Triplet.with(false, QUEEN, 3),
            Triplet.with(false, KING, 4),
            Triplet.with(false, ACE, 11),
            Triplet.with(true, SIX, 0),
            Triplet.with(true, SEVEN, 0),
            Triplet.with(true, EIGHT, 0),
            Triplet.with(true, TEN, 10),
            Triplet.with(true, QUEEN, 3),
            Triplet.with(true, KING, 4),
            Triplet.with(true, ACE, 11),
            Triplet.with(true, NINE, 14),
            Triplet.with(true, JACK, 20)
    );

    private Triplet<Boolean, Card.Suit, Integer> getTripletFor(Card card) {
        return VALUES.stream()
                .filter(p -> p.getValue0().equals(card.isTrump()) && p.getValue1().equals(card.getSuit()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Card not found"));
    }


    @Override
    public Integer getRankOrder(Card card) {
        return VALUES.indexOf(getTripletFor(card));
    }

    @Override
    public Integer getPoints(Card card) {
        return getTripletFor(card).getValue2();
    }
}
