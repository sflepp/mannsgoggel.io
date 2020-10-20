package io.mannsgoggel.gamejass.domain.game;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.List;
import java.util.stream.Collectors;

public interface GameMode {
    Integer getRankOrder(Card card);

    Integer getPoints(Card card);

    PlayedCard winningCard(List<PlayedCard> tableStack);

    Card higherCard(Card a, Card b);

    PlayedCard higherCard(PlayedCard a, PlayedCard b);

    List<Card> playableCards(List<Card> handCards, List<PlayedCard> tableStack);

    enum PlayingMode {
        TOP_DOWN, BOTTOM_UP, TRUMP_HEARTHS, TRUMP_SPADES, TRUMP_DIAMONDS, TRUMP_CLUBS
    }

    class Builder {
        static GameMode build(PlayingMode mode) {
            return switch (mode) {
                case BOTTOM_UP -> new GameMode.BottomUp();
                case TOP_DOWN -> new GameMode.TopDown();
                case TRUMP_HEARTHS -> new GameMode.Trump(Card.Color.HEARTHS);
                case TRUMP_SPADES -> new GameMode.Trump(Card.Color.SPADES);
                case TRUMP_DIAMONDS -> new GameMode.Trump(Card.Color.DIAMONDS);
                case TRUMP_CLUBS -> new GameMode.Trump(Card.Color.CLUBS);
            };
        }
    }

    class BottomUp implements GameMode {

        private static final List<Pair<Card.Suit, Integer>> VALUES = List.of(
                Pair.with(Card.Suit.ACE, 0),
                Pair.with(Card.Suit.KING, 4),
                Pair.with(Card.Suit.QUEEN, 3),
                Pair.with(Card.Suit.JACK, 2),
                Pair.with(Card.Suit.TEN, 10),
                Pair.with(Card.Suit.NINE, 0),
                Pair.with(Card.Suit.EIGHT, 8),
                Pair.with(Card.Suit.SEVEN, 0),
                Pair.with(Card.Suit.SIX, 11)
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
        public PlayedCard winningCard(List<PlayedCard> tableStack) {
            var firstCardColor = tableStack.get(0).getCard().getColor();

            return tableStack.stream()
                    .reduce((highestCard, card)
                            -> card.getCard().getColor() == firstCardColor ? higherCard(highestCard, card) : highestCard)
                    .orElseThrow(() -> new RuntimeException("No cards in stack"));
        }

        @Override
        public Card higherCard(Card a, Card b) {
            return getRankOrder(a) > getRankOrder(b) ? a : b;
        }

        @Override
        public PlayedCard higherCard(PlayedCard a, PlayedCard b) {
            return getRankOrder(a.getCard()) > getRankOrder(b.getCard()) ? a : b;
        }

        @Override
        public List<Card> playableCards(List<Card> handCards, List<PlayedCard> tableStack) {
            if (tableStack.isEmpty()) {
                return handCards;
            }

            var firstCardColor = tableStack.get(0).getCard().getColor();

            var validCards = handCards.stream()
                    .filter(card -> card.getColor().equals(firstCardColor))
                    .collect(Collectors.toList());

            return validCards.size() == 0 ? handCards : validCards;
        }
    }

    class TopDown implements GameMode {

        private static final List<Pair<Card.Suit, Integer>> VALUES = List.of(
                Pair.with(Card.Suit.SIX, 0),
                Pair.with(Card.Suit.SEVEN, 0),
                Pair.with(Card.Suit.EIGHT, 8),
                Pair.with(Card.Suit.NINE, 0),
                Pair.with(Card.Suit.TEN, 10),
                Pair.with(Card.Suit.JACK, 2),
                Pair.with(Card.Suit.QUEEN, 3),
                Pair.with(Card.Suit.KING, 4),
                Pair.with(Card.Suit.ACE, 11)
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
        public PlayedCard winningCard(List<PlayedCard> tableStack) {
            var firstCardColor = tableStack.get(0).getCard().getColor();

            return tableStack.stream()
                    .reduce((highestCard, card)
                            -> card.getCard().getColor() == firstCardColor ? higherCard(highestCard, card) : highestCard)
                    .orElseThrow(() -> new RuntimeException("No cards in stack"));
        }

        @Override
        public Card higherCard(Card a, Card b) {
            return getRankOrder(a) > getRankOrder(b) ? a : b;
        }

        @Override
        public PlayedCard higherCard(PlayedCard a, PlayedCard b) {
            return getRankOrder(a.getCard()) > getRankOrder(b.getCard()) ? a : b;
        }

        @Override
        public List<Card> playableCards(List<Card> handCards, List<PlayedCard> tableStack) {
            if (tableStack.isEmpty()) {
                return handCards;
            }

            var firstCardColor = tableStack.get(0).getCard().getColor();

            var validCards = handCards.stream()
                    .filter(card -> card.getColor().equals(firstCardColor))
                    .collect(Collectors.toList());

            return validCards.size() == 0 ? handCards : validCards;
        }
    }

    class Trump implements GameMode {

        private static final List<Triplet<Boolean, Card.Suit, Integer>> VALUES = List.of(
                Triplet.with(false, Card.Suit.SIX, 0),
                Triplet.with(false, Card.Suit.SEVEN, 0),
                Triplet.with(false, Card.Suit.EIGHT, 0),
                Triplet.with(false, Card.Suit.NINE, 0),
                Triplet.with(false, Card.Suit.TEN, 10),
                Triplet.with(false, Card.Suit.JACK, 2),
                Triplet.with(false, Card.Suit.QUEEN, 3),
                Triplet.with(false, Card.Suit.KING, 4),
                Triplet.with(false, Card.Suit.ACE, 11),
                Triplet.with(true, Card.Suit.SIX, 0),
                Triplet.with(true, Card.Suit.SEVEN, 0),
                Triplet.with(true, Card.Suit.EIGHT, 0),
                Triplet.with(true, Card.Suit.TEN, 10),
                Triplet.with(true, Card.Suit.QUEEN, 3),
                Triplet.with(true, Card.Suit.KING, 4),
                Triplet.with(true, Card.Suit.ACE, 11),
                Triplet.with(true, Card.Suit.NINE, 14),
                Triplet.with(true, Card.Suit.JACK, 20)
        );

        private final Card.Color trumpColor;

        Trump(Card.Color trumpColor) {
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
        public PlayedCard higherCard(PlayedCard a, PlayedCard b) {
            return getRankOrder(a.getCard()) > getRankOrder(b.getCard()) ? a : b;
        }

        @Override
        public List<Card> playableCards(List<Card> handCards, List<PlayedCard> tableStack) {
            if (tableStack.isEmpty()) {
                return List.copyOf(handCards);
            }

            var firstCardColor = tableStack.get(0).getCard().getColor();

            var validCards = handCards.stream()
                    .filter(card -> card.getColor().equals(firstCardColor)
                            || (isTrumpCard(card) && higherCard(card, winningCard(tableStack).getCard()).equals(card)))
                    .collect(Collectors.toList());

            return validCards.size() == 0
                    || (validCards.size() == 1 && validCards.contains(new Card(trumpColor, Card.Suit.JACK)))
                    ? handCards : validCards;
        }

        @Override
        public PlayedCard winningCard(List<PlayedCard> tableStack) {
            var firstCardColor = tableStack.get(0).getCard().getColor();

            return tableStack.stream()
                    .reduce((highestCard, card)
                            -> card.getCard().getColor() == firstCardColor || isTrumpCard(card.getCard()) ? higherCard(highestCard, card) : highestCard)
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
