package io.mannsgoggel.gamejass.domain.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

import static java.util.stream.Collectors.toUnmodifiableList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    Color color;
    Suit suit;

    public enum Color {
        HEARTHS, SPADES, DIAMONDS, CLUBS
    }

    public enum Suit {
        ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX
    }

    public static class CardDeckBuilder {
        public static List<Card> build() {
            List<Card> cards = new ArrayList<>();

            List.of(Color.values()).forEach(color -> {
                Set.of(Suit.values()).forEach(suit -> {
                    cards.add(new Card(color, suit));
                });
            });

            return cards;
        }

        public static CardHandout buildAndShuffle(List<String> players) {
            List<Card> cards = Card.CardDeckBuilder.build();

            Collections.shuffle(cards);

            List<CardState> cardState = new ArrayList<>();

            for (int i = 0; i < players.size(); i++) {
                for (int j = i * 9; j < (i + 1) * 9; j++) {
                    cardState.add(
                            CardState.builder()
                                    .card(cards.get(j))
                                    .player(players.get(i))
                                    .build()
                    );
                }
            }

            cardState = cardState.stream()
                    .sorted(Comparator.comparingInt(a -> a.getCard().getSuit().ordinal()))
                    .sorted(Comparator.comparingInt(a -> a.getCard().getColor().ordinal()))
                    .collect(toUnmodifiableList());

            return new CardHandout(cardState);
        }
    }
}
