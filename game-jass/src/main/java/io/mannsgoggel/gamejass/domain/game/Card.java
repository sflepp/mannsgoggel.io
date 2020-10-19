package io.mannsgoggel.gamejass.domain.game;

import java.util.*;

public class Card {
    public enum Color {
        HEARTHS, SPADES, DIAMONDS, CLUBS
    }

    public enum Suit {
        ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX
    }

    private final Color color;
    private final Suit suit;

    public Card(Color color, Suit suit) {
        this.color = color;
        this.suit = suit;
    }

    public Color getColor() {
        return color;
    }

    public Suit getSuit() {
        return suit;
    }

    public static class CardDeckBuilder {
        public static List<Card> build() {
            List<Card> cards = new ArrayList<>();

            List.of(Card.Color.values()).forEach(color -> {
                Set.of(Card.Suit.values()).forEach(suit -> {
                    cards.add(new Card(color, suit));
                });
            });

            return cards;
        }

        public static Map<String, Set<Card>> buildAndShuffleFor(List<Player> players) {
            List<Card> cards = Card.CardDeckBuilder.build();

            Collections.shuffle(cards);

            Map<String, Set<Card>> cardsPerPlayer = new HashMap<>();

            for (int i = 0; i < players.size(); i++) {
                cardsPerPlayer.put(
                        players.get(i).getName(),
                        Set.copyOf(cards.subList(i * 8, (i + 1) * 8 - 1))
                );
            }

            return cardsPerPlayer;
        }
    }
}
