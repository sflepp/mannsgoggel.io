package com.manoggeli.gamejass.domain.game;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.List;
import java.util.Set;
import java.util.Stack;

import static com.manoggeli.gamejass.domain.game.Card.Color.*;
import static com.manoggeli.gamejass.domain.game.Card.Suit.*;

public interface GameMode {
    enum PlayingMode {
        TOP_DOWN, BOTTOM_UP, TRUMP_HEARTHS, TRUMP_SPADES, TRUMP_DIAMONDS, TRUMP_CLUBS
    }

    Integer getRankOrder(Card card);

    Integer getPoints(Card card);

    Card winningCard(Stack<Card> card);

    Card higherCard(Card a, Card b);

    Boolean canPlayCard(Card card, Stack<Card> tableStack, Set<Card> playerCards);

    class Builder {
        static GameMode build(PlayingMode mode) {
            return switch (mode) {
                case BOTTOM_UP -> new GameMode.BottomUp();
                case TOP_DOWN -> new GameMode.TopDown();
                case TRUMP_HEARTHS -> new GameMode.Trump(HEARTHS);
                case TRUMP_SPADES -> new GameMode.Trump(SPADES);
                case TRUMP_DIAMONDS -> new GameMode.Trump(DIAMONDS);
                case TRUMP_CLUBS -> new GameMode.Trump(CLUBS);
            };
        }
    }

    class BottomUp implements GameMode {

        private static final List<Pair<Card.Suit, Integer>> VALUES = List.of(
                Pair.with(ACE, 0),
                Pair.with(KING, 4),
                Pair.with(QUEEN, 3),
                Pair.with(JACK, 2),
                Pair.with(TEN, 10),
                Pair.with(NINE, 0),
                Pair.with(EIGHT, 8),
                Pair.with(SEVEN, 0),
                Pair.with(SIX, 11)
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

        @Override
        public Card winningCard(Stack<Card> cards) {
            var firstCardColor = cards.get(0).getColor();

            return cards.stream()
                    .reduce((highestCard, card)
                            -> card.getColor() == firstCardColor ? higherCard(highestCard, card) : highestCard)
                    .orElseThrow(() -> new RuntimeException("No cards in stack"));
        }

        @Override
        public Card higherCard(Card a, Card b) {
            return getRankOrder(a) > getRankOrder(b) ? a : b;
        }
    }

    class TopDown implements GameMode {

        private static final List<Pair<Card.Suit, Integer>> VALUES = List.of(
                Pair.with(SIX, 0),
                Pair.with(SEVEN, 0),
                Pair.with(EIGHT, 8),
                Pair.with(NINE, 0),
                Pair.with(TEN, 10),
                Pair.with(JACK, 2),
                Pair.with(QUEEN, 3),
                Pair.with(KING, 4),
                Pair.with(ACE, 11)
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

        @Override
        public Card winningCard(Stack<Card> cards) {
            var firstCardColor = cards.get(0).getColor();

            return cards.stream()
                    .reduce((highestCard, card)
                            -> card.getColor() == firstCardColor ? higherCard(highestCard, card) : highestCard)
                    .orElseThrow(() -> new RuntimeException("No cards in stack"));
        }

        @Override
        public Card higherCard(Card a, Card b) {
            return getRankOrder(a) > getRankOrder(b) ? a : b;
        }
    }

    class Trump implements GameMode {

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

        private final Card.Color trumpColor;

        public Trump(Card.Color trumpColor) {
            this.trumpColor = trumpColor;
        }

        @Override
        public Integer getRankOrder(Card card) {
            return VALUES.indexOf(getTripletFor(card));
        }

        @Override
        public Integer getPoints(Card card) {
            return getTripletFor(card).getValue2();
        }

        @Override
        public Card higherCard(Card a, Card b) {
            return getRankOrder(a) > getRankOrder(b) ? a : b;
        }

        @Override
        public Card winningCard(Stack<Card> cards) {
            var firstCardColor = cards.get(0).getColor();

            return cards.stream()
                    .reduce((highestCard, card)
                            -> card.getColor() == firstCardColor || isTrumpCard(card) ? higherCard(highestCard, card) : highestCard)
                    .orElseThrow(() -> new RuntimeException("No cards in stack"));

        }

        private boolean isTrumpCard(Card card) {
            return card.getColor().equals(trumpColor);
        }

        private Triplet<Boolean, Card.Suit, Integer> getTripletFor(Card card) {
            return VALUES.stream()
                    .filter(p -> p.getValue0().equals(isTrumpCard(card)) && p.getValue1().equals(card.getSuit()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Card not found"));
        }
    }
}
