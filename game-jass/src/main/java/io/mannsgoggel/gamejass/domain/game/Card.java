package io.mannsgoggel.gamejass.domain.game;

import lombok.Value;

import java.util.*;

@Value
public class Card {
    Color color;
    Suit suit;

    public enum Color {
        HEARTHS, SPADES, DIAMONDS, CLUBS, HIDDEN
    }

    public enum Suit {
        ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX, HIDDEN
    }

    public PlayedCard play(String player) {
        return new PlayedCard(player, this);
    }

    public PlayedCard play() {
        return new PlayedCard("", this);
    }

    public Card hide() {
        return new Card(Color.HIDDEN, Suit.HIDDEN);
    }

    public static class CardDeckBuilder {
        public static List<Card> build() {
            List<Card> cards = new ArrayList<>();

            List.of(Card.Color.values()).stream().filter(v -> !v.equals(Color.HIDDEN)).forEach(color -> {
                Set.of(Card.Suit.values()).stream().filter(v -> !v.equals(Suit.HIDDEN)).forEach(suit -> {
                    cards.add(new Card(color, suit));
                });
            });

            return cards;
        }

        public static Map<String, List<Card>> buildAndShuffleFor(List<Player> players) {
            List<Card> cards = Card.CardDeckBuilder.build();

            Collections.shuffle(cards);

            Map<String, List<Card>> cardsPerPlayer = new HashMap<>();

            for (int i = 0; i < players.size(); i++) {
                cardsPerPlayer.put(
                        players.get(i).getName(),
                        cards.subList(i * 9, (i + 1) * 9)
                );
            }

            return cardsPerPlayer;
        }
    }
}
